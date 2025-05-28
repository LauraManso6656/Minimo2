package com.example.loginregister.Swagger;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.loginregister.DetailActivity;
import com.example.loginregister.R;
import com.example.loginregister.ShopActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<ShopItem> values; // Esta lista es la que se actualiza con updateList

    public ShopAdapter(List<ShopItem> values) {
        this.values = values; // 'values' referenciará 'displayedShopItems' de ShopActivity
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtPrice;
        public ShapeableImageView picItem;
        // public ImageView plusIcon; // No parece usarse en el onBindViewHolder
        public View layout;

        public ShopViewHolder(View v) {
            super(v);
            layout = v;
            txtName = v.findViewById(R.id.nameItem);
            txtPrice = v.findViewById(R.id.priceTxt);
            picItem = v.findViewById(R.id.picItem);
        }
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.viewholder_shop, parent, false);
        return new ShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShopViewHolder holder, int position) {
        ShopItem item = values.get(position); // Obtener de la lista 'values'

        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.valueOf(item.getPrice())); // Considera formatear el precio si es necesario

        Glide.with(holder.picItem.getContext())
                .load(item.getUrl_icon())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ShopAdapter", "Glide onLoadFailed: " + (e != null ? e.getMessage() : "Unknown error"));
                        // Considera ocultar progress bars aquí también si es el último ítem y falla la carga
                        checkAndHideProgressBars(holder, values.size());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        checkAndHideProgressBars(holder, values.size());
                        return false;
                    }
                })
                .into(holder.picItem);

        holder.layout.setOnClickListener(v -> {
            // Este click listener ya maneja la navegación a DetailActivity
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("item", item); // DetailActivity debe esperar "item"
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private void checkAndHideProgressBars(ShopViewHolder holder, int totalItems) {
        // Ocultar progress bars solo si es el último ítem visible el que se está cargando/renderizando
        if (holder.getBindingAdapterPosition() == totalItems - 1) {
            if (holder.itemView.getContext() instanceof ShopActivity) {
                ((ShopActivity) holder.itemView.getContext()).hideProgressBars();
            }
        }
    }

    public void updateList(List<ShopItem> newList) {
        values.clear();
        values.addAll(newList); // 'values' (que es 'displayedShopItems' en ShopActivity) se actualiza
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    // Método para obtener un ítem en una posición, útil para itemDetailClick en ShopActivity
    public ShopItem getItemAt(int position) {
        if (position >= 0 && position < values.size()) {
            return values.get(position);
        }
        return null;
    }
}