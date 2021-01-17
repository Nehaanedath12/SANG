package com.sangsolutions.sang.Adapter.Products;

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
public class ProductsAdapter extends ArrayAdapter<Products> {

    Context context;

    List<Products> items, tempItems, suggestions;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(Products products, int position);

    }

    public ProductsAdapter(Context context, List<Products> items) {
        super(context, 0, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<Products>(items);
        suggestions = new ArrayList<Products>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_customer, parent, false);
        }
        Products products = items.get(position);
        TextView lblName = (TextView) view.findViewById(R.id.customerName);
        LinearLayout LinearProduct =view.findViewById(R.id.customerLinear);
        if (products != null) {
            if (lblName != null)
                lblName.setText(products.getsName());
        }

        LinearProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("iidd", products.getiId() + "");
                onClickListener.onItemClick(products, position);
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
            String str = ((Products) resultValue).getsName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Products products : tempItems) {
                    if (products.getsName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(products);
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
            List<Products> filterList = (ArrayList<Products>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Products people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
























//public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
//    List<Products>list;
//    Context context;
//    private OnClickListener onClickListener;
//
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//    public interface OnClickListener {
//        void onItemClick(Products products, int position);
//
//    }
//
//    public ProductsAdapter(Context context, List<Products> list) {
//
//        this.context=context;
//        this.list=list;
//    }
//
//    @NonNull
//    @Override
//    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
//
//        holder.productName.setText(list.get(position).getsName());
//        holder.productCode.setText(list.get(position).getsCode());
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
//        TextView productName,productCode;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            linearLayout=itemView.findViewById(R.id.customerLinear);
//            productName=itemView.findViewById(R.id.customerName);
//            productCode=itemView.findViewById(R.id.code);
//            productCode.setVisibility(View.VISIBLE);
//        }
//    }
//}
