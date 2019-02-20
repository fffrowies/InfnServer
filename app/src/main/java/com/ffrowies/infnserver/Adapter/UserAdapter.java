package com.ffrowies.infnserver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ffrowies.infnserver.Models.User;
import com.ffrowies.infnserver.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<User> userList;
    Context context;

    public UserAdapter(Context context) {
        this.userList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<User> newUsers) {
        int initSize = userList.size();
        userList.addAll(newUsers);
        notifyItemRangeChanged(initSize, newUsers.size());
    }

    public void removeLastItem() {
        userList.remove(userList.size() - 1);
    }

    public String getLastItemId() {
        return userList.get(userList.size() - 1).getId();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.user_layout_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.txvName.setText(userList.get(position).getName());
        holder.txvEmail.setText(userList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txvName, txvEmail;
        public UserViewHolder(View itemView) {
            super(itemView);

            txvName = (TextView) itemView.findViewById(R.id.name);
            txvEmail = (TextView) itemView.findViewById(R.id.email);
        }
    }
}
