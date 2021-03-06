package com.sangsolutions.sang.Adapter.SalesPurchaseReportAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.sangsolutions.sang.R;

import java.util.List;

public class SalesPurchaseReportAdapter extends RecyclerView.Adapter<SalesPurchaseReportAdapter.ViewHolder> {

    List<SalesPurchaseReport>list;
    Context context;
    public SalesPurchaseReportAdapter(Context context, List<SalesPurchaseReport> list) {
        this.context=context;
        this.list=list;

    }

    @NonNull
    @Override
    public SalesPurchaseReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.report_sales_purchase_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesPurchaseReportAdapter.ViewHolder holder, int position) {

        holder.docNo.setText(list.get(position).sDocNo);
        holder.date.setText(list.get(position).Date);
        holder.customer1.setText(list.get(position).Account1);
        holder.customer2.setText(list.get(position).Account2);
        holder.product.setText(list.get(position).Product);
        holder.qty.setText(list.get(position).qty);
        holder.docNo.setText(list.get(position).sDocNo);
        holder.rate.setText(list.get(position).rate);
        holder.disc.setText(list.get(position).disc);
        holder.vat.setText(list.get(position).vat);

        holder.tag1.setText(list.get(position).Tag1);
        holder.tag2.setText(list.get(position).Tag2);
        holder.tag3.setText(list.get(position).Tag3);
        holder.tag4.setText(list.get(position).Tag4);
        holder.tag5.setText(list.get(position).Tag5);
        holder.tag6.setText(list.get(position).Tag6);
        holder.tag7.setText(list.get(position).Tag7);
        holder.tag8.setText(list.get(position).Tag8);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView docNo,date,customer1,customer2,product,qty,rate,disc,vat,tag1,tag2,tag3,tag4,tag5,tag6,tag7,tag8;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            docNo=itemView.findViewById(R.id.docNo);
            date=itemView.findViewById(R.id.date);
            customer1=itemView.findViewById(R.id.customer1);
            product=itemView.findViewById(R.id.product);
            customer2=itemView.findViewById(R.id.customer2);
            qty=itemView.findViewById(R.id.qty);
            rate=itemView.findViewById(R.id.rate);
            disc=itemView.findViewById(R.id.disc);
            vat=itemView.findViewById(R.id.vat);

            tag1=itemView.findViewById(R.id.Tag1);
            tag2=itemView.findViewById(R.id.Tag2);
            tag3=itemView.findViewById(R.id.Tag3);
            tag4=itemView.findViewById(R.id.Tag4);
            tag5=itemView.findViewById(R.id.Tag5);
            tag6=itemView.findViewById(R.id.Tag6);
            tag7=itemView.findViewById(R.id.Tag7);
            tag8=itemView.findViewById(R.id.Tag8);


        }
    }
}
