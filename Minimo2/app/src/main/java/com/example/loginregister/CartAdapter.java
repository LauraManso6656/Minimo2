package com.example.loginregister;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginregister.Swagger.ShopItem;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Map.Entry<ShopItem, Integer>> cartItems;
    private TextView totalTxt;

    public CartAdapter(List<Map.Entry<ShopItem, Integer>> cartItems, TextView totalTxt) {

        this.cartItems = cartItems;
        this.totalTxt = totalTxt;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map.Entry<ShopItem, Integer> entry = cartItems.get(position);
        ShopItem item = entry.getKey();
        int cantidad = entry.getValue();

        holder.titleTxt.setText(item.getName());
        holder.priceTxt.setText(item.getPrice() * cantidad + "$");
        holder.quantityTxt.setText(String.valueOf(cantidad));

        Glide.with(holder.picMain.getContext())
                .load(item.getUrl_icon())
                .into(holder.picMain);

        // Botón para aumentar cantidad
        holder.plusEachItem.setOnClickListener(v -> {
            cartItems.get(position).setValue(cantidad + 1);
            notifyItemChanged(position);
            updateTotal(); // Actualiza total
        });

        // Botón para disminuir cantidad
        holder.minusEachItem.setOnClickListener(v -> {
            if (cantidad > 1) {
                cartItems.get(position).setValue(cantidad - 1);
                notifyItemChanged(position);
                updateTotal(); // Actualiza total
            }
        });

        // Botón para eliminar el producto del carrito (no funciona)
        holder.removeBtn.setOnClickListener(v -> {
            int positionToRemove = holder.getAdapterPosition();
            if (positionToRemove != RecyclerView.NO_POSITION) {
                ShopItem itemToRemove = cartItems.get(positionToRemove).getKey();
                com.example.loginregister.CartManager.deleteItem(itemToRemove);
                cartItems.remove(positionToRemove);
                notifyItemRemoved(positionToRemove);
                notifyItemRangeChanged(positionToRemove, cartItems.size());
                updateTotal(); // Actualizar total al eliminar
            }
        });

    }
    private void updateTotal() {
        double total = 0;
        for (Map.Entry<ShopItem, Integer> entry : cartItems) {
            ShopItem item = entry.getKey();
            int cantidad = entry.getValue();
            total += item.getPrice() * cantidad;
        }
        totalTxt.setText(String.format("%.2f $", total));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, quantityTxt, plusEachItem, minusEachItem;
        ImageView picMain, removeBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.nameItemCart);
            priceTxt = itemView.findViewById(R.id.totalCartTxt);
            quantityTxt = itemView.findViewById(R.id.numberItems);
            plusEachItem = itemView.findViewById(R.id.plusEachItem);
            minusEachItem = itemView.findViewById(R.id.minusEachItem);
            picMain = itemView.findViewById(R.id.picCart);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }
}
