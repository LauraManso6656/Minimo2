package com.example.loginregister;

import android.util.Log;

import com.example.loginregister.Swagger.ShopItem;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static final Map<ShopItem, Integer> cartItems = new HashMap<>();

    // Agregar un producto con la cantidad deseada
    public static void addItem(ShopItem item, int cantidad) {
        if (cartItems.containsKey(item)) {
            cartItems.put(item, cartItems.get(item) + cantidad); // Suma la cantidad si ya existe
        } else {
            cartItems.put(item, cantidad); // Agrega un nuevo producto al carrito
        }
    }

    // Obtener el carrito con productos y cantidades
    public static Map<ShopItem, Integer> getCartItems() {
        return cartItems;
    }
    // Eliminar un producto del carrito
    public static void deleteItem(ShopItem item) {
        if (cartItems.containsKey(item)) {
            cartItems.remove(item);
            Log.d("CartManager", "Producto eliminado: " + item.getName());
        } else {
            Log.w("CartManager", "Intento de eliminar producto inexistente: " + item.getName());
        }
    }


    // Limpiar el carrito (por si el usuario quiere vaciarlo)
    public static void clearCart() {
        cartItems.clear();
    }
}