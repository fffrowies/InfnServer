package com.ffrowies.infnserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.R;

public class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txvName, txvEmail, txvPhone, txvAddress;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerViewHolder(@NonNull View itemView) {
        super(itemView);

        txvName = (TextView) itemView.findViewById(R.id.txvName);
        txvEmail = (TextView) itemView.findViewById(R.id.txvEmail);
        txvPhone = (TextView) itemView.findViewById(R.id.txvPhone);
        txvAddress = (TextView) itemView.findViewById(R.id.txvAddress);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
