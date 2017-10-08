package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.UserAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public abstract class UsersListFragment extends Fragment implements UserAdapter.UserAdapterListener {
    UserAdapter userAdapter;
    ArrayList<User> users;
    RecyclerView rvUsers;
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    long nextCursor;

    public interface UserSelectedListener {
        public void onUserSelected(User user);
        public void onProfileSelected(User user);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        users = new ArrayList<>();
        userAdapter = new UserAdapter(users, this);
        rvUsers = (RecyclerView) v.findViewById(R.id.rvUser);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setAdapter(userAdapter);
        nextCursor = -1;
        addDividers();
        attachScrollListener();
        return v;
    }

    private void addDividers() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                linearLayoutManager.getOrientation());
        rvUsers.addItemDecoration(dividerItemDecoration);
    }

    public void addItems(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                User user = User.fromJSON(response.getJSONObject(i));
                users.add(user);
                userAdapter.notifyItemInserted(users.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNextCursor(long cursor) {
        nextCursor = cursor;
        Log.d("nextcurso", String.valueOf(nextCursor));
    }

    @Override
    public void onItemSelected(View view, int position) {
        User user = users.get(position);
        ((UserSelectedListener) getActivity()).onUserSelected(user);
    }

    @Override
    public void onProfileSelected(View view, int position) {
        User user = users.get(position);
        ((UserSelectedListener) getActivity()).onProfileSelected(user);
    }

    private void attachScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                checkAndPopulate(nextCursor);
            }
        };
        rvUsers.addOnScrollListener(scrollListener);
    }

    private void checkAndPopulate(long nextCursor) {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            if (nextCursor != 0) {
                populateUsers(nextCursor);
            }
        } else {
            Snackbar.make(rvUsers, R.string.not_connected, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    protected abstract void populateUsers(long nextCursor);
}
