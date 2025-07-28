package com.example.wallstreettycoon.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private Context context;
    private List<Stock> stockArrayList;

    // Constructor
    public StockAdapter(Context context, List<Stock> stockModelArrayList) {
        this.context = context;
        this.stockArrayList = stockModelArrayList;
    }

    @NonNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StockAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Stock stock = stockArrayList.get(position);
        holder.stockSym.setText(stock.getSymbol());
        holder.stockName.setText(stock.getStockName());
        DatabaseUtil dbUtil = new DatabaseUtil(context);
        holder.stockPrice.setText(String.format("%.2f",dbUtil.getCurrentStockPrice(stock.getStockID(), 1)));
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return stockArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockSym;
        private final TextView stockName;
        private final TextView stockPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stockSym = itemView.findViewById(R.id.txtStockSymb);
            stockName = itemView.findViewById(R.id.txtStockName);
            stockPrice = itemView.findViewById(R.id.txtStockPrice);
        }
    }
}
