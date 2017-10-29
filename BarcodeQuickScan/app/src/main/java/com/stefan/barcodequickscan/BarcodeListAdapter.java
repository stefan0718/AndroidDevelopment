package com.stefan.barcodequickscan;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class BarcodeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String[]> list;
    private Context context;
    private MainActivity ma;

    public BarcodeListAdapter(List<String[]> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public class BarcodeHolder extends RecyclerView.ViewHolder {

        private TextView number;
        private TextView barcode;
        private TextView weight;
        private ImageView delete;

        public BarcodeHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.barcode_number);
            barcode = (TextView) v.findViewById(R.id.barcode_detail);
            weight = (TextView) v.findViewById(R.id.barcode_weight);
            delete = (ImageView) v.findViewById(R.id.delete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View barcodeView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.barcode_list, parent, false);
        BarcodeHolder bh = new BarcodeHolder(barcodeView);
        return bh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BarcodeHolder) {
            final BarcodeHolder bh = (BarcodeHolder) holder;
            if (!list.isEmpty() && list != null){
                bh.number.setText(position + 1 + ".");
                bh.barcode.setText(list.get(position)[0].toString());
                bh.weight.setText(list.get(position)[1].toString());
                bh.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ma = new MainActivity();
                        list.remove(bh.getAdapterPosition());
                        notifyItemRemoved(bh.getAdapterPosition());
                        ma.setBarcodeList(list);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
