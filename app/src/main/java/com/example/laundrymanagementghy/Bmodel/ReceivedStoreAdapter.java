package com.example.laundrymanagementghy.Bmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundrymanagementghy.R;

import java.util.ArrayList;

public class ReceivedStoreAdapter extends RecyclerView.Adapter<ReceivedStoreAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<GetReceiveddailogStoreList.ReceivedDataStore> ListData;

    public ReceivedStoreAdapter(ArrayList<GetReceiveddailogStoreList.ReceivedDataStore> ListData) {
        this.ListData = ListData;
    }

    @NonNull
    @Override
    public ReceivedStoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_receiced_store_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedStoreAdapter.ViewHolder holder, int position) {

        holder.tv_dse.setText(ListData.get(position).mItem_description);
        holder.tv_pl.setText(ListData.get(position).mPl_no);
        holder.tv_qty.setText(ListData.get(position).mQty);

    }

    @Override
    public int getItemCount() {
        return  ListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dse,tv_pl,tv_qty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_dse = (TextView) itemView.findViewById(R.id.tv_dec);
            tv_pl = (TextView) itemView.findViewById(R.id.tv_pl_no);
            tv_qty = (TextView) itemView.findViewById(R.id.tv_qty);

        }
    }
}
