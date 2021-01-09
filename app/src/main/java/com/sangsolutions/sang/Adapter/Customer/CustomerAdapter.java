package com.sangsolutions.sang.Adapter.Customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.sang.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends ArrayAdapter<Customer> {

    Context context;

    List<Customer> items, tempItems, suggestions;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(Customer customer, int position);

    }

    public CustomerAdapter(Context context, List<Customer> items) {
        super(context, 0, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<Customer>(items); // this makes the difference.
        suggestions = new ArrayList<Customer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_customer, parent, false);
        }
        Customer customer = items.get(position);
        TextView lblName = (TextView) view.findViewById(R.id.customerName);
        LinearLayout LinearCustomer= view.findViewById(R.id.customerLinear);
        if (customer != null) {
            if (lblName != null)
                lblName.setText(customer.getsName());
        }

        LinearCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("iidd", customer.getiId() + "");
                    onClickListener.onItemClick(customer, position);
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
            String str = ((Customer) resultValue).getsName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Customer people : tempItems) {
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
            List<Customer> filterList = (ArrayList<Customer>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Customer people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}


































//public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
//
//    Context context;
//    List<Customer>list;
//    private OnClickListener onClickListener;
//
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    public CustomerAdapter(Context context, List<Customer> list) {
//        this.context=context;
//        this.list=list;
//    }
//
//
//    @NonNull
//    @Override
//    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
//        holder.customerName.setText(list.get(position).getsName());
//        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickListener.onItemClick(list.get(position), position);
//            }
//        });
//    }
//    public interface OnClickListener {
//        void onItemClick(Customer customer, int position);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout linearLayout;
//        TextView customerName;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            linearLayout=itemView.findViewById(R.id.customerLinear);
//            customerName=itemView.findViewById(R.id.customerName);
//        }
//    }
//}
