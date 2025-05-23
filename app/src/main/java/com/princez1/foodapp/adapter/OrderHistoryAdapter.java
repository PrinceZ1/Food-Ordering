package com.princez1.foodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.princez1.foodapp.R;
import com.princez1.foodapp.domain.Foods;
import com.princez1.foodapp.domain.Order;

import java.text.DecimalFormat; // Thêm import này
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<Order> items;

    public OrderHistoryAdapter(ArrayList<Order> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = items.get(position);

        if (order.getOrderId() != null) {
            holder.orderIdTxt.setText(order.getOrderId().substring(Math.max(0, order.getOrderId().length() - 8)));
        } else {
            holder.orderIdTxt.setText("N/A");
        }
        holder.orderStatusTxt.setText(order.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.orderDateTxt.setText(sdf.format(new Date(order.getOrderDateTimestamp())));

        // Sử dụng DecimalFormat để hiển thị ký hiệu $ và định dạng số
        DecimalFormat dollarFormatter = new DecimalFormat("$#,##0.00"); // Ví dụ: $1,250.50
        holder.orderTotalAmountTxt.setText(dollarFormatter.format(order.getTotalAmount()));

        StringBuilder itemsString = new StringBuilder();
        if (order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                Foods food = order.getItems().get(i);
                if (food != null && food.getTitle() != null) {
                    itemsString.append(food.getTitle()).append(" x").append(food.getNumberInCart());
                    if (i < order.getItems().size() - 1) {
                        itemsString.append(", ");
                    }
                }
            }
        }
        holder.orderItemsTxt.setText(itemsString.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, orderTotalAmountTxt, orderStatusTxt, orderItemsTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            orderTotalAmountTxt = itemView.findViewById(R.id.orderTotalAmountTxt);
            orderStatusTxt = itemView.findViewById(R.id.orderStatusTxt);
            orderItemsTxt = itemView.findViewById(R.id.orderItemsTxt);
        }
    }
}