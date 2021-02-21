package com.sangsolutions.sang.Adapter.InvoiceAdapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    List<Invoice>list;
    Context context;
    private OnClickListener onClickListener;
    public SparseBooleanArray selected_item;
    private int current_index = -1;




    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_item = new SparseBooleanArray();
    }

    public interface OnClickListener {

        void OnItemClick(Invoice invoice, int position);
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_item.get(position, false)) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }
        if (current_index == position) {
            resetCurrentIndex();
        }
    }

    private void resetCurrentIndex() {
        current_index = -1;
    }

    public void clearSelection() {
        selected_item.clear();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        current_index = position;
        if (selected_item.get(position, false)) {
            selected_item.delete(position);
        } else {
            selected_item.put(position, true);
        }
        notifyItemChanged(position);
    }

    public int getSelectedItemCount() {
        return selected_item.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_item.size());
        for (int i = 0; i < selected_item.size(); i++) {
            items.add(selected_item.keyAt(i));
        }
        return items;
    }


    public InvoiceAdapter(Context context, List<Invoice> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_invoice,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        String amount=df.format(list.get(position).Amount);
        holder.amount.setText(amount);
        holder.date.setText(list.get(position).InvDate);
        holder.number.setText(String.valueOf(list.get(position).InvNo));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null) {
                    onClickListener.OnItemClick(list.get(position), position);
                }
                }
        });
        toggleCheckedIcon(holder, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,number,amount;
        LinearLayout parent;
        ImageView check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.invoice_date);
            number=itemView.findViewById(R.id.invoice_no);
            amount=itemView.findViewById(R.id.amount);
            parent=itemView.findViewById(R.id.parent);
            check=itemView.findViewById(R.id.check);
        }
    }
}
