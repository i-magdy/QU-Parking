package com.example.quparking.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quparking.Main2Activity;
import com.example.quparking.MainActivity;
import com.example.quparking.R;
import com.example.quparking.databinding.ActivityLoginBinding;
import com.example.quparking.model.UserModel;
import com.example.quparking.ui.parking.ParkingSlotsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout mSignUpLayout;
    private LinearLayout mLoginLayout;
    private LinearLayout mButtonsLayout;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mUserNameEditText;
    private EditText mPassEditText;
    private EditText mConfirmPassEditText;
    private EditText mLoginEmailEditText;
    private EditText mLoginPassEditText;
    private TextView mErrorTextView;
    private boolean isSignUpClicked = false;
    private boolean isLoginClicked = false;
    private boolean isSignUpLive = false;
    private boolean isLoginLive = false;
    private ProgressBar progressBar;
    private FirebaseUser currentUser;
    private static String SIGN_IN_TAG = "SignTag";
    private ConstraintLayout mainLayout;
    private ProgressBar progressBarLoging;
    private TextView emailExistTv;
    private ImageView logo;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private List<UserModel> mUsers = new ArrayList<>();

    private LoginViewModel viewModel;
    DatabaseReference reference;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mSignUpLayout = mBinding.signUpLayout;
        mLoginLayout = mBinding.loginLayout;
        mButtonsLayout = mBinding.buttonsLoginLayout;
        mUserNameEditText = mBinding.userName;
        mEmailEditText = mBinding.email;
        mPhoneEditText = mBinding.phone;
        logo = mBinding.logoIv;
        mPassEditText = mBinding.password;
        mConfirmPassEditText = mBinding.confirmPassword;
        mLoginEmailEditText = mBinding.loginEmail;
        mLoginPassEditText = mBinding.loginPassword;
        mErrorTextView = mBinding.loginErrorTextView;
        progressBar = mBinding.progress;
        mainLayout = mBinding.mainView;
        progressBarLoging = mBinding.progressLoging;
        progressBarLoging.setVisibility(View.INVISIBLE);
        mPhoneEditText.setEllipsize(TextUtils.TruncateAt.START);
        emailExistTv = mBinding.emailExistTv;

        //check if user exists
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getUsers().observe(this, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
               mUsers = userModels;
               progressBar.setVisibility(View.GONE);
               updateUI(currentUser,userModels);
            }
        });

