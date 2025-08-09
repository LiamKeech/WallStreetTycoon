package com.example.wallstreettycoon.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.Stock;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioStockAdapter extends RecyclerView.Adapter<PortfolioStockAdapter.ViewHolder> {

    private Context context;
    private List<PortfolioStock> PstockArrayList;

    // Constructor
    public PortfolioStockAdapter(Context context, List<PortfolioStock> stockModelArrayList) {
        this.context = context;
        this.PstockArrayList = stockModelArrayList;
    }

    @NonNull
    @Override
    public PortfolioStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_portfoliostock_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PortfolioStockAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        PortfolioStock Pstock = PstockArrayList.get(position);
        DatabaseUtil dbUtil = new DatabaseUtil(context);
        Stock stock = dbUtil.getStock(Integer.parseInt(Pstock.getStockID()));

        //new version
        holder.lblStockSymbol.setText(stock.getSymbol());
        holder.lblStockName.setText(stock.getStockName());
        holder.lblSharesOwned.setText(Pstock.getQuantity() + " shares");

        double currentPrice = dbUtil.getCurrentStockPrice(stock.getStockID(), 1); // Example timestamp
        String currentPriceStr = String.format("$%.2f", currentPrice);
        holder.lblCurrentPrice.setText(currentPriceStr);

        double priceChange = currentPrice - Pstock.getBuyPrice();
        String priceChangeStr;
        if (priceChange >= 0) {
            priceChangeStr = String.format("+$%.2f (+%.1f%%)", priceChange, (priceChange / Pstock.getBuyPrice()) * 100);
            holder.lblPriceChange.setTextColor(context.getResources().getColor(R.color.Green));
        } else {
            priceChangeStr = String.format("%.2f (%.1f%%)", priceChange, (priceChange / Pstock.getBuyPrice()) * 100);
            holder.lblPriceChange.setTextColor(context.getResources().getColor(R.color.Red));
        }
        holder.lblPriceChange.setText(priceChangeStr);

        double totalValueDouble = Pstock.getQuantity() * currentPrice;
        String totalValueStr = String.format("$%.2f", totalValueDouble);
        holder.lblTotalValue_numeric.setText(totalValueStr);
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return PstockArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblStockSymbol, lblStockName, lblSharesOwned, lblCurrentPrice, lblPriceChange, lblTotalValue_numeric, lblTotalValue_portfolioCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblStockSymbol = itemView.findViewById(R.id.lblStockSymbol_portfolioCard);
            lblStockName = itemView.findViewById(R.id.lblStockName_portfolioCard);
            lblSharesOwned = itemView.findViewById(R.id.lblSharesOwned_portfolioCard);
            lblCurrentPrice = itemView.findViewById(R.id.lblCurrentPrice_portfolioCard);
            lblPriceChange = itemView.findViewById(R.id.lblPriceChange_portfolioCard);
            lblTotalValue_numeric = itemView.findViewById(R.id.lblTotalValue_numeric_portfolioCard);
            lblTotalValue_portfolioCard = itemView.findViewById(R.id.lblTotalValue_portfolioCard);
        }
    }
}
