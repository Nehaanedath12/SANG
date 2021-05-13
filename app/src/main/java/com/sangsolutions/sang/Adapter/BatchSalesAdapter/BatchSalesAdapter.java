package com.sangsolutions.sang.Adapter.BatchSalesAdapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.sangsolutions.sang.R;

import java.util.ArrayList;
import java.util.List;

public class BatchSalesAdapter extends RecyclerView.Adapter<BatchSalesAdapter.ViewHolder> {

    List<BatchSales>list;
    Context context;
//    private OnClickListener onClickListener;
//    public SparseBooleanArray selected_item;
//    private int current_index = -1;


//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//        selected_item = new SparseBooleanArray();
//    }
//
//    public interface OnClickListener {
//
//        void OnItemClick(BatchSales batchSales, int position);
//    }
//
//    private void toggleCheckedIcon(ViewHolder holder, int position) {
//        if (selected_item.get(position, false)) {
////            holder.check.setVisibility(View.VISIBLE);
//            holder.parent.setBackgroundColor(Color.parseColor("#fca1a3"));
//        } else {
////            holder.check.setVisibility(View.GONE);
//            holder.parent.setBackgroundColor(Color.WHITE);
//        }
//        if (current_index == position) {
//            resetCurrentIndex();
//        }
//    }
//
//    private void resetCurrentIndex() {
//        current_index = -1;
//    }
//
//    public void clearSelection() {
//        selected_item.clear();
//        notifyDataSetChanged();
//    }
//
//    public void toggleSelection(int position) {
//        current_index = position;
//        if (selected_item.get(position, false)) {
//            selected_item.delete(position);
//        } else {
//            selected_item.put(position, true);
//        }
//        notifyItemChanged(position);
//    }
//
//    public int getSelectedItemCount() {
//        return selected_item.size();
//    }
//
//    public List<Integer> getSelectedItems() {
//        List<Integer> items = new ArrayList<>(selected_item.size());
//        for (int i = 0; i < selected_item.size(); i++) {
//            items.add(selected_item.keyAt(i));
//        }
//        return items;
//    }


    public BatchSalesAdapter(Context context, List<BatchSales> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public BatchSalesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.batch_sales_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchSalesAdapter.ViewHolder holder, int position) {

        holder.batchName.setText(list.get(position).batch);
        holder.mfDate.setText(list.get(position).mfDate);
        holder.expDate.setText(list.get(position).expDate);
        holder.qty.setText(String.valueOf(list.get(position).qty));
        if(list.get(position).enterQty!=0){
            holder.reqQty.setText(String.valueOf(list.get(position).enterQty));

        }
        Log.d("reqQtyy",list.get(position).enterQty+"");

        holder.reqQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if (Integer.parseInt(s.toString()) > list.get(position).qty) {
                        holder.reqQty.setError("Should not be greater");
                    }else {
                        list.set(position,
                                new BatchSales(
                                        list.get(position).batch,
                                        list.get(position).mfDate,
                                        list.get(position).expDate,
                                        list.get(position).qty,
                                        Integer.parseInt(s.toString()),
                                        list.get(position).iId,
                                        list.get(position).iProduct));
                    }
                }else {
                    list.set(position,
                            new BatchSales(
                                    list.get(position).batch,
                                    list.get(position).mfDate,
                                    list.get(position).expDate,
                                    list.get(position).qty,
                                    0,
                                    list.get(position).iId,
                                    list.get(position).iProduct));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        toggleCheckedIcon(holder, position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView batchName,qty,mfDate,expDate;
        LinearLayout parent;
        ImageView check;
        EditText reqQty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batchName =itemView.findViewById(R.id.batchName);
            mfDate=itemView.findViewById(R.id.mfDate);
            expDate=itemView.findViewById(R.id.expDate);
            qty=itemView.findViewById(R.id.qty);
            parent=itemView.findViewById(R.id.parent);
            check=itemView.findViewById(R.id.check);
            reqQty=itemView.findViewById(R.id.reqQty);
        }
    }
}
