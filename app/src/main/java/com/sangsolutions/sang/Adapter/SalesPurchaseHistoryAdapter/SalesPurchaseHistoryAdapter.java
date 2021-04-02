package com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.R;

import java.util.ArrayList;
import java.util.List;

public class SalesPurchaseHistoryAdapter extends RecyclerView.Adapter<SalesPurchaseHistoryAdapter.ViewHolder> {

    List<SalesPurchaseHistory>list;
    Context context;
    private OnClickListener onClickListener;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }


    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.img_check.setVisibility(View.VISIBLE);
            holder.parentCard.setBackgroundColor(Color.parseColor("#fca1a3"));
        } else {
            holder.img_check.setVisibility(View.GONE);
            holder.parentCard.setBackgroundColor(Color.WHITE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }
    public int getSelectedItemCount() {
        return selected_items.size();
    }



    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }
    public interface OnClickListener {

        void onItemClick(int iTransId, int position);


        void onItemLongClick(int position);

        void onDeleteClick(int iTransId, String sDocNo);
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete!");
                builder.setMessage("Do you want to delete?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickListener.onDeleteClick(list.get(position).iTransId,list.get(position).sDocNo);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        holder.parentCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (onClickListener == null) return false;
                else {
                    onClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
        toggleCheckedIcon(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,docNo,customerName;
        CardView parentCard;
        ImageView delete,img_check;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentCard=itemView.findViewById(R.id.parentCard);
            date=itemView.findViewById(R.id.Date);
            docNo=itemView.findViewById(R.id.DocNo);
            customerName=itemView.findViewById(R.id.customerName);
            delete=itemView.findViewById(R.id.delete);
            img_check=itemView.findViewById(R.id.check);

        }
    }
}
