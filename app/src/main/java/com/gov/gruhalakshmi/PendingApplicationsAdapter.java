package com.gov.gruhalakshmi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;






public class PendingApplicationsAdapter extends RecyclerView.Adapter<PendingApplicationsAdapter.ViewHolder> {

    List<ApplicationData> arrList = new ArrayList<ApplicationData>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private boolean isForDistrict;
    private String TAG = "TxnDisplayAdapter";

    public PendingApplicationsAdapter(Context context, List<ApplicationData> arrList) {
        this.mInflater = LayoutInflater.from(context);
        this.arrList = arrList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_pending_applications, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApplicationData obj = arrList.get(position);
        holder.txtRc.setText(obj.getRcNo());
        //holder.txtaadhar.setText(obj.getHusband_aadhaar());
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtRc, txtaadhar;
        public Button subBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            txtRc = itemView.findViewById(R.id.rcNoTxt);
            txtaadhar = itemView.findViewById(R.id.husadhaarhashTxt);
            subBtn = itemView.findViewById(R.id.submitApplicationBtn);
            subBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemModelClick(view, getAdapterPosition());
        }

    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemModelClick(View view, int position);
    }
}