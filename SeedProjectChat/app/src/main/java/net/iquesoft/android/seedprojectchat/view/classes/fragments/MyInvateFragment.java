package net.iquesoft.android.seedprojectchat.view.classes.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import net.iquesoft.android.seedprojectchat.R;
import net.iquesoft.android.seedprojectchat.adapters.MyInviteAdapter;
import net.iquesoft.android.seedprojectchat.common.BaseFragment;
import net.iquesoft.android.seedprojectchat.di.components.IMainActivityComponent;
import net.iquesoft.android.seedprojectchat.model.Friends;
import net.iquesoft.android.seedprojectchat.presenter.classes.fragments.MyInviteFragmentPresenter;
import net.iquesoft.android.seedprojectchat.view.interfaces.fragments.IMyInvateFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class MyInvateFragment extends BaseFragment implements IMyInvateFragment {

    @InjectPresenter
    MyInviteFragmentPresenter presenter;

    @BindView(net.iquesoft.android.seedprojectchat.R.id.recycler_my_invite)
    RecyclerView recyclerMyInvite;

    @BindView(R.id.swipe_refresh)
    SwipyRefreshLayout swipyRefreshLayout;

    @BindView(R.id.progress_my_invite)
    ProgressBar progressBar;

    private MyInviteAdapter adapter;
    private View rootView;

    public void setProgressBarVisible(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarGone(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(net.iquesoft.android.seedprojectchat.R.layout.fragment_my_invate, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getComponent(IMainActivityComponent.class).inject(this);
        swipyRefreshLayout.setOnRefreshListener(direction -> {
            presenter.updateMyInviteList();
            swipyRefreshLayout.setRefreshing(false);
        });
    }

    public void setUserAdapter(List<Friends> users) {
        adapter = new MyInviteAdapter(users, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerMyInvite.setLayoutManager(linearLayoutManager);
        recyclerMyInvite.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        scaleInAnimationAdapter.setFirstOnly(true);
        scaleInAnimationAdapter.setDuration(500);
        setProgressBarGone();
        recyclerMyInvite.setAdapter(scaleInAnimationAdapter);
    }
}
