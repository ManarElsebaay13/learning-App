package com.armo.robot.model.firebase;

import com.armo.robot.utils.Constants;


public class PlayVideoCommand extends Command {

    public String videoId;

    public PlayVideoCommand() {
        super(Constants.ACTION_PLAY_VIDEO);
    }

    public PlayVideoCommand(String videoId) {
        super(Constants.ACTION_PLAY_VIDEO);
        this.videoId = videoId;
    }
}
