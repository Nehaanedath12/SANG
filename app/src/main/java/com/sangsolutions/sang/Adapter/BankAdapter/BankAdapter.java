package com.sangsolutions.sang.Adapter.BankAdapter;

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

public class BankAdapter extends ArrayAdapter<Bank> {

    Context context;

    List<Bank> items, tempItems, suggestions;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(Bank bank, int position);

    }

    public BankAdapter(Context context, List<Bank> items) {
        super(context, 0, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<Bank>(items);
        suggestions = new ArrayList<Bank>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_customer, parent, false);
        }
        Bank bank = items.get(position);
        TextView lblName = view.findViewById(R.id.customerName);
        LinearLayout LinearBank= view.findViewById(R.id.customerLinear);
        if (bank != null) {
            if (lblName != null)
                lblName.setText(bank.getsName());
        }

        LinearBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("iidd", bank.getiId() + "");
                onClickListener.onItemClick(bank, position);
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
            String str = ((Bank) resultValue).getsName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Bank bank : tempItems) {
                    if (bank.getsName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(bank);

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
            List<Bank> filterList = (ArrayList<Bank>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Bank bank : filterList) {
                    add(bank);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
