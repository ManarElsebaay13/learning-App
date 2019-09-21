package com.armo.client.model.youtube;


public class Snippet {

    public String title;
    public String description;
    public ResourceId resourceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Snippet snippet = (Snippet) o;

        if (title != null ? !title.equals(snippet.title) : snippet.title != null) return false;
        if (description != null ? !description.equals(snippet.description) : snippet.description != null)
            return false;
        return resourceId != null ? resourceId.equals(snippet.resourceId) : snippet.resourceId == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        return result;
    }
}
