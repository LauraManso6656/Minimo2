package com.example.loginregister.Swagger;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShopItem implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String name;

    @SerializedName("descripcion")
    private String description;

    @SerializedName("url_icon")
    private String url_icon;

    @SerializedName("precio")
    private int price;

    public ShopItem(String name, String description, String url_icon, int price, int id) {
        this.name = name;
        this.description = description;
        this.url_icon = url_icon;
        this.price = price;
        this.id = id;
    }

    public ShopItem() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl_icon() { return url_icon; }
    public void setUrl_icon(String url_icon) { this.url_icon = url_icon; }
    public void setPrice(int price) {this.price = price;}
    public int getPrice() {return price;}
    public void setId(int id){this.id = id;}
    public int getId(){return this.id;}
}