package com.armo.client.model.firebase;

import com.armo.client.utils.Constants;


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
