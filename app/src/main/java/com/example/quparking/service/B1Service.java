package com.example.quparking.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class B1Service extends Worker {
    public B1Service(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        changeB1State();
        bookingTag("b1","serialTag");
        return Result.success();
    }

    void changeB1State(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("booking").child("b1").child("state").setValue(0);
    }

    void bookingTag(String park,String tag){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("booking").child(park).child("id").setValue(tag);
    }
}
