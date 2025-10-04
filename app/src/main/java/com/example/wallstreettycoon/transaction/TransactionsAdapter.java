package com.example.wallstreettycoon.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private Context context;

    public TransactionsAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvStockSymbol.setText(transaction.getStockSymbol());

        String type = transaction.getType().toUpperCase();
        holder.tvTypeBadge.setText(type);

        holder.tvDate.setText(transaction.getFormattedDate());

        double totalAmount = transaction.getPriceAsDouble() * transaction.getQuantity();

        if (type.equals("BUY")) {
            holder.tvTypeBadge.setTextColor(context.getResources().getColor(R.color.Green));
            holder.tvAmount.setText(String.format("+$%,.2f", totalAmount));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.Green));
        } else {
            holder.tvTypeBadge.setTextColor(context.getResources().getColor(R.color.Red));
            holder.tvAmount.setText(String.format("-$%,.2f", totalAmount));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.Red));
        }

        int quantity = transaction.getQuantity();
        holder.tvShares.setText(quantity + (quantity == 1 ? " Share" : " Shares"));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStockSymbol;
        TextView tvTypeBadge;
        TextView tvDate;
        TextView tvAmount;
        TextView tvShares;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStockSymbol = itemView.findViewById(R.id.tv_stock_symbol);
            tvTypeBadge = itemView.findViewById(R.id.tv_type_badge);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvShares = itemView.findViewById(R.id.tv_shares);
        }
    }
}