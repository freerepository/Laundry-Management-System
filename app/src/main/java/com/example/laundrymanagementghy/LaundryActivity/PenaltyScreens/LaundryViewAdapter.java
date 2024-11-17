package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundrymanagementghy.R;

import java.util.ArrayList;

public class LaundryViewAdapter extends RecyclerView.Adapter<LaundryViewAdapter.LaundryViewViewHolder> {

    private ArrayList<PenaltyItemsViewDataModel.PenaltyItem> list;

    public LaundryViewAdapter(ArrayList<PenaltyItemsViewDataModel.PenaltyItem> mList) {
        this.list = mList;
    }

    @NonNull
    @Override
    public LaundryViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LaundryViewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.penalty_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryViewViewHolder holder, int position) {
        PenaltyItemsViewDataModel.PenaltyItem item = list.get(position);

        holder.tv_index_number.setText(String.valueOf(position + 1));
        holder.tv_item.setText(item.itemName);
        holder.tv_qty.setText(item.quantity);
        holder.tv_refer_no.setText(item.amount);
        holder.tv_rate.setText(item.rate);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LaundryViewViewHolder extends RecyclerView.ViewHolder {

        TextView tv_index_number, tv_item, tv_qty, tv_refer_no, tv_rate;

        public LaundryViewViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index_number = itemView.findViewById(R.id.tv_index_number);
            tv_item = itemView.findViewById(R.id.tv_item);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_refer_no = itemView.findViewById(R.id.tv_refer_no);
            tv_rate = itemView.findViewById(R.id.tv_rate);
        }
    }
}
