package com.example.wallstreettycoon.transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private DatabaseUtil dbUtil;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());

    public TransactionsAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.dbUtil = new DatabaseUtil(context);
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        String stockSymbol = dbUtil.getStockSymbol((int) transaction.getStockID());
        holder.tvStock.setText(stockSymbol != null ? stockSymbol : "N/A");

        holder.tvType.setText(transaction.getTransactionType());
        if ("BUY".equals(transaction.getTransactionType())) {
            holder.tvType.setTextColor(holder.itemView.getContext().getColor(R.color.Green));
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            holder.tvType.setTextColor(holder.itemView.getContext().getColor(R.color.Red));
            holder.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }

        holder.tvQty.setText(String.valueOf(transaction.getQuantity()));
        holder.tvPrice.setText(String.format("$%.2f", transaction.getPrice().doubleValue()));
        holder.tvDate.setText(dateFormat.format(transaction.getTransactionDate()));
//
//        if (position % 2 == 0) {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LightGrey));
//        } else {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.LightBlueHighlight));
//        }
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