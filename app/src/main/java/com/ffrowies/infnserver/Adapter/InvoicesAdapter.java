package com.ffrowies.infnserver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ffrowies.infnserver.Models.Invoice;
import com.ffrowies.infnserver.R;

import java.util.ArrayList;
import java.util.List;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoicesViewHolder> {
    List<Invoice> invoicesList;
    Context context;

    public InvoicesAdapter(Context context) {
        this.invoicesList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<Invoice> newInvoices) {
        int initSize = invoicesList.size();
        invoicesList.addAll(newInvoices);
        notifyItemRangeChanged(initSize, newInvoices.size());
    }

    public void removeLastItem() {
        invoicesList.remove(invoicesList.size() - 1);
    }

    public String getLastItemId() {
        return invoicesList.get(invoicesList.size() - 1).getDate();
    }

    @NonNull
    @Override
    public InvoicesAdapter.InvoicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_invoice_item, parent, false);
        return new InvoicesAdapter.InvoicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoicesAdapter.InvoicesViewHolder holder, int position) {
        holder.txvDate.setText(invoicesList.get(position).getDate());
        holder.txvTotal.setText(invoicesList.get(position).getTotal());
    }

    @Override
    public int getItemCount() {
        return invoicesList.size();
    }

    public class InvoicesViewHolder extends RecyclerView.ViewHolder {

        TextView txvDate, txvTotal;
        public InvoicesViewHolder(View itemView) {
            super(itemView);

            txvDate = (TextView) itemView.findViewById(R.id.txvDate);
            txvTotal = (TextView) itemView.findViewById(R.id.txvTotal);
        }
    }

}
