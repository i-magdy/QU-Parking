package com.example.quparking.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.example.quparking.ui.login.LoginActivity;
import com.example.quparking.ui.payment.PaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private UserModel userInfo;
    private ProfileViewModel viewModel;
    private TextView mUserNameTv;
    private TextView mPhoneTv;
    private TextView mEmailTv;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private CardView cardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getSupportActionBar().setTitle("Profile");
        if (mUser == null){
            finish();
        }
        cardInfo = findViewById(R.id.payment_profile_card);
        mUserNameTv = findViewById(R.id.user_name_tv);
        mPhoneTv = findViewById(R.id.phone_tv);
        mEmailTv = findViewById(R.id.email_tv);
        progressBar = findViewById(R.id.progress_profile);
        fab = findViewById(R.id.edit_fab);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.getUserData(mUser.getEmail());
        viewModel.getUserInfo().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModels) {

                if (userModels != null){
                    fillUserData(userModels);
                    progressBar.setVisibility(View.GONE);
                }



            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                if (userInfo != null) {
                    i.putExtra("user", userInfo);
                    startActivity(i);
                }
            }
        });

        cardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                logOut();
                Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    void fillUserData(UserModel user){
        if (user != null){
            userInfo = user;
            mUserNameTv.setText(user.getUserName());
            mPhoneTv.setText(user.getPhone());
            mEmailTv.setText(user.getEmail());
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    void logOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}
