package com.example.wallstreettycoon.marketevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.model.MarketEvent;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<MarketEvent> notifications;

    public NotificationAdapter(Context context, List<MarketEvent> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_card, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        MarketEvent notification = notifications.get(position);

        holder.txtTitle.setText(notification.getTitle());
        holder.txtInfo.setText(notification.getInfo());

        // Show minigame button
        if (notification.isMinigame()) {
            holder.btnPlayMinigame.setVisibility(View.VISIBLE);
            holder.btnPlayMinigame.setOnClickListener(v -> {
                // TODO: Launch minigame activity

            });
        } else {
            holder.btnPlayMinigame.setVisibility(View.GONE);
        }

        // Click entire card to open expanded dialog
        holder.cardView.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                ExpandedNotificationDialogFragment dialog =
                        ExpandedNotificationDialogFragment.newInstance(
                                notification.getTitle(),
                                notification.getInfo(),
                                notification.getDuration(),
                                notification.isMinigame()
                        );
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ExpandedNotification");
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtInfo;
        AppCompatButton btnPlayMinigame;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            txtTitle = itemView.findViewById(R.id.txtNotificationTitle);
            txtInfo = itemView.findViewById(R.id.txtNotificationInfo);
            btnPlayMinigame = itemView.findViewById(R.id.btnPlayMinigame);
        }
    }
}