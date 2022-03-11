package com.example.quparking.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private UserModel userModel;
    private FirebaseAuth mAuth;
    private EditText userNameEdit;
    private EditText phoneEdit;
    private EditText emailEdit;
    private EditText mPassEditText;
    private EditText mConfirmPassEditText;
    private MaterialButton changePassButton;
    TextInputLayout passLayout;
    TextInputLayout confirmPassLayout;
    private FloatingActionButton fab;

    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        if (getIntent().hasExtra("user")){
            userModel = (UserModel) getIntent().getSerializableExtra("user");
            userKey = userModel.getKey();
        }else{
            finish();
        }
        userNameEdit = findViewById(R.id.user_name_edit);
        phoneEdit = findViewById(R.id.phone_edit);
        emailEdit = findViewById(R.id.email_edit);
        mPassEditText = findViewById(R.id.password);
        mConfirmPassEditText = findViewById(R.id.confirm_password);
        changePassButton = findViewById(R.id.change_pass_button);
        passLayout = findViewById(R.id.textInputLayout2);
        confirmPassLayout = findViewById(R.id.textInputLayout3);
        fab = findViewById(R.id.save_fab);
        fillUserInfo(userModel);



        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changePassButton.setVisibility(View.GONE);
                confirmPassLayout.setVisibility(View.VISIBLE);
                passLayout.setVisibility(View.VISIBLE);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempToSaveInfo();
            }
        });
    }


    void fillUserInfo(UserModel user){
        userNameEdit.setText(user.getUserName());
        phoneEdit.setText(user.getPhone());
        emailEdit.setText(user.getEmail());
        mConfirmPassEditText.setText(user.getPassword());
        mPassEditText.setText(user.getPassword());
    }

    void attempToSaveInfo(){
        String userName = userNameEdit.getEditableText().toString();
        String phone = phoneEdit.getEditableText().toString();
        String email = emailEdit.getEditableText().toString();
        String password = mPassEditText.getEditableText().toString();
        String confirmPass = mConfirmPassEditText.getEditableText().toString();

        mPassEditText.setError(null);
        mConfirmPassEditText.setError(null);
        if(TextUtils.isEmpty(email)){ emailEdit.setError(getString(R.string.empty_message)); }

        if (!isEmailValid(email)){
            if(TextUtils.isEmpty(userName)){ userNameEdit.setError(getString(R.string.empty_message));
            }else {

                emailEdit.setError(getString(R.string.email_message));
            }
        }else{
            emailEdit.setError(null);
        }
        if (TextUtils.isEmpty(phone)){
            phoneEdit.setError(getString(R.string.empty_message));
        }

        if (!TextUtils.isEmpty(phone) && phone.length() != 8){
            phoneEdit.setError(getString(R.string.phone_error));

        }

        if (!TextUtils.isEmpty(phone)){
            if(phone.charAt(0) != '3' && phone.charAt(0) != '5' && phone.charAt(0) != '6' && phone.charAt(0) != '7' ){
                phoneEdit.setError(getString(R.string.phone_error));
            }
        }
        if (!isPasswordValid(password,confirmPass)){
            if(TextUtils.isEmpty(password)){ mPassEditText.setError(getString(R.string.empty_message));
            }else if(TextUtils.isEmpty(confirmPass)){ mConfirmPassEditText.setError(getString(R.string.empty_message));
            }else if(password.length() < 8){
                mPassEditText.setError("password should be at least 8 letters");
            } else{
                mPassEditText.setError(getString(R.string.not_matching_pass));
            }
        }

        if (isEmailValid(email) && !TextUtils.isEmpty(userName) && isPasswordValid(password , confirmPass) && !TextUtils.isEmpty(phone) && phone.length() == 8 ){
            //TODO-SignUp Here

            if (!TextUtils.equals(userModel.getUserName(),userName)){
                pushUserName(userName);
            }

            if (!TextUtils.equals(userModel.getPhone(),phone)){
                if(phone.charAt(0) != '3' && phone.charAt(0) != '5' && phone.charAt(0) != '6' && phone.charAt(0) != '7' ) {
                    phoneEdit.setError(getString(R.string.phone_error));


                }else{
                    pushPhone(phone);
                }
            }

            if (!TextUtils.equals(userModel.getEmail(),email)){
                updateEmail(userModel.getEmail(),userModel.getPassword(),email);
            }

            if(!TextUtils.equals(userModel.getPassword(),password)){
                updatePassword(userModel.getEmail(),userModel.getPassword(),password);
            }

        }

    }

    boolean isPasswordValid(String pass,String confirmed){

        return (confirmed.length() >= 8 && pass.length() >= 8 && TextUtils.equals(pass,confirmed));


    }
    boolean isEmailValid(String s){
        return s.contains("@") && s.contains(".com") || s.contains("qu.edu.qa");
    }

    void pushEmail(String email){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userKey).child("email").setValue(email);
        Toast.makeText(getApplicationContext(),"Email changed",Toast.LENGTH_LONG).show();
    }

    void pushPassword(String password){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userKey).child("password").setValue(password);
        Toast.makeText(getApplicationContext(),"Password changed",Toast.LENGTH_LONG).show();
    }

    void pushPhone(String phone){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userKey).child("phone").setValue(phone);
        Toast.makeText(getApplicationContext(),"Phone number changed",Toast.LENGTH_LONG).show();
    }

    void pushUserName(String userName){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userKey).child("userName").setValue(userName);
        Toast.makeText(getApplicationContext(),"User name changed",Toast.LENGTH_LONG).show();
    }

    void updateEmail(String currentEmail, String currentPass, final String newEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentEmail, currentPass); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
       user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("update", "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("update", "User email address updated.");
                                            pushEmail(newEmail);
                                        }
                                    }
                                });
                    }
                });


    }

    void updatePassword(String email, String pass, final String newPass){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, pass);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updatePassword(newPass)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("update", "User [password] address updated.");
                                            pushPassword(newPass);
                                        }
                                    }
                                });
                    }
                });
    }

}