mEmailEditText.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        emailExistTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
});


        mBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSignUpClicked && !isSignUpLive){
                    hideLayout(mLoginLayout);
                    showLayout(mSignUpLayout);
                    isSignUpLive = true;
                    isSignUpClicked = true;
                    isLoginClicked = false;
                    isLoginLive = false;
                }else if(isSignUpClicked && isSignUpLive){
                    //Toast.makeText(getApplicationContext(),"hey",Toast.LENGTH_LONG).show();
                    attemptSignUp();
                }
            }
        });

        mBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoginClicked&& !isLoginLive){
                    hideLayout(mSignUpLayout);
                    showLayout(mLoginLayout);
                    isSignUpClicked = false;
                    isLoginClicked = true;
                    isSignUpLive = false;
                    isLoginLive = true;

                }else if(isLoginClicked && isLoginLive){
                    attemptLogin();
                    Log.i( "Ytest",mUsers.toString());
                }
            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        checkUsersFromCloud(mUsers);

    }


    private void updateUI(FirebaseUser user,List<UserModel> users){
        if (user != null){
            String role = getRole(users,user.getEmail());
            if (TextUtils.equals(role,"admin")) {


                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                reference.removeEventListener(childEventListener);
                startActivity(i);
                finish();
            }else if (TextUtils.equals(role,"user")){

                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                reference.removeEventListener(childEventListener);
                startActivity(i);
                finish();
            }else if (TextUtils.equals(role,"disable")){
                Toast.makeText(getApplicationContext(),"Your're disabled",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                reference.removeEventListener(childEventListener);
                startActivity(i);
                finish();
            }
        }


        if(currentUser == null){
            logo.setVisibility(View.GONE);
            mButtonsLayout.setVisibility(View.VISIBLE);
        }

    }

    private void showLayout(View v){
        v.setVisibility(View.VISIBLE);
    }
    private void hideLayout(View v){
        v.setVisibility(View.INVISIBLE);
    }

    boolean isPasswordValid(String pass,String confirmed){

        return (confirmed.length() >= 8 && pass.length() >= 8 && TextUtils.equals(pass,confirmed));


    }
    boolean isEmailValid(String s){
        return s.contains("@") && s.contains(".com") || s.contains("qu.edu.qa");
    }

    private void attemptSignUp(){
        String userName = mUserNameEditText.getEditableText().toString();
        String email = mEmailEditText.getEditableText().toString();
        String phone = mPhoneEditText.getEditableText().toString();
        String password = mPassEditText.getEditableText().toString();
        String confirmPass = mConfirmPassEditText.getEditableText().toString();
        mPassEditText.setError(null);
        mConfirmPassEditText.setError(null);

        if(TextUtils.isEmpty(email)){ mEmailEditText.setError(getString(R.string.empty_message)); }

        if (!isEmailValid(email)){
            if(TextUtils.isEmpty(userName)){ mUserNameEditText.setError(getString(R.string.empty_message));
            }else {

                mEmailEditText.setError(getString(R.string.email_message));
            }
        }else{
            mEmailEditText.setError(null);
        }
        if (TextUtils.isEmpty(phone)){
            mPhoneEditText.setError(getString(R.string.empty_message));
        }

        if (!TextUtils.isEmpty(phone) && phone.length() != 8){
            mPhoneEditText.setError(getString(R.string.phone_error));

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

        if (isEmailValid(email) && !TextUtils.isEmpty(userName) && isPasswordValid(password , confirmPass) && !TextUtils.isEmpty(phone) && phone.length() == 8){
            //TODO-SignUp Here
            boolean userExist = false;
            if (!TextUtils.isEmpty(phone)){
                for (int i =0;i <mUsers.size();++i){
                    if (TextUtils.equals(mUsers.get(i).getEmail(),email)){
                        userExist = true;
                        break;
                    }
                }
                if (!userExist){
                    signUp(userName,email,phone, password);
                    progressBarLoging.setVisibility(View.VISIBLE);
                }else{
                    emailExistTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void attemptLogin(){
        String email = mLoginEmailEditText.getEditableText().toString();
        String password = mLoginPassEditText.getEditableText().toString();
        if (TextUtils.isEmpty(email)) mLoginEmailEditText.setError(getString(R.string.empty_message));
        if (TextUtils.isEmpty(password)) mLoginPassEditText.setError(getString(R.string.empty_message));
        //TODO check if user exists

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            if (!isUserExist(mUsers,email) || !isPasswordCorrect(mUsers,password)){
                if(!isUserExist(mUsers,email)){
                    userDoesNotExistUI(true);
                }else{
                    passwordIncorrectUI(true);
                }

            }else {
                userDoesNotExistUI(false);
                passwordIncorrectUI(false);
                login(email,password);
                progressBarLoging.setVisibility(View.VISIBLE);
            }
        }

    }
    void signUp(final String userName, final String email ,final String phone, final String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SIGN_IN_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("users").push().setValue(new UserModel("not_define",userName,email,phone,password,"user","not signed",false,"not signed","not signed","not signed"));
                            Snackbar snackbar = Snackbar.make(mainLayout,"Sign up successed.",Snackbar.LENGTH_LONG);
                            View v = snackbar.getView();
                            v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccentDark));

                            recreate();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(SIGN_IN_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,null);
                        }

                        // ...
                    }
                });


    }

    void login(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SIGN_IN_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar snackbar = Snackbar.make(mainLayout,"Log in successed.",Snackbar.LENGTH_LONG);
                            View v = snackbar.getView();
                            v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccentDark));

                            updateUI(user,mUsers);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(SIGN_IN_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,null);
                        }

                        // ...
                    }
                });
    }



    boolean isUserExist(List<UserModel> users , String email){
        //user account dose not exist
        int size = users.size();
        if (size == 0) {return false;}
        for (int i =0;i < size; i++){
            String s = users.get(i).getEmail();
            if (TextUtils.equals(email,s)){
                return true;
            }
        }
        return false;
    }
    String getRole(List<UserModel> users,String email){
        //user account dose not exist
        int size = users.size();
        if (size == 0) {return null;}
        for (int i =0;i < size; i++){
            String s = users.get(i).getEmail();
            if (TextUtils.equals(email,s)){
                return users.get(i).getRole();
            }
        }
        return null;
    }
    boolean isPasswordCorrect(List<UserModel> users , String password){
        //user account dose not exist
        int size = users.size();
        if (size == 0) {return false;}
        for (int i =0;i < size; i++){
            String s = users.get(i).getPassword();
            if (TextUtils.equals(password,s)){
                return true;
            }
        }
        return false;
    }
    void userDoesNotExistUI(boolean b){
        if(b) {
            mErrorTextView.setText(getString(R.string.login_email_error_message));
            mErrorTextView.setVisibility(View.VISIBLE);
        }else{
            mErrorTextView.setVisibility(View.INVISIBLE);
        }
    }
    void passwordIncorrectUI(boolean b){
        if(b) {
            mErrorTextView.setText(getString(R.string.login_password_message));
            mErrorTextView.setVisibility(View.VISIBLE);
        }else{
            mErrorTextView.setVisibility(View.INVISIBLE);
        }
    }
    void checkUsersFromCloud(final List<UserModel> users){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = data.getReference().child("users");


         childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.exists()) {


                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        users.add(user);
                        updateUI(currentUser,users);
                        progressBar.setVisibility(View.GONE);
                        Log.i("TEST_DATABASE",  " HOW");
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
