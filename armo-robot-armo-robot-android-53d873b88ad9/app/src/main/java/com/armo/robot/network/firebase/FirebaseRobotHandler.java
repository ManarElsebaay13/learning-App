package com.armo.robot.network.firebase;

import android.os.Build;

import com.armo.robot.model.firebase.CloseCameraCommand;
import com.armo.robot.model.firebase.OpenCameraCommand;
import com.armo.robot.model.firebase.PlayVideoCommand;
import com.armo.robot.model.firebase.Robot;
import com.armo.robot.model.firebase.StopVideoCommand;
import com.armo.robot.utils.Constants;
import com.armo.robot.utils.OnCommandListener;
import com.armo.robot.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;

import static com.armo.robot.utils.Constants.FIREBASE_CLIENT_COMMANDS;
import static com.armo.robot.utils.Constants.FIREBASE_COMMANDS;


public class FirebaseRobotHandler {


    private static FirebaseRobotHandler instance;
    private FirebaseUser firebaseUser;
    private WeakReference<OnCommandListener> onCommandListener;
    private ChildEventListener childEventListener;
    private String clientId;
    private String ipAddress;
    private ValueEventListener clientConnectionListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String clientId = dataSnapshot.getValue(String.class);
            if (clientId != null) {
                onClientConnected(clientId);
            } else {
                onClientDisconnected();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private FirebaseRobotHandler() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener((firebaseAuth) ->
                firebaseUser = firebaseAuth.getCurrentUser());
        onCommandListener = new WeakReference<>(null);
    }

    public static FirebaseRobotHandler getInstance() {
        if (instance == null) {
            synchronized (FirebaseRobotHandler.class) {
                if (instance == null)
                    instance = new FirebaseRobotHandler();
            }
        }

        return instance;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRobotId() {
        return isSignedIn() ? firebaseUser.getUid() : null;
    }

    void onClientConnected(String clientId) {
        this.clientId = clientId;
        listenToClientCommands();
        OnCommandListener onCommandListener = this.onCommandListener.get();
        if (onCommandListener != null) {
            onCommandListener.onClientConnected();
        }
    }

    void onClientDisconnected() {
        clientId = null;
        OnCommandListener onCommandListener = this.onCommandListener.get();
        if (onCommandListener != null) {
            onCommandListener.onClientDisconnected();
        }
    }

    public boolean isSignedIn() {
        return firebaseUser != null;
    }

    public boolean isClientConnected() {
        return clientId != null;
    }

    public void signin() {
        if (!isSignedIn()) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    firebaseUser = task.getResult().getUser();
                    updateRobot(new Robot(Build.MODEL, Utils.getStreamUrl(ipAddress)));
                }
            });
        } else {
            updateRobot(new Robot(Build.MODEL, Utils.getStreamUrl(ipAddress)));
        }
    }

    public void disconnect() {
        if (isClientConnected()) {
            DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_COMMANDS);
            commandsDatabaseReference.child(getRobotId()).removeValue();
        }
    }

    public void updateRobot(Robot robot) {
        if (isSignedIn() && robot != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ROBOTS);
            databaseReference.child(getRobotId()).setValue(robot);
            listenToClientConnection();
        }
    }

    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = new WeakReference<>(onCommandListener);
    }

    void listenToClientConnection() {
        if (isSignedIn()) {
            DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS);
            commandsDatabaseReference.child(getRobotId()).child(Constants.FIREBASE_CLIENT_ID).removeEventListener(clientConnectionListener);
            commandsDatabaseReference.child(getRobotId()).child(Constants.FIREBASE_CLIENT_ID).addValueEventListener(clientConnectionListener);
        }
    }

    void listenToClientCommands() {
        if (isSignedIn()) {
            DatabaseReference commandsDatabaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_COMMANDS).child(getRobotId()).child(FIREBASE_CLIENT_COMMANDS);
            if (this.onCommandListener.get() == null) {
                if (childEventListener != null)
                    commandsDatabaseReference.removeEventListener(childEventListener);
                childEventListener = null;
            } else {

                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                        try {
                            OnCommandListener onCommandListener = FirebaseRobotHandler.this.onCommandListener.get();
                            if (onCommandListener != null && dataSnapshot.child(Constants.FIREBASE_COMMAND_ACTION).getValue() instanceof Number) {
                                switch (((Number) dataSnapshot.child(Constants.FIREBASE_COMMAND_ACTION).getValue()).intValue()) {
                                    case Constants.ACTION_PLAY_VIDEO:
                                        onCommandListener.onCommand(dataSnapshot.getValue(PlayVideoCommand.class));
                                        break;
                                    case Constants.ACTION_STOP_VIDEO:
                                        onCommandListener.onCommand(dataSnapshot.getValue(StopVideoCommand.class));
                                        break;
                                    case Constants.ACTION_OPEN_CAMERA:
                                        onCommandListener.onCommand(dataSnapshot.getValue(OpenCameraCommand.class));
                                        break;
                                    case Constants.ACTION_CLOSE_CAMERA:
                                        onCommandListener.onCommand(dataSnapshot.getValue(CloseCameraCommand.class));
                                        break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                commandsDatabaseReference.orderByChild(Constants.FIREBASE_COMMAND_TIMESTAMP).startAt(System.currentTimeMillis() / 1000)
                        .addChildEventListener(childEventListener);
            }
        }
    }
}
