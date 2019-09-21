package com.armo.client.entertainment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.armo.client.base.BaseFragment;
import com.armo.client.utils.Constants;
import com.armo.client.youtube.PlayListActivity;
import com.armorobot.client.R;

import butterknife.OnClick;

public class EntertainmentFragment extends BaseFragment {

    public static EntertainmentFragment newInstance() {
        return new EntertainmentFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.entertainment, container, false);
    }

    @OnClick(R.id.cartoon)
    public void onCartoonClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.CARTOON_PLAYLIST_ID));
    }

    @OnClick(R.id.songs)
    public void onSongsClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.SONGS_PLAYLIST_ID));
    }

    @OnClick(R.id.movies)
    public void onMoviesClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.MOVIES_PLAYLIST_ID));
    }

    @OnClick(R.id.stories)
    public void onStoriesClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.STORIES_PLAYLIST_ID));
    }


    @Override
    public
    @StringRes
    int getTitle() {
        return R.string.entertainment;
    }
}

