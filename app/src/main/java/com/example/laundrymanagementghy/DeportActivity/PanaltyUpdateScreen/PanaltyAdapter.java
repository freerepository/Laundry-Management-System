package com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundrymanagementghy.DeportActivity.BedrollReturntViewStockActivity;
import com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens.LaundryViewActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.model.GetStockModel;

import java.util.ArrayList;

public class PanaltyAdapter extends RecyclerView.Adapter<PanaltyAdapter.PanaltyViewHolder> {

    Context context;
    private ArrayList<PenaltyViewModelClass.PenaltyItem> mList;
    private ArrayList<PenaltyViewModelClass.PenaltyItem> mListFull;

    public PanaltyAdapter(ArrayList<PenaltyViewModelClass.PenaltyItem> mList, Context context) {
        this.mList = new ArrayList<>(mList);
        this.mListFull = new ArrayList<>(mList);
        this.context = context;
    }

    @NonNull
    @Override
    public PanaltyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.panalty_item_, parent, false);
        PanaltyViewHolder viewHolder = new PanaltyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PanaltyViewHolder holder, int position) {
        PenaltyViewModelClass.PenaltyItem item = mList.get(position);

        holder.tv_index.setText(String.valueOf(position + 1));
        holder.tv_date.setText(item.penaltyDate);
        holder.tv_remark.setText(item.remark);
        holder.tv_amount.setText(item.totalPenalty);
//        holder.tv_view.setText(item.);

        holder.tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DepotPenaltyViewScreen.class);
                intent.putExtra("id", item.id);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });





    }

    public void filterList(ArrayList<PenaltyViewModelClass.PenaltyItem> filteredList) {
        mList.clear();
        mList.addAll(filteredList);
        notifyDataSetChanged();
    }
    public void resetFilter() {
        mList.clear();
        mList.addAll(mListFull);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList.size() ;
    }

    public class PanaltyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_index,tv_date,tv_remark,tv_amount,tv_view;
        LinearLayout linearLayout;
        public PanaltyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_index = itemView.findViewById(R.id.tv_index_number);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_view = itemView.findViewById(R.id.iv_varify);

            linearLayout = itemView.findViewById(R.id.linearLayout3);

        }
    }


}
