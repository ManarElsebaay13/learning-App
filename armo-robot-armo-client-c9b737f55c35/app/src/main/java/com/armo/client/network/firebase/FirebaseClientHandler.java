package com.armo.client.network.firebase;

import android.os.Build;

import com.armo.client.model.firebase.Client;
import com.armo.client.model.firebase.Command;
import com.armo.client.model.firebase.Robot;
import com.armo.client.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.armo.client.utils.Constants.FIREBASE_CLIENT_COMMANDS;
import static com.armo.client.utils.Constants.FIREBASE_COMMANDS;


public class FirebaseClientHandler {


    private static FirebaseClientHandler instance;

    private FirebaseUser firebaseUser;
    private ConcurrentHashMap<String, WeakReference<ConnectionListener>> connectionListeners;
    private String robotID;
    private Robot robot;
    private boolean isConnected;
    private ValueEventListener robotConnectionListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (getClientId() != null && getClientId().equals(dataSnapshot.getValue(String.class))) {
                onConnected();
            } else {
                onDisconnected();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            onDisconnected();
        }
    };

    private ValueEventListener robotDetailsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Robot robot = dataSnapshot.getValue(Robot.class);
            updateConnectedRobot(robot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private FirebaseClientHandler() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener((firebaseAuth) ->
                firebaseUser = firebaseAuth.getCurrentUser());
        connectionListeners = new ConcurrentHashMap<>();
    }

    public static FirebaseClientHandler getInstance() {
        if (instance == null) {
            synchronized (FirebaseClientHandler.class) {
                if (instance == null)
                    instance = new FirebaseClientHandler();
            }
        }

        return instance;
    }

    public boolean isSignedIn() {
        return firebaseUser != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getClientId() {
        return firebaseUser.getUid();
    }

    public void signin() {
        if (!isSignedIn()) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    firebaseUser = task.getResult().getUser();
                    updateClient(new Client(Build.MODEL));
                }
            });
        } else {
            updateClient(new Client(Build.MODEL));
        }
    }

    public void updateClient(Client client) {
        if (isSignedIn() && client != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CLIENTS);
            databaseReference.child(getClientId()).setValue(client);
            if (robotID == null) {
                checkConnectedRobots();
            }
        }
    }

    private void checkConnectedRobots() {
        DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
        commandsDatabaseReference.orderByChild(Constants.FIREBASE_CLIENT_ID).equalTo(getClientId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() instanceof HashMap) {
                    Object key = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next();
                    if (key instanceof String) {
                        connectToRobot((String) key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void connectToRobot(String robotID) {
        if (isSignedIn()) {
            final String oldRobotID = this.robotID;
            this.robotID = robotID;
            DatabaseReference robotsDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ROBOTS);
            robotsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(robotID)) {
                        DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
                        if (oldRobotID != null)
                            commandsDatabaseReference.child(oldRobotID).child(Constants.FIREBASE_CLIENT_ID).removeEventListener(robotConnectionListener);
                        commandsDatabaseReference.child(robotID).child(Constants.FIREBASE_CLIENT_ID).addValueEventListener(robotConnectionListener);
                        commandsDatabaseReference.child(robotID).child(Constants.FIREBASE_CLIENT_ID).setValue(getClientId());

                        robotsDatabaseReference.child(robotID).removeEventListener(robotDetailsListener);
                        robotsDatabaseReference.child(robotID).addValueEventListener(robotDetailsListener);
                    } else {
                        for (WeakReference<ConnectionListener> connectionListenerReference : FirebaseClientHandler.this.connectionListeners.values()) {
                            ConnectionListener connectionListener = connectionListenerReference.get();
                            if (connectionListener != null) {
                                connectionListener.onInvalidRobotScanned();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    void onConnected() {
        isConnected = true;
        for (WeakReference<ConnectionListener> connectionListenerReference : this.connectionListeners.values()) {
            ConnectionListener connectionListener = connectionListenerReference.get();
            if (connectionListener != null) {
                connectionListener.onConnected();
            }
        }
    }


    void onDisconnected() {
        isConnected = false;
        DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
        commandsDatabaseReference.child(robotID).child(Constants.FIREBASE_CLIENT_ID).removeEventListener(robotConnectionListener);
        robotID = null;
        robot = null;
        for (WeakReference<ConnectionListener> connectionListenerReference : this.connectionListeners.values()) {
            ConnectionListener connectionListener = connectionListenerReference.get();
            if (connectionListener != null) {
                connectionListener.onDisconnected();
            }
        }
    }

    void updateConnectedRobot(Robot robot) {
        if (isConnected()) {
            this.robot = robot;
        }
    }

    public String getStreamUrl() {
        return robot == null ? null : robot.streamUrl;
    }

    public void disconnect() {
        DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
        commandsDatabaseReference.child(this.robotID).removeValue();
    }

    public void sendCommand(Command command) {
        if (isConnected) {
            DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
            commandsDatabaseReference.child(robotID).child(FIREBASE_CLIENT_COMMANDS).push().setValue(command);
        }
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null) {
            this.connectionListeners.put(connectionListener.getClass().getSimpleName(), new WeakReference<>(connectionListener));
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null) {
            this.connectionListeners.remove(connectionListener.getClass().getSimpleName());
        }
    }
}
