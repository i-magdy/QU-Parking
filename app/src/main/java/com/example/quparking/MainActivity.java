package com.example.quparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.Report;
import com.example.quparking.model.UserModel;
import com.example.quparking.ui.login.LoginActivity;
import com.example.quparking.ui.parking.ParkingSlotsActivity;
import com.example.quparking.ui.user.UserActivity;
import com.example.quparking.ui.user.UsersListActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String userName;
    String email;
    String phone;
private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MaterialButton userButton = findViewById(R.id.report_button);
        getSupportActionBar().setTitle("Feedback");
        editText = findViewById(R.id.report_edit_text);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getUsers(user.getEmail());
        viewModel.getuser().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {

                if (userModel != null){
                    userName = userModel.getUserName();
                    email = userModel.getEmail();
                    phone= userModel.getPhone();
                }
            }
        });




        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getEditableText().toString();
                pushFeedback(text);
                Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_LONG).show();
                editText.setText(null);

            }
        });



    }

    void pushFeedback(String text){

        if (!editText.getEditableText().toString().isEmpty()){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
            mDatabase.child("feedback").push().setValue(new Report(userName,email,phone,text));
        }
    }
}
