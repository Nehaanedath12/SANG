package com.sangsolutions.sang.Adapter.InvoiceAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPart;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPartAdapter;
import com.sangsolutions.sang.Home;
import com.sangsolutions.sang.Login;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.databinding.DialogueInvoiceAmountBinding;

import java.util.List;

public class InvoiceSelectedAdapter extends RecyclerView.Adapter<InvoiceSelectedAdapter.ViewHolder> {
    Context context;
    List<Invoice>list;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {

        void onItemClick( List<Invoice> list, int position);
    }

    public InvoiceSelectedAdapter(Context context, List<Invoice> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public InvoiceSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_invoice,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceSelectedAdapter.ViewHolder holder, int position) {
        holder.amount.setText(list.get(position).Amount);
        holder.date.setText(list.get(position).DocDate);
        holder.number.setText(list.get(position).DocNo);
        holder.delete.setVisibility(View.VISIBLE);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete!")
                        .setMessage("Do you want to Delete ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               list.remove(position);
                               notifyDataSetChanged();
                               dialog.dismiss();
                                onClickListener.onItemClick( list,position);

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
                View view = LayoutInflater.from(context).inflate(R.layout.dialogue_invoice_amount, null, false);
                AlertDialog.Builder builderMain=new AlertDialog.Builder(context);
                builderMain.setView(view);
                builderMain.setCancelable(false);
                AlertDialog alertDialog_Main = builderMain.create();
                alertDialog_Main.show();
                TextView cancel,apply;
                EditText edit;
                cancel=view.findViewById(R.id.cancel);
                apply=view.findViewById(R.id.apply);
                edit=view.findViewById(R.id.amountEdit);
                edit.setText(list.get(position).Amount);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog_Main.dismiss();
                    }
                });

                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(position).Amount=edit.getText().toString();
                        notifyDataSetChanged();
                        alertDialog_Main.dismiss();
                        onClickListener.onItemClick( list,position);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,number,amount;
        LinearLayout parent;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.invoice_date);
            number=itemView.findViewById(R.id.invoice_no);
            amount=itemView.findViewById(R.id.amount);
            parent=itemView.findViewById(R.id.parent);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
