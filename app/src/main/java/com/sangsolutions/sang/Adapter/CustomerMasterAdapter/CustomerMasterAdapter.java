package com.sangsolutions.sang.Adapter.CustomerMasterAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistory;
import com.sangsolutions.sang.Adapter.SalesPurchaseHistoryAdapter.SalesPurchaseHistoryAdapter;
import com.sangsolutions.sang.Database.CustomerMasterClass;
import com.sangsolutions.sang.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerMasterAdapter extends RecyclerView.Adapter<CustomerMasterAdapter.ViewHolder> {

    List<CustomerMasterClass>list;
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


        void onItemClick(int iId, int position);

        void onDeleteClick(int iId, int position);

        void onItemLongClick(int iId, int position);
    }

    public CustomerMasterAdapter(Context context, List<CustomerMasterClass> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public CustomerMasterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.customer_master_history_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerMasterAdapter.ViewHolder holder, int position) {
        holder.customerName.setText(list.get(position).name);
        holder.id.setText(String.valueOf(list.get(position).iId));
        holder.customerCode.setText(list.get(position).code);
        if (position % 2 == 0) {
            holder.parentCard.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {
            holder.parentCard.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        holder.parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(list.get(position).iId,position);
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
                        onClickListener.onDeleteClick(list.get(position).iId,position);
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
                    onClickListener.onItemLongClick(list.get(position).iId,position);
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
        TextView id,customerCode,customerName;
        CardView parentCard;
        ImageView delete,img_check;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentCard=itemView.findViewById(R.id.parentCard);
            id=itemView.findViewById(R.id.id);
            customerCode=itemView.findViewById(R.id.customerCode);
            customerName=itemView.findViewById(R.id.customerName);
            delete=itemView.findViewById(R.id.delete);
            img_check=itemView.findViewById(R.id.check);
        }
    }
}
