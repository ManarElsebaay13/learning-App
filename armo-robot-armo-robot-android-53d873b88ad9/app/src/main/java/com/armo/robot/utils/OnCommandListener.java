package com.armo.robot.utils;

import com.armo.robot.model.firebase.Command;


public interface OnCommandListener {

   void onClientConnected();

   void onClientDisconnected();

   void onCommand(Command command);

}
