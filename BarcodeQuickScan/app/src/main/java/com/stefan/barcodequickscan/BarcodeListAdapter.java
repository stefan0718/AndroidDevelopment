package com.stefan.barcodequickscan;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class BarcodeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private List<String[]> list;
    private Context context;
    private MainActivity ma;
    private BarcodeOnClickListener bOnClickListener;

    public BarcodeListAdapter(List<String[]> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public class BarcodeHolder extends RecyclerView.ViewHolder {

        private TextView number, barcode, weight;
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
        barcodeView.setOnClickListener(this);
        return bh;
    }

    public interface BarcodeOnClickListener{
        void onBarcodeClick(View view, String[] s);
    }

    @Override
    public void onClick(View v) {
        if (bOnClickListener != null){
            bOnClickListener.onBarcodeClick(v, (String [])v.getTag());
        }
    }

    public void setBarcodeOnClickListener(BarcodeOnClickListener listener){
        this.bOnClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BarcodeHolder) {
            final BarcodeHolder bh = (BarcodeHolder) holder;
            bh.itemView.setTag(list.get(position));
            if (!list.isEmpty() && list != null){
                bh.number.setText(position + 1 + ".");
                bh.barcode.setText(list.get(position)[0]);
                bh.weight.setText(list.get(position)[1]);
                bh.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ma = new MainActivity();
                        new AlertDialog.Builder(context)
                                .setTitle("Delete this Barcode")
                                .setMessage("Do you really want to delete the barcode "
                                        + list.get(bh.getAdapterPosition())[0] + "?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        list.remove(bh.getAdapterPosition());
                                        notifyItemRemoved(bh.getAdapterPosition());
                                        ma.setBarcodeList(list);
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
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
