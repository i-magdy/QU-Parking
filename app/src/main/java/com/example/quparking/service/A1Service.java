package com.example.quparking.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class A1Service extends Worker {
    public A1Service(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        changeA1State();
        bookingTag("a1","serialTag");
        return Result.success();
    }

    void changeA1State(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("booking").child("a1").child("state").setValue(0);
    }

    void bookingTag(String park,String tag){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("booking").child(park).child("id").setValue(tag);
    }
}
