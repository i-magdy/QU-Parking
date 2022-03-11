package com.example.quparking.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private String email;
    private String phone;
    private String role;
    private String serial;
    private String key;

    private String child;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent i = getIntent();
        if (i.hasExtra("user")) {
            ArrayList<String> data = i.getStringArrayListExtra("user");

            getSupportActionBar().setTitle(data.get(0));
            email = data.get(1);
            phone = data.get(2);
            serial = data.get(3);
            role = data.get(4);
            key = data.get(5);
        }
        FloatingActionButton callPhone = findViewById(R.id.phone_call);
        mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        final ScrollView scrollView = findViewById(R.id.scrollView2);
        final FrameLayout editLayout = findViewById(R.id.edit_layout);
        TextView emailTv = findViewById(R.id.email_tv);
        TextView phoneTv = findViewById(R.id.phone_tv);
        final TextView roleTv = findViewById(R.id.role_tv);
        final TextView serialTv = findViewById(R.id.serial_tv);
        final EditText editText = findViewById(R.id.edit_field);
        final MaterialButton cancel = findViewById(R.id.cancel_button);
        final MaterialButton edit = findViewById(R.id.edit_button);
        Switch sw = findViewById(R.id.user_switch);

        if (TextUtils.equals(role, "admin")) {
            sw.setVisibility(View.GONE);
        }

        if (TextUtils.equals(role, "user")) {
            sw.setChecked(true);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Toast.makeText(getApplicationContext(), "disabled", Toast.LENGTH_LONG).show();
                    pushdata("role", "disable");
                    roleTv.setText("disable");
                } else {
                    Toast.makeText(getApplicationContext(), "user", Toast.LENGTH_LONG).show();
                    pushdata("role", "user");
                    roleTv.setText("user");
                }
            }
        });

        emailTv.setText(email);
        phoneTv.setText(phone);
        roleTv.setText(role);
        serialTv.setText(serial);

        serialTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
                editText.setText(serial);
                child = "serialNo";
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                serial = editText.getEditableText().toString();
                serialTv.setText(serial);
                pushdata(child, serial);
            }
        });

        ActivityCompat.requestPermissions(UserActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);


        final Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+974" + phone));
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(UserActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
                    return;
                }
                startActivity(call);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:
               return;
       }
    }

    void pushdata(String c, String s){

        mDatabase.child("users").child(key).child(c).setValue(s);

    }
}
