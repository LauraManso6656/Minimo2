package com.example.loginregister.Swagger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainRepositoryShop {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public LiveData<List<ShopItem>> loadShopItems() {
        MutableLiveData<List<ShopItem>> listData = new MutableLiveData<>();
        firebaseDatabase.getReference("ShopItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ShopItem> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ShopItem item = childSnapshot.getValue(ShopItem.class);
                    if (item != null) {
                        list.add(item);
                    }
                }
                listData.setValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejo de errores si es necesario
            }
        });

        return listData;
    }
}
