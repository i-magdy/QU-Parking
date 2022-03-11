package com.example.quparking.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity implements UsersListAdapter.ListItemOnClickListener{

    private SwipeRefreshLayout refreshLayout;
    private UsersListAdapter adapter;
    private List<UserModel> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        refreshLayout = findViewById(R.id.swipe_refresh_user);
        RecyclerView recyclerView = findViewById(R.id.users_list_recycler);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setRefreshing(true);
        users = new ArrayList<>();
        adapter = new UsersListAdapter(this);
        recyclerView.setAdapter(adapter);

        final UsersListViewModel viewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        viewModel.getData().observe(this, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                if (userModels.size() > 0) {
                    users = userModels;
                    adapter.setData(userModels);
                    refreshLayout.setRefreshing(false);


                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                viewModel.getData();

            }
        });


    }


    void fetchingUsersFromCloud(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("users");


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.exists()) {


                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        users.add(user);
                        refreshLayout.setRefreshing(false);

                        Log.i("TEST_DATABASE",  " users  fetched");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.addChildEventListener(childEventListener);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    public void onListItemClicked(int itemPosition) {

        Intent i = new Intent(this,UserActivity.class);
        ArrayList<String> user = new ArrayList<>();
        UserModel u = users.get(itemPosition);
        user.add(u.getUserName());
        user.add(u.getEmail());
        user.add(u.getPhone());
        user.add(u.getSerialNo());
        user.add(u.getRole());
        user.add(u.getKey());
        i.putStringArrayListExtra("user",  user);
        startActivity(i);


    }
}
