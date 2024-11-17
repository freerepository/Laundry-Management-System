package com.example.laundrymanagementghy;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;
    private int selectedItemPosition = -1; // Track selected position

    public CustomSpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.spinner_selected_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.spinner_selected_item_text);
        textView.setTextColor(Color.BLUE); // Set your desired dropdown item color
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.spinner_selected_item_text);

        // Change color based on selected position
        if (position == selectedItemPosition) {
            textView.setTextColor(Color.BLUE); // Color for selected item
        } else {
            textView.setTextColor(Color.BLACK); // Default color for other items
        }

        return view;
    }

    // Method to update the selected position
    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
        notifyDataSetChanged(); // Refresh the spinner view
    }
}
