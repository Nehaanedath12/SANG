package com.sangsolutions.sang.Adapter.DocNoAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPart;
import com.sangsolutions.sang.Adapter.BodyAdapter.BodyPartAdapter;
import com.sangsolutions.sang.R;

import java.util.List;

public class DocNoAdapter extends RecyclerView.Adapter<DocNoAdapter.ViewHolder> {
    Context context;
    List<DocNoClass>list;
    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(DocNoClass docNoClass, int position);

    }
    public DocNoAdapter(Context context, List<DocNoClass> list) {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public DocNoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.doc_no_adapter,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DocNoAdapter.ViewHolder holder, int position) {
        holder.docNo.setText(list.get(position).docNo);
        holder.parent.setOnClickListener(new View.OnClickListener() {
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
        TextView docNo;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docNo=itemView.findViewById(R.id.docNo);
            parent=itemView.findViewById(R.id.parent);
        }
    }
}
