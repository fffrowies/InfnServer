package com.ffrowies.infnserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txvGroupName;

    public RoundedImageView rivGroup;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);

        txvGroupName = (TextView) itemView.findViewById(R.id.txvGroupName);
        rivGroup = (RoundedImageView) itemView.findViewById(R.id.rivGroup);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
