package com.armo.client.network.firebase;

public interface ConnectionListener {

   void onConnected();

   void onDisconnected();

   void onInvalidRobotScanned();
}
