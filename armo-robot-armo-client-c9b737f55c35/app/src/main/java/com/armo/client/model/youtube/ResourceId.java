package com.armo.client.model.youtube;


public class ResourceId {

    public String videoId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceId that = (ResourceId) o;

        return videoId != null ? videoId.equals(that.videoId) : that.videoId == null;

    }

    @Override
    public int hashCode() {
        return videoId != null ? videoId.hashCode() : 0;
    }
}
