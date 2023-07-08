package com.gov.gruhalakshmi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gov.gruhalakshmi.aadharData.Male;

import java.util.List;

public class MaleAdapter extends RecyclerView.Adapter<MaleAdapter.ViewHolder> {

    private List<Male> arrList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private boolean isForDistrict;
    private String TAG = "TxnDisplayAdapter";

    public MaleAdapter(Context context, List<Male> arrList) {
        this.mInflater = LayoutInflater.from(context);
        this.arrList = arrList;
        this.context = context;
        this.isForDistrict = isForDistrict;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.husband_cardview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Male data =  arrList.get(position);
        holder.husName.setText(data.getMEMBER_NAME_ENG());
        holder.husAge.setText("");
        holder.husGender.setText(data.getMBR_GENDER());
        holder.husDob.setText(data.getMBR_DOB());
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView husName, husAge,husDob,husGender;
        public LinearLayout lldelivery;
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            husName = itemView.findViewById(R.id.husbandName);
           // husAge = itemView.findViewById(R.id.husage);
            husDob = itemView.findViewById(R.id.husDob);
            husGender = itemView.findViewById(R.id.husgender);
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

