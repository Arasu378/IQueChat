package com.iquesoft.andrew.seedprojectchat.view.classes.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.backendless.Backendless;
import com.iquesoft.andrew.seedprojectchat.R;
import com.iquesoft.andrew.seedprojectchat.common.BaseActivity;
import com.iquesoft.andrew.seedprojectchat.di.IHasComponent;
import com.iquesoft.andrew.seedprojectchat.di.components.DaggerIMainActivityComponent;
import com.iquesoft.andrew.seedprojectchat.di.components.IMainActivityComponent;
import com.iquesoft.andrew.seedprojectchat.di.components.ISeedProjectChatComponent;
import com.iquesoft.andrew.seedprojectchat.di.modules.MainActivityModule;
import com.iquesoft.andrew.seedprojectchat.presenter.classes.activity.MainActivityPresenter;
import com.iquesoft.andrew.seedprojectchat.util.OnBackPressedListener;
import com.iquesoft.andrew.seedprojectchat.util.UpdateCurentUser;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.AboutUsFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.ChatWithFriendFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.ContainerFriendFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.FindFriendFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.GroupChatContainer;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.MainFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.SettingsFragment;
import com.iquesoft.andrew.seedprojectchat.view.classes.fragments.TermsAndConditionsFragment;
import com.iquesoft.andrew.seedprojectchat.view.interfaces.activity.IMainActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivity, IHasComponent<IMainActivityComponent> {

    private IMainActivityComponent mainActivityComponent;

    @InjectPresenter
    MainActivityPresenter presenter;

    @Inject
    FindFriendFragment findFriendFragment;

    @Inject
    ContainerFriendFragment containerFriendFragment;

    @Inject
    UpdateCurentUser updateCurentUser;

    @Inject
    ChatWithFriendFragment chatWithFriendFragment;

    @Inject
    GroupChatContainer groupChatContainer;

    @Inject
    SettingsFragment settingsFragment;

    @Inject
    TermsAndConditionsFragment termsAndConditionsFragment;

    @Inject
    AboutUsFragment aboutUsFragment;

    @Inject
    MainFragment mainFragment;

    private Boolean mainFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mainFlag = savedInstanceState.getBoolean("mainFlag");
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ButterKnife.bind(navigationView.getHeaderView(0));
        View headerView = navigationView.getHeaderView(0);
        CircularImageView headerImage = (CircularImageView) headerView.findViewById(R.id.header_image_view);
        TextView userName = (TextView) headerView.findViewById(R.id.tv_user_name);
        TextView userEMail = (TextView) headerView.findViewById(R.id.tv_last_message);
        if (Backendless.UserService.CurrentUser().getProperty("photo") != null) {
            Uri uri = Uri.parse(Backendless.UserService.CurrentUser().getProperty("photo").toString());
            Picasso.with(getBaseContext()).load(uri).placeholder(R.drawable.seed_logo).into(headerImage);
        }
        userName.setText(Backendless.UserService.CurrentUser().getProperty("name").toString());
        userEMail.setText(Backendless.UserService.CurrentUser().getProperty("email").toString());

        if (mainFlag.equals(false)){
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle("Home");
            replaceFragment(mainFragment);
            mainFlag = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mainFlag", mainFlag);
    }

    @Override
    protected void onStop() {
        presenter.setOnline(updateCurentUser, false);
        super.onStop();
    }

    @Override
    protected void onResume() {
        presenter.setOnline(updateCurentUser, true);
        super.onResume();
    }

    public void setFindFriendFragment() {
        replaceFragment(findFriendFragment);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            OnBackPressedListener backPressedListener = null;
            for (Fragment fragment : fm.getFragments()) {
                if (fragment instanceof OnBackPressedListener) {
                    backPressedListener = (OnBackPressedListener) fragment;
                    break;
                }
            }

            if (backPressedListener != null) {
                backPressedListener.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            presenter.logOut(this, updateCurentUser);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setGroupChatContainer() {
        replaceFragment(groupChatContainer);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home){
            replaceFragment(mainFragment);
        }else if (id == R.id.nav_friends) {
            replaceFragment(containerFriendFragment);
        } else if (id == R.id.nav_group_chat) {
            replaceFragment(groupChatContainer);
        } else if (id == R.id.nav_manage) {
            replaceFragment(settingsFragment);
        } else if (id == R.id.nav_terms_and_conditions) {
            replaceFragment(termsAndConditionsFragment);
        } else if (id == R.id.nav_about_us) {
            replaceFragment(aboutUsFragment);
        } else if (id == R.id.nav_share) {
            presenter.share(this);
        } else if (id == R.id.nav_logout) {
            presenter.logOut(this, updateCurentUser);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = "chatBackStack";
        String fragmentTag = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        } else {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }

    @Override
    protected void setupComponent(ISeedProjectChatComponent appComponent) {
        mainActivityComponent = DaggerIMainActivityComponent.builder()
                .iSeedProjectChatComponent(appComponent)
                .mainActivityModule(new MainActivityModule(this))
                .build();
        mainActivityComponent.inject(this);
    }

    @Override
    public IMainActivityComponent getComponent() {
        return mainActivityComponent;
    }


}
