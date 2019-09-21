package com.armo.client.teaching;

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

public class TeachingFragment extends BaseFragment {

    public static TeachingFragment newInstance() {
        return new TeachingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.teaching, container, false);
    }

    @OnClick(R.id.math)
    public void onMathClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.MATH_PLAYLIST_ID));
    }
    @OnClick(R.id.music)
    public void onMusicClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.MUSIC_PLAYLIST_ID));
    }

    @OnClick(R.id.arabic)
    public void onArabicClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.ARABIC_PLAYLIST_ID));
    }
    @OnClick(R.id.english)
    public void onEnglishClick() {
        startActivity(PlayListActivity.getIntent(getContext(), Constants.ENGLISH_PLAYLIST_ID));
    }

    @Override
    public
    @StringRes
    int getTitle() {
        return R.string.teaching;
    }
}

