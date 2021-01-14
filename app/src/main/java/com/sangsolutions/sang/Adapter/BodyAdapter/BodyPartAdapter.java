package com.sangsolutions.sang.Adapter.BodyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.R;

import java.util.List;

public class BodyPartAdapter extends RecyclerView.Adapter<BodyPartAdapter.ViewHolder> {

    Context context;
    List<BodyPart>list;

    public BodyPartAdapter(Context context, List<BodyPart> list) {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public BodyPartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.body_part_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyPartAdapter.ViewHolder holder, int position) {

        Log.d("bodyValues","String.valueOf(list.get(position).qty");
        holder.productName.setText(list.get(position).productName);
        holder.unit.setText(list.get(position).unit);
        holder.qty.setText(String.valueOf(list.get(position).qty));
        holder.rate.setText(String.valueOf(list.get(position).rate));
        holder.gross.setText(String.valueOf(list.get(position).gross));
        holder.vat.setText(String.valueOf(list.get(position).vat));
        holder.net.setText(String.valueOf(list.get(position).net));
        if (position % 2 == 0) {
            holder.parentLinear.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.parentLinear.setBackgroundColor(Color.rgb(255, 255, 255));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,unit,qty,rate,gross,vat,net;
        LinearLayout parentLinear;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.product_name);
            unit=itemView.findViewById(R.id.unit);
            qty=itemView.findViewById(R.id.qty);
            rate=itemView.findViewById(R.id.rate);
            gross=itemView.findViewById(R.id.gross);
            vat=itemView.findViewById(R.id.vat);
            net=itemView.findViewById(R.id.net);
            parentLinear=itemView.findViewById(R.id.parentLinear);

        }
    }
}
