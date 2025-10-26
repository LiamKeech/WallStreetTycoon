package com.example.wallstreettycoon.chapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.chapter.Chapter;
import com.example.wallstreettycoon.chapter.ChapterManager;
import com.example.wallstreettycoon.chapter.ChapterState;
import com.example.wallstreettycoon.model.Game;

import java.util.List;

public class ChapterProgressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private TextView lblEmptyChapters;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_progress);

        recyclerView = findViewById(R.id.chapter_recycler_view);
        lblEmptyChapters = findViewById(R.id.lblEmptyChapters);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ChapterManager and load chapters
        ChapterManager chapterManager = ChapterManager.getInstance();
        List<Chapter> chapters = chapterManager.getChapters();

        // Handle empty state
        if (chapters.isEmpty()) {
            lblEmptyChapters.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            lblEmptyChapters.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            chapterAdapter = new ChapterAdapter(chapters);
            recyclerView.setAdapter(chapterAdapter);
        }

        // Set up back button
        btnBack.setOnClickListener(v -> finish());
    }

    // RecyclerView Adapter for chapters
    private static class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

        private final List<Chapter> chapters;

        public ChapterAdapter(List<Chapter> chapters) {
            this.chapters = chapters;
        }

        @Override
        public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter, parent, false);
            return new ChapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChapterViewHolder holder, int position) {
            Chapter chapter = chapters.get(position);
            holder.chapterName.setText(chapter.getChapterName());
            holder.chapterDescription.setText(chapter.getDescription());

            String statusText = getUserFriendlyState(chapter.getState());
            holder.chapterStatus.setText(statusText);

            // Highlight current chapter
            if (chapter.getChapterID() == Game.getInstance().currentChapterID) {
                holder.cardView.setCardBackgroundColor(
                        holder.itemView.getContext().getResources().getColor(R.color.NotificationInfo));
            } else {
                holder.cardView.setCardBackgroundColor(
                        holder.itemView.getContext().getResources().getColor(R.color.white));
            }
        }

        @Override
        public int getItemCount() {
            return chapters.size();
        }

        static class ChapterViewHolder extends RecyclerView.ViewHolder {
            TextView chapterName;
            TextView chapterDescription;
            TextView chapterStatus;
            CardView cardView;

            public ChapterViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.chapterCard);
                chapterName = itemView.findViewById(R.id.chapter_name);
                chapterDescription = itemView.findViewById(R.id.chapter_description);
                chapterStatus = itemView.findViewById(R.id.chapter_status);
            }
        }

        // Helper method to map ChapterState to user-friendly labels
        private String getUserFriendlyState(ChapterState state) {
            switch (state) {
                case NOT_STARTED:
                    return "Locked";
                case IN_PROGRESS:
                    return "Active";
                case COMPLETED:
                    return "Done";
                default:
                    return "Unknown";
            }
        }
    }
}