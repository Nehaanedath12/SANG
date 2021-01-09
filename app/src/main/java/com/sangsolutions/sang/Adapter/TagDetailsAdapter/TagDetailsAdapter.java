package com.sangsolutions.sang.Adapter.TagDetailsAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sangsolutions.sang.R;

import java.util.ArrayList;
import java.util.List;


public class TagDetailsAdapter extends ArrayAdapter<TagDetails> {

    Context context;

    List<TagDetails> items, tempItems, suggestions;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(TagDetails tagDetails, int position);

    }

    public TagDetailsAdapter(Context context, List<TagDetails> items) {
        super(context, 0, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<TagDetails>(items); // this makes the difference.
        suggestions = new ArrayList<TagDetails>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_customer, parent, false);
        }
        TagDetails tagDetails = items.get(position);
        TextView lblName = (TextView) view.findViewById(R.id.customerName);
        LinearLayout LinearTagDetails= view.findViewById(R.id.customerLinear);
        if (tagDetails != null) {
            if (lblName != null)
                lblName.setText(tagDetails.getsName());
        }

        LinearTagDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("iidd", tagDetails.getiId() + "");
                onClickListener.onItemClick(tagDetails, position);
            }
        });

        return view;
    }



    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((TagDetails) resultValue).getsName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (TagDetails people : tempItems) {
                    if (people.getsName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<TagDetails> filterList = (ArrayList<TagDetails>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (TagDetails people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}





















//public class TagDetailsAdapter extends RecyclerView.Adapter<TagDetailsAdapter.ViewHolder> {
//    List<TagDetails>list;
//    Context context;
//    private OnClickListener onClickListener;
//
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//    public interface OnClickListener {
//        void onItemClick( TagDetails tagDetails, int position);
//
//    }
//    public TagDetailsAdapter(Context context, List<TagDetails> list) {
//        this.list=list;
//        this.context=context;
//    }
//
//    @NonNull
//    @Override
//    public TagDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TagDetailsAdapter.ViewHolder holder, int position) {
//
//        holder.tagName.setText(list.get(position).sName);
//        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickListener.onItemClick(list.get(position),position);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout linearLayout;
//        TextView tagName;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            linearLayout=itemView.findViewById(R.id.customerLinear);
//            tagName=itemView.findViewById(R.id.customerName);
//        }
//    }
//}
