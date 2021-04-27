package com.sangsolutions.sang.Adapter.QuotationAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.TagDetailsAdapter.TagDetails;
import com.sangsolutions.sang.Adapter.TransSalePurchase.TransSetting;
import com.sangsolutions.sang.Database.DatabaseHelper;
import com.sangsolutions.sang.R;

import java.util.List;

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.ViewHolder> {

    List<QuotationClass>list;
    Context context;
    DatabaseHelper helper;
    int tagTotalNumber;
    int iDocType;

    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(QuotationClass quotationClass, int position);

    }

    public QuotationAdapter(Context context, List<QuotationClass> list, int tagTotalNumber, int iDocType) {

        this.context=context;
        this.list=list;
        this.tagTotalNumber=tagTotalNumber;
        this.iDocType=iDocType;

    }

    @NonNull
    @Override
    public QuotationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.quotaion_adapter,parent,false);
        helper=new DatabaseHelper(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotationAdapter.ViewHolder holder, int position) {
        holder.productName.setText(list.get(position).productName);
        holder.unit.setText(list.get(position).unit);
        holder.qty.setText(String.valueOf(list.get(position).qty));
        holder.rate.setText(String.valueOf(list.get(position).rate));
        holder.remarks.setText(String.valueOf(list.get(position).remarks));

        Log.d("mapsizee",list.get(position).hashMapBody.size()+"");

        for (int tagId=1;tagId<=tagTotalNumber;tagId++){
            Cursor cursor=helper.getTransSettings(iDocType,tagId);
            if(cursor!=null ) {
                cursor.moveToFirst();
                String iTagPosition = cursor.getString(cursor.getColumnIndex(TransSetting.I_TAG_POSITION));
                String mandatory = cursor.getString(cursor.getColumnIndex(TransSetting.B_MANDATORY));
                String visibility = cursor.getString(cursor.getColumnIndex(TransSetting.B_VISIBLE));
                Log.d(" iTagPositionN ", iTagPosition + " pos " + mandatory + " visible " + visibility + " iTagId " + tagId);

                Cursor cursor1 = helper.getTagNamebyId(tagId);
                cursor1.moveToFirst();
                if (iTagPosition.equals("2")) {

                    LinearLayout l_tags = holder.linearTag;
                    // add autocompleteTextView
                    TextView textView = new TextView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    l_tags.addView(textView);
                    textView.setGravity(Gravity.CENTER);
                    textView.setWidth(150);
                    textView.setText("");

                    for (int i=0;i<list.get(position).hashMapBody.size();i++) {
                        int tagId_map = (int) list.get(position).hashMapBody.keySet().toArray()[i];
                        int tagDetails_map = (int) list.get(position).hashMapBody.values().toArray()[i];

                        try {
                            if (tagId_map == tagId) {
                                Cursor cursor_map = helper.getTagName(tagId, tagDetails_map);
                                textView.setText(cursor_map.getString(cursor_map.getColumnIndex(TagDetails.S_NAME)));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        if (position % 2 == 0) {
            holder.parentCard.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.parentCard.setBackgroundColor(Color.rgb(255, 255, 255));
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

        TextView productName,unit,qty,rate,remarks;
        ImageView delete;
        CardView parentCard;
        LinearLayout linearTag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName=itemView.findViewById(R.id.product_name);
            unit=itemView.findViewById(R.id.unit);
            qty=itemView.findViewById(R.id.qty);
            rate=itemView.findViewById(R.id.rate);
            remarks=itemView.findViewById(R.id.remarks);
            linearTag=itemView.findViewById(R.id.linear_tags);
            delete=itemView.findViewById(R.id.delete);
            parentCard=itemView.findViewById(R.id.cardView_body_adapter);
        }
    }
}
