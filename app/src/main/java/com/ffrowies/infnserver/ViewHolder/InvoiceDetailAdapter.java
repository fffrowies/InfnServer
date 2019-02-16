package com.ffrowies.infnserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ffrowies.infnserver.Models.Order;
import com.ffrowies.infnserver.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class InvoiceDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txvItemDescription, txvItemAmount;

    public InvoiceDetailViewHolder(@NonNull View itemView) {
        super(itemView);
        txvItemDescription = (TextView) itemView.findViewById(R.id.txvItemDescription);
        txvItemAmount = (TextView) itemView.findViewById(R.id.txvItemAmount);
    }
}

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailViewHolder> {

    List<Order> myItemsInvoice;

    public InvoiceDetailAdapter(List<Order> myItemsInvoice) {
        this.myItemsInvoice = myItemsInvoice;
    }

    @NonNull
    @Override
    public InvoiceDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invoice_detail, parent, false);
        return new InvoiceDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceDetailViewHolder holder, int position) {
        Order itemsInvoice = myItemsInvoice.get(position);

        holder.txvItemDescription.setText(String.format("%s", itemsInvoice.getProductName()));

        int amount = Integer.parseInt(itemsInvoice.getPrice());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.txvItemAmount.setText(String.format("%s", fmt.format(amount)));
    }

    @Override
    public int getItemCount() {
        return myItemsInvoice.size();
    }
}
