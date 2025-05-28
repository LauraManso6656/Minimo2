package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Swagger.API;
import com.example.loginregister.Swagger.AuthService;
import com.example.loginregister.Swagger.ShopAdapter;
import com.example.loginregister.Swagger.ShopItem;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopAdapter adapter;
    private List<ShopItem> allShopItemsMaster; // Lista maestra con todos los ítems
    private List<ShopItem> displayedShopItems; // Lista que usa el adaptador para mostrar ítems
    private ProgressBar progressBarItems;
    private ProgressBar progressBarBanner;

    int money;

    public void hideProgressBars() {
        if (progressBarItems != null) {
            progressBarItems.setVisibility(View.GONE);
        }
        if (progressBarBanner != null) {
            progressBarBanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        EditText searchBox = findViewById(R.id.editTextText);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarItems = findViewById(R.id.progressBarItems);
        progressBarBanner = findViewById(R.id.progressBarBanner); // Asegúrate que este ID existe en tu layout activity_shop.xml

        ImageView bannerImage = findViewById(R.id.banner);
        bannerImage.setImageResource(R.drawable.tienda_tocabolas_banner);

        // Mostrar progress bars al inicio
        if (progressBarItems != null) progressBarItems.setVisibility(View.VISIBLE);
        if (progressBarBanner != null) progressBarBanner.setVisibility(View.VISIBLE);


        allShopItemsMaster = new ArrayList<>();
        displayedShopItems = new ArrayList<>(); // Esta es la lista que el adaptador usará
        adapter = new ShopAdapter(displayedShopItems); // El adaptador usa la lista 'displayedShopItems'
        recyclerView.setAdapter(adapter);

        getShopItems();

        // Manejo seguro de la conversión a entero
        String moneyString = getIntent().getStringExtra("money");
        if (moneyString != null && !moneyString.isEmpty()) {
            try {
                this.money = Integer.parseInt(moneyString);
            } catch (NumberFormatException e) {
                Log.e("ShopActivity", "Error al convertir 'money' a entero: " + moneyString, e);
                this.money = 0; // Asigna un valor por defecto si hay error
            }
        } else {
            Log.e("ShopActivity", "El valor de 'money' es nulo o vacío.");
            this.money = 0; // Valor por defecto si el dato es nulo o vacío
        }
    }

    private void filterItems(String query) {
        List<ShopItem> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(allShopItemsMaster); // Si la búsqueda está vacía, mostrar todos
        } else {
            for (ShopItem item : allShopItemsMaster) { // Siempre filtrar desde la lista maestra
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            // Si la búsqueda no está vacía y no se encontraron resultados, mostrar todos
            if (filteredList.isEmpty()) {
                filteredList.addAll(allShopItemsMaster);
            }
        }
        adapter.updateList(filteredList); // Actualizar el adaptador con la lista resultante
    }

    private void getShopItems() {
        if (progressBarItems != null) progressBarItems.setVisibility(View.VISIBLE);

        AuthService authService = API.getAuthService();
        authService.getShopItems().enqueue(new Callback<List<ShopItem>>() {
            @Override
            public void onResponse(Call<List<ShopItem>> call, Response<List<ShopItem>> response) {
                if (progressBarItems != null) progressBarItems.setVisibility(View.GONE); // Ocultar siempre, haya éxito o no

                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("API", "No se pudieron obtener los productos. Código: " + response.code());
                    // Opcionalmente, mostrar un mensaje al usuario
                    hideProgressBars(); // Asegurarse de ocultar si no se hizo antes
                    return;
                }

                allShopItemsMaster.clear();
                allShopItemsMaster.addAll(response.body());

                // Inicialmente, mostrar todos los ítems.
                // updateList se encarga de actualizar 'displayedShopItems' y notificar al adaptador.
                adapter.updateList(new ArrayList<>(allShopItemsMaster)); // Pasar una copia
                hideProgressBars(); // Ocultar progressBars después de cargar los ítems
            }

            @Override
            public void onFailure(Call<List<ShopItem>> call, Throwable t) {
                Log.e("API", "Error al obtener productos: " + t.getMessage(), t);
                if (progressBarItems != null) progressBarItems.setVisibility(View.GONE);
                hideProgressBars(); // Asegurarse de ocultar en caso de fallo
                // Opcionalmente, mostrar un mensaje de error al usuario
            }
        });
    }

    public void itemDetailClick (View view){
        try {
            // Es más robusto obtener el RecyclerView directamente por su ID
            RecyclerView currentRecyclerView = findViewById(R.id.recyclerViewItems);
            View itemView = (View) view.getParent().getParent(); // Asumiendo que 'view' es un hijo directo del layout del ViewHolder y este del ConstraintLayout/RelativeLayout principal del item.
            // Esto puede necesitar ajuste según tu R.layout.viewholder_shop

            if (currentRecyclerView != null && itemView != null) {
                int position = currentRecyclerView.getChildAdapterPosition(itemView);

                if (position != RecyclerView.NO_POSITION && adapter != null) {
                    ShopItem clickedItem = adapter.getItemAt(position); // Usar un método del adaptador

                    if (clickedItem != null) {
                        Intent intent = new Intent(this, DetailActivity.class);
                        // Asegúrate que DetailActivity espera "shopItem" y que ShopItem es Serializable o Parcelable
                        intent.putExtra("shopItem", clickedItem);
                        intent.putExtra("itemPosition", position); // Esta línea es opcional
                        startActivity(intent);
                    } else {
                        Log.w("ShopActivity", "No se pudo obtener el ítem en la posición: " + position);
                    }
                } else {
                    Log.w("ShopActivity", "Posición no válida o adaptador nulo. Posición: " + position);
                }
            } else {
                Log.e("ShopActivity", "RecyclerView o itemView no encontrado para itemDetailClick.");
            }
        } catch (Exception e) {
            Log.e("ShopActivity", "Error en itemDetailClick", e);
        }
    }

    public void cartClcik (View view){
        Intent intent = new Intent(ShopActivity.this, CartActivity.class);
        intent.putExtra("origen", "lobby"); // Asumo que esto es correcto para tu lógica
        String token = getIntent().getStringExtra("token");
        intent.putExtra("token", token);
        startActivity(intent);
    }

    public void lobbyClcik (View view){
        finish();
        // overridePendingTransition(R.anim.slide_in_left); // Descomenta si tienes estas animaciones
    }

}