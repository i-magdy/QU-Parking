package com.example.quparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quparking.model.UserModel;
import com.example.quparking.ui.parking.ParkingSlotsActivity;
import com.example.quparking.ui.payment.PaymentActivity;
import com.example.quparking.ui.profile.ProfileActivity;
import com.example.quparking.ui.user.UsersListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity implements CardView.OnClickListener {

    MainViewModel viewModel;
    FirebaseAuth auth ;
    FirebaseUser user ;
    String role;
    CardView cardView1;
    ImageView imageView1;
    TextView textView1;
    CardView cardView2;
    ImageView imageView2;
    TextView textView2;
    CardView cardView3;
    ImageView imageView3;
    TextView textView3;
    CardView cardView4;
    ImageView imageView4;
    TextView textView4;
    CardView cardView5;
    ImageView imageView5;
    TextView textView5;
    CardView cardView6;
    ImageView imageView6;
    TextView textView6;
    private boolean isPaymentAdded= false;
    CardView paymentCard;
    private FrameLayout paymentLayout;
    private TextView nameView;
    private TextView activeStateTv;
    private TextView roleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorAccentDark));
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        viewModel.getUsers(user.getEmail());
        activeStateTv = findViewById(R.id.activate_state_tv);
        paymentLayout = findViewById(R.id.payment_layout);
        cardView1 = findViewById(R.id.cardView);
        imageView1 = findViewById(R.id.image_card1);
        textView1 = findViewById(R.id.text_card1);
        cardView2 = findViewById(R.id.card_view2);
        imageView2 = findViewById(R.id.image_card2);
        textView2 = findViewById(R.id.text_card2);
        cardView3 = findViewById(R.id.cardView3);
        imageView3 = findViewById(R.id.image_card3);
        textView3 = findViewById(R.id.text_card3);
        cardView4 = findViewById(R.id.card_view4);
        imageView4 = findViewById(R.id.image_card4);
        textView4 = findViewById(R.id.text_card4);
        cardView5 = findViewById(R.id.cardView5);
        imageView5 = findViewById(R.id.image_card5);
        textView5 = findViewById(R.id.text_card5);
        cardView6 = findViewById(R.id.card_view6);
        imageView6 = findViewById(R.id.image_card6);
        textView6 = findViewById(R.id.text_card6);
        nameView = findViewById(R.id.name_textView);
        roleTv = findViewById(R.id.frame_text_view);
        paymentLayout.setVisibility(View.GONE);
        paymentCard = findViewById(R.id.payment_main_card);
        viewModel.getuser().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if (userModel != null){
                    role = userModel.getRole();
                    isPaymentAdded = userModel.isPayment();
                    changeActivityState(userModel.getRole());
                    nameView.setText(userModel.getUserName());
                    paymentMethod(userModel.isPayment(),role);
                    isPaymentAdded = userModel.isPayment();
                    if (userModel.getSerialNo().equals("not signed")){
                        activeStateTv.setText("Deactivated");
                    }else{
                        activeStateTv.setText("Activated");
                    }
                }
            }
        });


        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
        cardView6.setOnClickListener(this);
        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
                //finish();
            }
        });



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.getUsers(user.getEmail());
    }

    @Override
    public void onClick(View v) {

        if (isPaymentAdded) {
            switch (v.getId()) {
                case R.id.cardView:
                    if (role.equals("admin")) {

                        startActivity(new Intent(getApplicationContext(), ParkingSlotsActivity.class));
                        break;
                    } else if (role.equals("user")) {
                        startActivity(new Intent(getApplicationContext(), ParkingSlotsActivity.class));
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }
                case R.id.card_view2:
                    if (role.equals("admin")) {
                        startActivity(new Intent(getApplicationContext(),UsersListActivity.class));
                        break;
                    } else if (role.equals("user")) {
                        //startActivity(new Intent(getApplicationContext(), ParkingSlotsActivity.class));
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }
                case R.id.cardView3:
                    if (role.equals("admin")) {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        break;
                    } else if (role.equals("user")) {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }
                case R.id.card_view4:
                    if (role.equals("admin")) {

                    } else if (role.equals("user")) {
                        //feedback
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }
                case R.id.cardView5:
                    if (role.equals("admin")) {

                    } else if (role.equals("user")) {
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }
                case R.id.card_view6:
                    if (role.equals("admin")) {

                    } else if (role.equals("user")) {
                        break;
                    } else if (role.equals("disable")) {
                        break;
                    }

                default:
                    break;
            }
        }

    }

    void changeActivityState(String userRole){
        if (userRole.equals("admin")){
            adminState();
        }else if (userRole.equals("user")){

            userState();
        }else if(userRole.equals("disable")){

        }
    }

    void adminState(){
        cardView5.setVisibility(View.VISIBLE);
        cardView6.setVisibility(View.VISIBLE);
        textView1.setText("Parking");
        imageView1.setBackground(getDrawable(R.drawable.parking));
        imageView2.setBackground(getDrawable(R.drawable.profile));
        textView2.setText("Users");
        imageView3.setBackground(getDrawable(R.drawable.profile));
        textView3.setText("Profile");
    }

    void userState(){
        cardView5.setVisibility(View.GONE);
        cardView6.setVisibility(View.GONE);
        textView1.setText("Parking");
        imageView1.setBackground(getDrawable(R.drawable.parking));
        textView2.setText("Booking History");
        imageView2.setBackground(getDrawable(R.drawable.history));
        textView3.setText("Profile");
        imageView3.setBackground(getDrawable(R.drawable.profile));
        textView4.setText("FeedBack");
        imageView4.setBackground(getDrawable(R.drawable.feedback));


    }

    void paymentMethod(boolean payment,String role){
        if (!payment){
            paymentLayout.setVisibility(View.VISIBLE);

        }else{
            paymentLayout.setVisibility(View.GONE);
            isPaymentAdded = true;
        }

        if (role.equals("disable")){
            paymentLayout.setVisibility(View.VISIBLE);
            paymentCard.setVisibility(View.GONE);
            roleTv.setText("Your Account Is Disabled");
            activeStateTv.setText("Deactivated");
            isPaymentAdded = false;

        }
    }
}
