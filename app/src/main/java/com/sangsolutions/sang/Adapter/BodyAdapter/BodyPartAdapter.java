package com.sangsolutions.sang.Adapter.BodyAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.Customer.Customer;
import com.sangsolutions.sang.Adapter.Customer.CustomerAdapter;
import com.sangsolutions.sang.R;

import java.util.List;

public class BodyPartAdapter extends RecyclerView.Adapter<BodyPartAdapter.ViewHolder> {

    Context context;
    List<BodyPart>list;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(BodyPart bodyPart, int position);

    }

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
        holder.parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(list.get(position),position);
            }
        });

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,unit,qty,rate,gross,vat,net;
        LinearLayout parentLinear;
        ImageView delete;
        CardView parentCard;
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
            delete=itemView.findViewById(R.id.delete);
            parentCard=itemView.findViewById(R.id.cardView_body_adapter);

        }
    }
}
