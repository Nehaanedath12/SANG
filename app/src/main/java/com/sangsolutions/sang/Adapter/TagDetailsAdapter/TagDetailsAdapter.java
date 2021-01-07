package com.sangsolutions.sang.Adapter.TagDetailsAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.R;

import java.util.List;

public class TagDetailsAdapter extends RecyclerView.Adapter<TagDetailsAdapter.ViewHolder> {
    List<TagDetails>list;
    Context context;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick( TagDetails tagDetails, int position);

    }
    public TagDetailsAdapter(Context context, List<TagDetails> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public TagDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagDetailsAdapter.ViewHolder holder, int position) {

        holder.tagName.setText(list.get(position).sName);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
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
        LinearLayout linearLayout;
        TextView tagName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.customerLinear);
            tagName=itemView.findViewById(R.id.customerName);
        }
    }
}
