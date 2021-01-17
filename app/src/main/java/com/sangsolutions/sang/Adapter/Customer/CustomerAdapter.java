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
        tempItems = new ArrayList<Customer>(items);
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
        TextView lblName = view.findViewById(R.id.customerName);
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
                for (Customer customer : tempItems) {
                    if (customer.getsName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);

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