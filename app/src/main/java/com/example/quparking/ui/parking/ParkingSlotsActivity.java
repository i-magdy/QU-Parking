package com.example.quparking.ui.parking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;
import com.example.quparking.service.A1Service;
import com.example.quparking.service.A2Service;
import com.example.quparking.service.B1Service;
import com.example.quparking.service.B2Service;
import com.example.quparking.ui.profile.ProfileActivity;
import com.example.quparking.ui.user.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ParkingSlotsActivity extends AppCompatActivity implements CardView.OnLongClickListener{


    FirebaseAuth auth ;
    FirebaseUser user ;
    String date;
    private boolean isA1Clickable = false;
    private boolean isA1Busy = true;
    private boolean isA1Booked = true;
    private boolean isA2Clickable = false;
    private boolean isA2Busy = true;
    private boolean isA2Booked = true;
    private boolean isB1Clickable = false;
    private boolean isB1Busy = true;
    private boolean isB1Booked = true;
    private boolean isB2Clickable = false;
    private boolean isB2Busy = true;
    private boolean isB2Booked = true;
    private int serviceTime = 0;
    private TextView A1Tv;
    private TextView A2Tv;
    private TextView B1Tv;
    private TextView B2Tv;
    CardView slotA1Card;
    private ImageView parkingA1Iv;
    private TextView bookedA1Tv;
    CardView slotA2Card;
    private ImageView parkingA2Iv;
    private TextView bookedA2Tv;
    CardView slotB1Card;
    private ImageView parkingB1Iv;
    private TextView bookedB1Tv;
    CardView slotB2Card;
    private ImageView parkingB2Iv;
    private TextView bookedB2Tv;
    private String serialTag="not signed";
    private Toolbar bar;
    private String a1SerialTag;
    private String a2SerialTag;
    private String b1SerialTag;
    private String b2SerialTag;
    private String role;
    private TextView bookTv;
    private boolean isBooked = false;

    private ParkingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_slots);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        bar = findViewById(R.id.app_bar);

        if (user == null ){
            finish();
        }
        viewModel = new ViewModelProvider(this).get(ParkingViewModel.class);
        viewModel.getUsers(user.getEmail());
        viewModel.getuser().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if (userModel != null){
                    serialTag = userModel.getSerialNo();
                    Log.i("testParkingViewmodel",userModel.getSerialNo());
                    role = userModel.getRole();
                    if (userModel.getRole().equals("admin")){
                        setSupportActionBar(bar);
                        bar.inflateMenu(R.menu.parking);
                    }
                }
            }
        });
        bookTv = findViewById(R.id.book_state_tv);

        A1Tv = findViewById(R.id.slot_one_tv);
        A2Tv = findViewById(R.id.slot_two_tv);
        B1Tv = findViewById(R.id.slot_three_tv);
        B2Tv = findViewById(R.id.slot_four_tv);
        slotA1Card = findViewById(R.id.slot_one_card);
        parkingA1Iv = findViewById(R.id.parking_a1_iv);
        bookedA1Tv = findViewById(R.id.book_a1_tv);
        slotA2Card = findViewById(R.id.slot_two_card);
        parkingA2Iv = findViewById(R.id.parking_a2_iv);
        bookedA2Tv = findViewById(R.id.book_a2_tv);
        slotB1Card = findViewById(R.id.slot_three_card);
        parkingB1Iv = findViewById(R.id.parking_b1_iv);
        bookedB1Tv = findViewById(R.id.book_b1_tv);
        slotB2Card = findViewById(R.id.slot_four_card);
        parkingB2Iv = findViewById(R.id.parking_b2_iv);
        bookedB2Tv = findViewById(R.id.book_b2_tv);

        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("bookingTimeout");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = 0;
                if (dataSnapshot.getValue(Integer.class) != null){
                    state = dataSnapshot.getValue(Integer.class);
                }
                Log.i("bookingTimeout",""+state);
                if (state != 0){
                    serviceTime = state;
                }else{
                    serviceTime = 2;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",new Locale("en"));
        date = format.format(c.getTime());

        getSlotA1State();
        checkA1Booking();
        getSlotA2State();
        checkA2Booking();
        getSlotB1State();
        checkB1Booking();
        getSlotB2State();
        checkB2Booking();


        slotA1Card.setOnLongClickListener(this);
        slotA2Card.setOnLongClickListener(this);
        slotB1Card.setOnLongClickListener(this);
        slotB2Card.setOnLongClickListener(this);
        slotA1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isA1Clickable && !isA1Busy && !isA1Booked && serviceTime != 0 && !serialTag.equals("not signed") && !isBooked) {
                    bookParking("a1", 1);
                    bookingTag("a1", serialTag);
                    isBooked = true;
                    bookTv.setText("You booked A1");
                    parkingA1Service();
                }
                if (isA1Booked && !serialTag.equals("not signed")) {
                    if (a1SerialTag.equals(serialTag)) {
                        bookParking("a1", 0);
                        bookingTag("a1", "serialTag");
                        isBooked = false;
                        bookTv.setText(null);
                    } else {
                        Toast.makeText(getApplicationContext(), "already booked", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        slotA2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isA2Clickable && !isA2Busy && !isA2Booked && serviceTime != 0 && !serialTag.equals("not signed") && !isBooked) {
                    bookParking("a2", 1);
                    bookingTag("a2", serialTag);
                    isBooked = true;
                    bookTv.setText("You booked A2");
                    parkingA2Service();
                }
                if (isA2Booked && !serialTag.equals("not signed")) {
                    if (a2SerialTag.equals(serialTag)) {
                        bookParking("a2", 0);
                        bookingTag("a2", "serialTag");
                        isBooked = false;
                        bookTv.setText(null);
                    } else {
                        Toast.makeText(getApplicationContext(), "already booked", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        slotB1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isB1Clickable && !isB1Busy && !isB1Booked && serviceTime != 0 && !serialTag.equals("not signed") && !isBooked) {
                    bookParking("b1", 1);
                    bookingTag("b1", serialTag);
                    parkingB1Service();
                    isBooked = true;
                    bookTv.setText("You booked B1");
                }

                if (isB1Booked && !serialTag.equals("not signed")) {
                    if (b1SerialTag.equals(serialTag)) {
                        isBooked = false;
                        bookTv.setText(null);
                        bookParking("b1", 0);
                        bookingTag("b1", "serialTag");

                    } else {
                        Toast.makeText(getApplicationContext(), "already booked", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        slotB2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isB2Clickable && !isB2Busy && !isB2Booked && serviceTime != 0 && !serialTag.equals("not signed") && !isBooked) {
                    bookParking("b2", 1);
                    bookingTag("b2", serialTag);
                    parkingB2Service();
                    isBooked = true;
                    bookTv.setText("You booked B2");
                }
                if (isB2Booked && !serialTag.equals("not signed")) {
                    if (b2SerialTag.equals(serialTag)) {
                        bookParking("b2", 0);
                        bookingTag("b2", "serialTag");
                        isBooked = false;
                        bookTv.setText(null);
                    } else {
                        Toast.makeText(getApplicationContext(), "already booked", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    void showStateBook(){
        if (serialTag.equals(a1SerialTag)){
            isBooked = true;
            bookTv.setText("You booked A1");
        }else if (serialTag.equals(a2SerialTag)){
            isBooked = true;
            bookTv.setText("You booked A2");
        }else if (serialTag.equals(b1SerialTag)){
            isBooked = true;
            bookTv.setText("You booked B1");
        }else if (serialTag.equals(b2SerialTag)){
            isBooked = true;
            bookTv.setText("You booked B1");
        }else{
            isBooked = false;
            bookTv.setText(null);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parking,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_booking) {
            showDialog();
                return true;

        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    void bookingTag(String park,String tag){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("booking").child(park).child("id").setValue(tag);
    }
    void bookParking(String park, int state){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("booking").child(park).child("state").setValue(state);
    }
    void parkingA1Service(){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(A1Service.class)
                .setInitialDelay(serviceTime, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(request);
    }

    void parkingA2Service(){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(A2Service.class)
                .setInitialDelay(serviceTime, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(request);
    }

    void parkingB1Service(){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(B1Service.class)
                .setInitialDelay(serviceTime, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(request);
    }

    void parkingB2Service(){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(B2Service.class)
                .setInitialDelay(serviceTime, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(request);
    }

    void getSlotA1State(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("slots").child("slot-1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                if (state == 0){
                    isA1Busy = false;
                    A1Tv.setText("Available");
                    A1Tv.setBackground(getDrawable(R.drawable.slote_state_avalible));
                }else{
                    isA1Busy = true;
                    bookParking("a1",0);
                    bookingTag("a2","serialTag");
                    A1Tv.setText("Busy");
                    A1Tv.setBackground(getDrawable(R.drawable.slote_state_busy));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void showDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Booking Timeout");
        alert.setMessage("Enter time in minutes,current time is : "+serviceTime);
        alert.setView(edittext);
        alert.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                if (!edittext.getEditableText().toString().isEmpty()) {
                    int i = Integer.parseInt(edittext.getEditableText().toString());
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    mDatabase.child("bookingTimeout").setValue(i);
                    Toast.makeText(getApplicationContext(), "Edited", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });

        alert.show();
    }
    void getSlotA2State(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("slots").child("slot-2");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                if (state == 0){
                    isA2Busy = false;
                    A2Tv.setText("Available");
                    A2Tv.setBackground(getDrawable(R.drawable.slote_state_avalible));
                }else{
                    bookParking("a2",0);
                    bookingTag("a2","serialTag");
                    isA2Busy = true;
                    A2Tv.setText("Busy");
                    A2Tv.setBackground(getDrawable(R.drawable.slote_state_busy));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    void getSlotB1State(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("slots").child("slot-3");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                if (state == 0){
                    isB1Busy = false;
                    B1Tv.setText("Available");
                    B1Tv.setBackground(getDrawable(R.drawable.slote_state_avalible));
                }else{
                    bookParking("b1",0);
                    bookingTag("b1","serialTag");
                    isB1Busy = true;
                    B1Tv.setText("Busy");
                    B1Tv.setBackground(getDrawable(R.drawable.slote_state_busy));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void getSlotB2State(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("slots").child("slot-4");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                if (state == 0){
                    isB2Busy = false;
                    B2Tv.setText("Available");
                    B2Tv.setBackground(getDrawable(R.drawable.slote_state_avalible));
                }else{
                    bookParking("b2",0);
                    bookingTag("b2","serialTag");
                    isB2Busy = true;
                    B2Tv.setText("Busy");
                    B2Tv.setBackground(getDrawable(R.drawable.slote_state_busy));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void checkA1Booking(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("booking").child("a1").child("state");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                showStateBook();
                if (state == 0){
                    isA1Booked = false;
                    parkingA1Iv.setImageDrawable(getDrawable(R.drawable.park));
                    bookedA1Tv.setVisibility(View.INVISIBLE);
                }else{
                    isA1Booked = true;
                    parkingA1Iv.setImageDrawable(getDrawable(R.drawable.parkbooked));
                    bookedA1Tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference referenceId = data.getReference().child("booking").child("a1").child("id");
        referenceId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                Log.i("test",""+id);
                isA1Clickable = true;
                if (id != null){
                    a1SerialTag = id;
                    showStateBook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    void checkA2Booking(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("booking").child("a2").child("state");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                showStateBook();
                if (state == 0){
                    isA2Booked = false;
                    parkingA2Iv.setImageDrawable(getDrawable(R.drawable.park));
                    bookedA2Tv.setVisibility(View.INVISIBLE);
                }else{
                    isA2Booked = true;
                    parkingA2Iv.setImageDrawable(getDrawable(R.drawable.parkbooked));
                    bookedA2Tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference referenceId = data.getReference().child("booking").child("a2").child("id");
        referenceId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                Log.i("test",""+id);
                isA2Clickable = true;
                if (id != null){
                    a2SerialTag = id;
                    showStateBook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    void checkB1Booking(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("booking").child("b1").child("state");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                showStateBook();
                if (state == 0){
                    isB1Booked = false;
                    parkingB1Iv.setImageDrawable(getDrawable(R.drawable.park));
                    bookedB1Tv.setVisibility(View.INVISIBLE);
                }else{
                    isB1Booked = true;
                    parkingB1Iv.setImageDrawable(getDrawable(R.drawable.parkbooked));
                    bookedB1Tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference referenceId = data.getReference().child("booking").child("b1").child("id");
        referenceId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                Log.i("test",""+id);
                isB1Clickable = true;
                if (id != null){
                    b1SerialTag = id;
                    showStateBook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void checkB2Booking(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance("https://qu-smartparking-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference reference = data.getReference().child("booking").child("b2").child("state");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = dataSnapshot.getValue(Integer.class);
                Log.i("test",""+state);
                showStateBook();
                if (state == 0){
                    isB2Booked = false;
                    parkingB2Iv.setImageDrawable(getDrawable(R.drawable.park));
                    bookedB2Tv.setVisibility(View.INVISIBLE);
                }else{
                    isB2Booked = true;
                    parkingB2Iv.setImageDrawable(getDrawable(R.drawable.parkbooked));
                    bookedB2Tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference referenceId = data.getReference().child("booking").child("b2").child("id");
        referenceId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                Log.i("test",""+id);
                isB2Clickable = true;
                if (id != null){
                    b2SerialTag = id;
                    showStateBook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null ){
            finish();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.slot_one_card:
                if (role.equals("admin")){
                    changeBookingState("a1",a1SerialTag,isA1Booked);
                    return false;
                }
            case R.id.slot_two_card:
                if (role.equals("admin")){
                    changeBookingState("a2",a2SerialTag,isA2Booked);
                    return false;
                }
            case R.id.slot_three_card:
                if (role.equals("admin")){
                    changeBookingState("b1",b1SerialTag,isB1Booked);
                    return false;
                }
            case R.id.slot_four_card:
                if (role.equals("admin")){
                    changeBookingState("b2",b2SerialTag,isB2Booked);
                    return false;
                }
            default:
                return false;

        }
    }

    void changeBookingState(final String slot, String tag, boolean isBooked) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Change slot state");
        alertDialog.setMessage("This slot is booked by: " + tag);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                bookParking(slot, 0);
                bookingTag(slot,"serialTag");
                dialog.dismiss();

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (isBooked) {
            alertDialog.show();
        }
    }

}
