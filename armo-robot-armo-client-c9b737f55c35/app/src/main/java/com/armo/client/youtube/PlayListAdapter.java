package com.armo.client.youtube;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.armo.client.model.youtube.PlayListItem;
import com.armo.client.utils.Utils;
import com.armorobot.client.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlayListAdapter extends RecyclerView.Adapter<
        PlayListAdapter.PlayListItemViewHolder> {


    private OnItemClickListener onItemClickListener;
    private List<PlayListItem> items = new ArrayList<>();
    private PlayListItem currentlyPlayingItem;


    public PlayListAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    public void setItems(List<PlayListItem> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public PlayListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlayListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R
                .layout.video_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlayListItemViewHolder holder, int position) {
        PlayListItem playListItem = items.get(position);
        holder.videoTextView.setText(playListItem.snippet.title);

        Glide.with(holder.videoImageView.getContext().getApplicationContext())
                .load(Utils.getVideoThumbnail(playListItem.snippet.resourceId.videoId))
                .into(holder.videoImageView);

        boolean isPlaying = playListItem.equals(currentlyPlayingItem);
        holder.play.setImageResource(isPlaying ? R.drawable.stop : R.drawable.play);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {

                if (isPlaying) {
                    onItemClickListener.onItemClick(playListItem, false);
                    currentlyPlayingItem = null;
                } else {
                    onItemClickListener.onItemClick(playListItem, true);
                    currentlyPlayingItem = playListItem;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        onItemClickListener = null;
    }

    public interface OnItemClickListener {
        void onItemClick(PlayListItem item, boolean play);
    }

    public static class PlayListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.videoImage)
        ImageView videoImageView;

        @BindView(R.id.play)
        ImageView play;

        @BindView(R.id.videoTitle)
        TextView videoTextView;

        public PlayListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
