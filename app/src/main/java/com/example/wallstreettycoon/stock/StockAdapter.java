package com.example.wallstreettycoon.stock;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.displayBuySell.DisplayStockActivity;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private Context context;
    private List<Stock> stockArrayList;
    private DatabaseUtil dbUtil;

    // Constructor
    public StockAdapter(Context context, List<Stock> stockModelArrayList) {
        this.context = context;
        this.stockArrayList = stockModelArrayList;
        this.dbUtil = new DatabaseUtil(context);
    }

    public void updateList(List<Stock> list)
    {
        this.stockArrayList = list;
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

        try {
            holder.stockPrice.setText("$" + String.format("%.2f",dbUtil.getCurrentStockPrice(stock.getStockID(), 1)));
        } catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get stock
                int pos = holder.getAdapterPosition();
                Stock clickedStock = stockArrayList.get(pos);

                Intent intent = new Intent(context, DisplayStockActivity.class);

                intent.putExtra("stock_id", clickedStock.getStockID());
                intent.putExtra("stock_symbol", clickedStock.getSymbol());
                intent.putExtra("stock_name", clickedStock.getStockName());

                context.startActivity(intent);
            }
        });
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
            stockSym = itemView.findViewById(R.id.lblStockSymbol);
            stockName = itemView.findViewById(R.id.lblStockName);
            stockPrice = itemView.findViewById(R.id.lblStockCurrentPrice);
        }
    }
}
