package com.example.wallstreettycoon.marketevent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame1.miniGame1;
import com.example.wallstreettycoon.minigames.miniGame1.miniGame1Notification;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Notification;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3Notification;
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

        // Show minigame button only for minigame notifications
        if (notification.isMinigame() && notification.getMinigameID() > 0) {
            holder.btnPlayMinigame.setVisibility(View.VISIBLE);
            holder.btnPlayMinigame.setOnClickListener(v -> {
                Intent intent;

                switch (notification.getMinigameID()) {
                    case 1:
                        intent = new Intent(context, miniGame1Notification.class);
                        break;
                    case 2:
                        intent = new Intent(context, miniGame2Notification.class);
                        break;
                    case 3:
                        intent = new Intent(context, miniGame3Notification.class);
                        break;
                    default:
                        Toast.makeText(context, "Invalid minigame ID: " + notification.getMinigameID(), Toast.LENGTH_SHORT).show();
                        return;
                }

                //intent.putExtra("duration", notification.getDuration());
                context.startActivity(intent);
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
                                notification.isMinigame(),
                                notification.getMinigameID()
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