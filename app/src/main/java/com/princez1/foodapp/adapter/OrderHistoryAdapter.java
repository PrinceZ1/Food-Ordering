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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<Order> items;
    private Context context;

    public OrderHistoryAdapter(ArrayList<Order> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = items.get(position);

        holder.orderIdTxt.setText(order.getOrderId().substring(Math.max(0, order.getOrderId().length() - 8))); // Hiển thị 8 ký tự cuối của ID
        holder.orderStatusTxt.setText(order.getStatus());

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.orderDateTxt.setText(sdf.format(new Date(order.getOrderDateTimestamp())));

        // Format currency
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        holder.orderTotalAmountTxt.setText(currencyFormatter.format(order.getTotalAmount()));

        // Display items (simple version)
        StringBuilder itemsString = new StringBuilder();
        if (order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                Foods food = order.getItems().get(i);
                itemsString.append(food.getTitle()).append(" x").append(food.getNumberInCart());
                if (i < order.getItems().size() - 1) {
                    itemsString.append(", ");
                }
            }
        }
        holder.orderItemsTxt.setText(itemsString.toString());

        // You can add an OnClickListener for each item if needed
        // holder.itemView.setOnClickListener(v -> {
        //     // Handle click on order item, e.g., show order details
        // });
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

    public void updateOrders(ArrayList<Order> newOrders) {
        this.items.clear();
        this.items.addAll(newOrders);
        notifyDataSetChanged();
    }
}