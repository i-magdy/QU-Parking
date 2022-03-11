package com.example.quparking;

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

public class MainViewModel extends ViewModel {


    MutableLiveData<UserModel> user;

    public MainViewModel() {

        user = new MutableLiveData<>();
    }


    LiveData<UserModel> getuser(){
        return user;
    }

    public void getUsers(final String email){
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = database.getReference().child("users");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.exists()) {

                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            userModel.setKey(dataSnapshot.getKey());
                            if (TextUtils.equals(userModel.getEmail(), email)) {
                                user.setValue(userModel);
                            }

                            Log.i("TEST_DATABASE", userModel.getKey());
                        }
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
