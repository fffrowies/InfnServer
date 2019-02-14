package com.ffrowies.infnserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.R;

public class InvoiceViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {

    public TextView txvDate, txvTotal;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public InvoiceViewHolder(@NonNull View itemView) {
        super(itemView);

        txvDate = (TextView) itemView.findViewById(R.id.txvDate);
        txvTotal = (TextView) itemView.findViewById(R.id.txvTotal);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}