package com.ffrowies.infnserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.R;
import com.ffrowies.infnserver.Utils.Common;

public class CustomerViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

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
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
