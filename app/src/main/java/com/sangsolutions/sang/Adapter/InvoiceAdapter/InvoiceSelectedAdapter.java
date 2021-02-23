package com.sangsolutions.sang.Adapter.InvoiceAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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

import com.sangsolutions.sang.R;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceSelectedAdapter extends RecyclerView.Adapter<InvoiceSelectedAdapter.ViewHolder> {
    Context context;
    List<Invoice>list;
    List<Invoice>invoiceSecondList;
    private OnClickListener onClickListener;
    String status;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {

        void onItemClick(List<Invoice> list, int position);
    }

    public InvoiceSelectedAdapter(Context context, List<Invoice> list, List<Invoice> invoiceSecondList) {
        this.context=context;
        this.list=list;
        this.invoiceSecondList=invoiceSecondList;
    }

    @NonNull
    @Override
    public InvoiceSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_invoice,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceSelectedAdapter.ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        String amount=df.format(list.get(position).Amount);
        holder.amount.setText(amount);
        holder.date.setText(list.get(position).InvDate);
        holder.number.setText(String.valueOf(list.get(position).InvNo));
        Log.d("invoicenumber",list.get(position).iTransId+"");
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
                DecimalFormat df = new DecimalFormat("0.00");
                View view = LayoutInflater.from(context).inflate(R.layout.dialogue_invoice_amount, null, false);
                AlertDialog.Builder builderMain=new AlertDialog.Builder(context);
                builderMain.setView(view);
                builderMain.setCancelable(false);
                AlertDialog alertDialog_Main = builderMain.create();
                alertDialog_Main.show();

                TextView cancel,apply,actualAmount;
                EditText edit;
                cancel=view.findViewById(R.id.cancel);
                apply=view.findViewById(R.id.apply);
                edit=view.findViewById(R.id.amountEdit);
                actualAmount=view.findViewById(R.id.actualAmount);
                String amount=df.format(list.get(position).Amount);
                edit.setText(amount);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog_Main.dismiss();
                    }
                });

                apply.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {

                        for (int i=0;i<invoiceSecondList.size();i++){
                            if(list.get(position).getiTransId()==invoiceSecondList.get(i).getiTransId())
                            {
                                if(!edit.getText().toString().equals("")) {
                                    Log.d("amountt",Double.parseDouble(edit.getText().toString())+"");
                                    Log.d("amountts", invoiceSecondList.get(i).getAmount()+"");
                                    if ((Double.parseDouble(edit.getText().toString())) > invoiceSecondList.get(i).getAmount()) {
                                        edit.setError("should not greater than Total amount ");
                                        Log.d("amountts", "if");
//                                        actualAmount.setText("Total Amount: " + invoiceSecondList.get(i).getAmount() + "");
                                    } else {
                                        list.get(position).Amount = Double.parseDouble(edit.getText().toString());
                                        notifyDataSetChanged();
                                        Log.d("amountts", "else");
                                        alertDialog_Main.dismiss();
                                        onClickListener.onItemClick(list, position);
                                    }
                                }else {
                                    alertDialog_Main.dismiss();
                                    Log.d("amountts", "else else");
                                }
                            }
                        }
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
