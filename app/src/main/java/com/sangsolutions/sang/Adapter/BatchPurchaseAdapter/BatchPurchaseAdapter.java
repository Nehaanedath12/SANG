package com.sangsolutions.sang.Adapter.BatchPurchaseAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;

import java.util.List;

public class BatchPurchaseAdapter extends RecyclerView.Adapter<BatchPurchaseAdapter.ViewHolder> {

    List<BatchPurchase>list;
    Context context;
    DatabaseHelper helper;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(BatchPurchase batchPurchase, int position);

    }

    public BatchPurchaseAdapter(Context context, List<BatchPurchase> list) {
        this.list=list;
        this.context=context;

    }

    @NonNull
    @Override
    public BatchPurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.batch_puchase_adapter,parent,false);
        helper=new DatabaseHelper(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchPurchaseAdapter.ViewHolder holder, int position) {

        holder.batch.setText(list.get(position).batch);
        holder.qty.setText(String.valueOf(list.get(position).qty));
        holder.expDate.setText(list.get(position).expDate);
        holder.mfDate.setText(list.get(position).mfDate);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("delete!")
                        .setMessage("Do you want to delete this item ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

            }
        });

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
        TextView batch,qty,mfDate,expDate;
        ImageView delete;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delete=itemView.findViewById(R.id.delete);
            batch=itemView.findViewById(R.id.batch);
            qty=itemView.findViewById(R.id.qty);
            mfDate=itemView.findViewById(R.id.mfDate);
            expDate=itemView.findViewById(R.id.expDate);
            parent=itemView.findViewById(R.id.parent);

        }
    }
}
