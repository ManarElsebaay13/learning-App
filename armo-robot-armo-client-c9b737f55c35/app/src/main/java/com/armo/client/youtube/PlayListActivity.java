package com.armo.client.youtube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.armo.client.base.BaseActivity;
import com.armo.client.model.firebase.Command;
import com.armo.client.model.firebase.PlayVideoCommand;
import com.armo.client.model.firebase.StopVideoCommand;
import com.armo.client.model.youtube.PlayListItem;
import com.armo.client.model.youtube.PlayListItems;
import com.armo.client.network.firebase.FirebaseClientHandler;
import com.armo.client.network.youtube.YouTubeAPIConnectionHandler;
import com.armo.client.utils.Constants;
import com.armo.client.utils.RxAndroidTransformer;
import com.armorobot.client.R;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;


public class PlayListActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, PlayListAdapter.OnItemClickListener {

    @BindView(R.id.playlistactivity_recyclerView)
    RecyclerView playListRecyclerView;

    @BindView(R.id.playlistactivity_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    PlayListAdapter playListAdapter;

    private String playListId = null;


    public static Intent getIntent(Context context, @NonNull String playListId) {
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtra(Constants.BUNDLE_PLAY_LIST_ID, playListId);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        setupToolbar(true);

        playListId = getIntent().getStringExtra(Constants.BUNDLE_PLAY_LIST_ID);
        if (playListId == null) {
            finish();
        } else {

            playListAdapter = new PlayListAdapter(this);
            playListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            playListRecyclerView.setAdapter(playListAdapter);
            swipeRefreshLayout.setOnRefreshListener(this);
            loadPlayList();
        }
    }

    private void loadPlayList() {
        swipeRefreshLayout.setRefreshing(true);
        Disposable playListItemsDisposable = YouTubeAPIConnectionHandler.getInstance()
                .getPlayListVideos(playListId)
                .compose(RxAndroidTransformer.applySingleSchedulers())
                .subscribe(this::onPlayListLoaded, this::onPlayListLoadingFailure);

        needToUnsubscribe(playListItemsDisposable);
    }


    public void onPlayListLoaded(PlayListItems playListItems) {
        swipeRefreshLayout.setRefreshing(false);
        playListAdapter.setItems(playListItems.items);

    }

    public void onPlayListLoadingFailure(Throwable throwable) {
        swipeRefreshLayout.setRefreshing(false);
        showToast(getString(R.string.error_occurred));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Override
    public void onRefresh() {
        loadPlayList();
    }


    @Override
    public void onItemClick(PlayListItem item, boolean play) {
        Command command;
        if (play)
            command = new PlayVideoCommand(item.snippet.resourceId.videoId);
        else
            command = new StopVideoCommand();
        FirebaseClientHandler.getInstance().sendCommand(command);
    }
}
