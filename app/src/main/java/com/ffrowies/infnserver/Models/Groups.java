package com.ffrowies.infnserver.Models;

import java.util.List;

public class Groups {
    private String id;
    private String name;
    private String image;
    private List<Members> items;

    public Groups() {
    }

    public Groups(String id, String name, String image, List<Members> items) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Members> getItems() {
        return items;
    }

    public void setItems(List<Members> items) {
        this.items = items;
    }
}
