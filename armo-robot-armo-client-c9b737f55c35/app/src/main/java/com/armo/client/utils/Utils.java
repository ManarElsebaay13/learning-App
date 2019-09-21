package com.armo.client.utils;


public class Utils {




    public static String getVideoThumbnail(String videoId) {
        return String.format("https://i.ytimg.com/vi/%s/hqdefault.jpg", videoId);
    }
}
