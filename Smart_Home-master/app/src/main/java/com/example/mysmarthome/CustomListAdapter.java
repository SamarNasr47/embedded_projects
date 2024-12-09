package com.example.mysmarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;
    private List<String> filteredItems;
    private int[] imageResources;

    public CustomListAdapter(Context context, List<String> items, int[] imageResources) {
        super(context, R.layout.list_item_layout, items);
        this.context = context;
        this.items = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
        this.imageResources = imageResources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_layout, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        String filteredItem = filteredItems.get(position);
        textView.setText(filteredItem);

        int originalIndex = items.indexOf(filteredItem);

        if (originalIndex != -1 && originalIndex < imageResources.length) {
            imageView.setImageResource(imageResources[originalIndex]);
        }

        return view;
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(items);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String item : items) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                if (results.values != null) {
                    filteredItems.addAll((List<String>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
}