package com.example.quparking.ui.profile;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quparking.model.UserModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<UserModel> mData;

    public ProfileViewModel() {
        mData = new MutableLiveData<>();
    }


    LiveData<UserModel> getUserInfo(){

        return mData;
    }


    public void getUserData(final String user){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("users");
        final List<UserModel> users = new ArrayList<>();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.exists()) {


                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        userModel.setKey(dataSnapshot.getKey());
                        if (TextUtils.equals(userModel.getEmail(), user)) {
                            mData.setValue(userModel);
                        }

                        Log.i("TEST_DATABASE",  " users  fetched"+"   "+users.get(0).getKey());
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
}
