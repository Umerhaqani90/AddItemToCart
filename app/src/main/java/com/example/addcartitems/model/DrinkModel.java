package com.example.addcartitems.model;

public class DrinkModel {
    private String key,name,image,price;

    public DrinkModel() {
    }

    public DrinkModel(String key, String name, String image, String price) {
        this.key = key;
        this.name = name;
        this.image = image;
        this.price = price;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
