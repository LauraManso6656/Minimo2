package com.example.loginregister;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.loginregister.R;
import com.example.loginregister.Swagger.ShopItem;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTxt, priceTxt, descriptionTxt, cantidadTxt;
    private ImageView picMain;

    private int cantidad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Referencias a los elementos del layout
        titleTxt = findViewById(R.id.TitleTxt);
        priceTxt = findViewById(R.id.priceDetailTxt);
        descriptionTxt = findViewById(R.id.textView9);
        picMain = findViewById(R.id.picMain);
        cantidadTxt = findViewById(R.id.cantidadTxt);
        this.cantidad = Integer.parseInt(cantidadTxt.getText().toString());

        // Obtener el objeto ShopItem enviado desde el intent
        ShopItem item = (ShopItem) getIntent().getSerializableExtra("item");

        if (item != null) {
            titleTxt.setText(item.getName());
            priceTxt.setText(item.getPrice() + "$");
            descriptionTxt.setText(item.getDescription());

            // Cargar la imagen con Glide
            Glide.with(this)
                    .load(item.getUrl_icon())
                    .into(picMain);
        }
    }
    public void plusQuantityClick(View view){
        this.cantidad++;
        cantidadTxt.setText(String.valueOf(cantidad));
    }


    public void addToCart(View view) {
        ShopItem item = (ShopItem) getIntent().getSerializableExtra("item");

        if (item != null) {
            com.example.loginregister.CartManager.addItem(item, cantidad); // Agregar al carrito

            // Mostrar mensaje flotante de confirmación
            Toast.makeText(this, "¡Item añadido al carrito!", Toast.LENGTH_SHORT).show();
        }
    }
    public void minusQuantityClick(View view){
        if (cantidad > 1) { // Evita que la cantidad llegue a 0
            this.cantidad--;
            cantidadTxt.setText(String.valueOf(cantidad));
        }
    }
    public void onClickBack(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left); // Asegúrate de tener estos archivos en res/anim
    }

    private void overridePendingTransition(int slideInLeft) {
    }


}