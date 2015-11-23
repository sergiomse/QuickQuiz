package com.sergiomse.quickquiz.model;

import java.util.List;

/**
 * Created by sergio on 07/11/2015.
 */
public class Folder {

    private long id;
    private String name;
    private long parentId;
    private boolean isRoot;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
}
