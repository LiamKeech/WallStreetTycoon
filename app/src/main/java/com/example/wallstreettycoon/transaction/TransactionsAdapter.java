package com.example.wallstreettycoon.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private List<Transaction> transactions;

    public TransactionsAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions.clear();
        this.transactions.addAll(newTransactions);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvStock.setText(String.valueOf(transaction.getStockID()));
        holder.tvType.setText(transaction.getTransactionType());
        holder.tvQty.setText(String.format("%.0f", (double) transaction.getQuantity()));
        holder.tvPrice.setText(String.format("$%.2f", transaction.getPrice().doubleValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        try {
            holder.tvDate.setText(sdf.format(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(transaction.getTransactionDate().toString()))));
        } catch (Exception e) {
            holder.tvDate.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        if (transactions != null) {
            return transactions.size();
        } else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStock, tvType, tvQty, tvPrice, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvType = itemView.findViewById(R.id.tv_type);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}