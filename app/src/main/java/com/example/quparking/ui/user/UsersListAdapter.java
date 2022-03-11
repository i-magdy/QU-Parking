package com.example.quparking.ui.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quparking.R;
import com.example.quparking.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {


    private ListItemOnClickListener onClickListener;
    private List<UserModel> data;

    UsersListAdapter(ListItemOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        data = new ArrayList<>();

    }

    @NonNull
    @Override
    public UsersListAdapter.UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListAdapter.UsersListViewHolder holder, int position) {


            holder.name.setText(data.get(position).getUserName());
            holder.email.setText(data.get(position).getEmail());

            if (data.get(position).isPayment()){
                holder.paymentIv.setVisibility(View.VISIBLE);
            }else {
                holder.paymentIv.setVisibility(View.INVISIBLE);
            }

            if (data.get(position).getSerialNo().equals("not signed")){
                holder.activateIv.setVisibility(View.INVISIBLE);

            }else {
                holder.activateIv.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public int getItemCount() {
       if (data != null){
           return data.size();
       }

       return 0;

    }

    public class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView email;
        ImageView paymentIv;
        ImageView activateIv;
        private UsersListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_text_view);
            email = itemView.findViewById(R.id.email_text_view);
            paymentIv = itemView.findViewById(R.id.payment_iv);
            activateIv = itemView.findViewById(R.id.activate_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            onClickListener.onListItemClicked(position);
        }
    }

    interface ListItemOnClickListener{
        void onListItemClicked(int itemPosition);
    }


    void setData(List<UserModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
