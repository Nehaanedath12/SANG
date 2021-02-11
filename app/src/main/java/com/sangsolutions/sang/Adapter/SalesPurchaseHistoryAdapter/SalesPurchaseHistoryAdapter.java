package com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.InvoiceAdapter.Invoice;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceSelectedAdapter;
import com.sangsolutions.sang.R;

import java.util.List;

public class SalesPurchaseHistoryAdapter extends RecyclerView.Adapter<SalesPurchaseHistoryAdapter.ViewHolder> {

    List<SalesPurchaseHistory>list;
    Context context;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {

        void onItemClick(int iTransId, int position);
    }

    public SalesPurchaseHistoryAdapter(Context context, List<SalesPurchaseHistory> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public SalesPurchaseHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sale_purchase_history_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("customerName",list.get(position).sAccount1);
        holder.customerName.setText(list.get(position).sAccount1);
        holder.docNo.setText(list.get(position).sDocNo);
        holder.date.setText(list.get(position).sDate);
        if (position % 2 == 0) {
            holder.parentCard.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.parentCard.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        holder.parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(list.get(position).iTransId,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,docNo,customerName;
        CardView parentCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentCard=itemView.findViewById(R.id.parentCard);
            date=itemView.findViewById(R.id.Date);
            docNo=itemView.findViewById(R.id.DocNo);
            customerName=itemView.findViewById(R.id.customerName);

        }
    }
}
