package com.example.loginregister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginregister.Swagger.InventoryResponse;

import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.ViewHolder>{
    private List<InventoryResponse> inventarioList;
    private Context context;

    public InventarioAdapter(Context context, List<InventoryResponse> inventarioList) {
        this.context = context;
        this.inventarioList = inventarioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_row_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryResponse item = inventarioList.get(position);
        holder.firstLine.setText(item.getNombre());
        holder.secondLine.setText(item.getDescripcion());
        holder.CantidadText.setText("Cantidad: " + item.getCantidad());

        Glide.with(context)
                .load(item.getUrl_icon())
                .placeholder(R.mipmap.ic_launcher) // imagen por defecto mientras carga
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView firstLine, secondLine, CantidadText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            firstLine = itemView.findViewById(R.id.firstLine);
            secondLine = itemView.findViewById(R.id.secondLine);
            CantidadText = itemView.findViewById(R.id.CantidadTxt);
        }
    }
}
