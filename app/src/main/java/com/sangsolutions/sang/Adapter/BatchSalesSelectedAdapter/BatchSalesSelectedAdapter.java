package com.sangsolutions.sang.Adapter.BatchSalesSelectedAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchase;
import com.sangsolutions.sang.Adapter.BatchPurchaseAdapter.BatchPurchaseAdapter;
import com.sangsolutions.sang.Adapter.BatchSalesAdapter.BatchSales;
import com.sangsolutions.sang.Adapter.InvoiceAdapter.InvoiceSelectedAdapter;
import com.sangsolutions.sang.R;

import java.util.List;

public class BatchSalesSelectedAdapter extends RecyclerView.Adapter<BatchSalesSelectedAdapter.ViewHolder> {

    List<BatchSales>list;
    Context context;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(BatchSales batchSales, int position);

    }

    public BatchSalesSelectedAdapter(Context context, List<BatchSales> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public BatchSalesSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_sales_selected_batch,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchSalesSelectedAdapter.ViewHolder holder, int position) {
        holder.batchName.setText(list.get(position).batch);
        holder.mfDate.setText(list.get(position).mfDate);
        holder.expDate.setText(list.get(position).expDate);
        holder.qty.setText(String.valueOf(list.get(position).enterQty));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(list.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView batchName,qty,mfDate,expDate;
        LinearLayout parent;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batchName =itemView.findViewById(R.id.batchName);
            mfDate=itemView.findViewById(R.id.mfDate);
            expDate=itemView.findViewById(R.id.expDate);
            qty=itemView.findViewById(R.id.qty);
            parent=itemView.findViewById(R.id.parent);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
