package com.example.quparking.ui.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.example.quparking.ui.parking.ParkingViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PaymentViewModel viewModel;
    UserModel mUserModel;
    EditText holderNameEdit;
    EditText cardNumberEdit;
    EditText expireEdit;
    EditText cvvEdit;
    EditText zipeCodeEdit;
    MaterialButton addCardButton;
    boolean isDateFetched = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        holderNameEdit = findViewById(R.id.name_holder_edit);
        cardNumberEdit = findViewById(R.id.card_number_edit);
        expireEdit = findViewById(R.id.expire_edit);
        cvvEdit = findViewById(R.id.cvv_edit);
        zipeCodeEdit = findViewById(R.id.zip_code_edit);
        addCardButton = findViewById(R.id.add_card_button);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        viewModel.getUsers(user.getEmail());
        viewModel.getuser().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if(userModel != null){
                    mUserModel = userModel;
                    isDateFetched = true;
                    fillCardInfo(userModel);
                }
            }
        });


        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateFetched){
                    attemptToAddCard();
                }
            }
        });


    }

    void fillCardInfo(UserModel user){
        if (user.isPayment()){
            holderNameEdit.setText(user.getHolderName());
            cardNumberEdit.setText(user.getCardNumber());
            zipeCodeEdit.setText(user.getZipcode());
        }
    }

    void attemptToAddCard(){
        String holdername = holderNameEdit.getEditableText().toString();
        String cardNumber = cardNumberEdit.getEditableText().toString();
        String cvv = cvvEdit.getEditableText().toString();
        String expire = expireEdit.getEditableText().toString();
        String zipCode = zipeCodeEdit.getEditableText().toString();

        if (holdername.isEmpty()){
            holderNameEdit.setError(getString(R.string.empty_message));

        }

        if (cardNumber.isEmpty()){
            cardNumberEdit.setError(getString(R.string.empty_message));
        }

        if (cvv.isEmpty()){
            cvvEdit.setError(getString(R.string.empty_message));
        }

        if (expire.isEmpty()){
            expireEdit.setError(getString(R.string.empty_message));
        }

        if (zipCode.isEmpty()){
            zipeCodeEdit.setError(getString(R.string.empty_message));
        }

        if (!cardNumber.isEmpty() && cardNumber.length() != 16){
            cardNumberEdit.setError("Invalid card number");
        }

        if (cvv.length() != 3){
            cvvEdit.setError("Invalid CVV");
        }

        if (!expire.isEmpty() ){
            if (expire.charAt(2) != '/') {
                expireEdit.setError("Invalid Expire Date");
            }
        }

        if ( cardNumber.length() == 16 && !holdername.isEmpty() && cvv.length() == 3 &&expire.charAt(2) == '/' && !zipCode.isEmpty() ){

            pushPaymentMethod(holdername,cardNumber,zipCode);
        }
    }

    void  pushPaymentMethod(String name , String number , String zip){
        mUserModel.setHolderName(name);
        mUserModel.setCardNumber(number);
        mUserModel.setZipcode(zip);
        mUserModel.setPayment(true);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("users").child(mUserModel.getKey()).setValue(mUserModel);

        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "Card Added Successfully", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccentDark));
                snackbar.setActionTextColor(getColor(R.color.colorBackGround))
                .show();


    }
}
