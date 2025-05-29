package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;


public class Shop {

    List<ShopItem> items;

    public Shop(List<ShopItem> Items) {
        items = Items;
    }

    public void addShopItem(ShopItem item){
        items.add(item);
    }

    public List<ShopItem> getAllShopItems(){
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }
}
