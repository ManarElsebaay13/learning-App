package com.armo.client.model.youtube;



public class PlayListItem {

    public String id;
    public Snippet snippet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayListItem that = (PlayListItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return snippet != null ? snippet.equals(that.snippet) : that.snippet == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (snippet != null ? snippet.hashCode() : 0);
        return result;
    }
}
