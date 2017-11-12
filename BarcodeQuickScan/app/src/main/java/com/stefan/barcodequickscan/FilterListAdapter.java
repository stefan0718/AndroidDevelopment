package com.stefan.barcodequickscan;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FilterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<String []> filterList;
    private Context context;
    private MainActivity ma;
    private SharedPreferences sp;

    public FilterListAdapter(List<String []> filterList, Context context, SharedPreferences sp) {
        this.filterList = filterList;
        this.context = context;
        this.sp = sp;
    }

    public class FilterHolder extends RecyclerView.ViewHolder {

        private TextView number, prefix, content, suffix, description;
        private ImageView delete;

        public FilterHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.filter_number);
            prefix = (TextView) v.findViewById(R.id.filter_prefix);
            content = (TextView) v.findViewById(R.id.filter_content);
            suffix = (TextView) v.findViewById(R.id.filter_suffix);
            description = (TextView) v.findViewById(R.id.filter_description);
            delete = (ImageView) v.findViewById(R.id.filter_delete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View filterView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_list, parent, false);
        FilterListAdapter.FilterHolder fh = new FilterListAdapter.FilterHolder(filterView);
        return fh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilterHolder) {
            final FilterHolder fh = (FilterHolder) holder;
            if (!filterList.isEmpty() && filterList != null){
                fh.number.setText(position + 1 + ".");
                fh.prefix.setText(filterList.get(position)[0]);
                fh.content.setText(filterList.get(position)[1]);
                fh.suffix.setText(filterList.get(position)[2]);
                fh.description.setText(filterList.get(position)[3]);
                fh.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ma = new MainActivity();
                        new AlertDialog.Builder(context)
                                .setTitle("Delete this Filter")
                                .setMessage("Do you really want to delete the "
                                        + (fh.getAdapterPosition() + 1) + " filter? ")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        filterList.remove(fh.getAdapterPosition());
                                        sp.edit().clear().apply();  //clear all sp data
                                        //add all remain data
                                        if (filterList.size() > 0){
                                            for (int i = 0; i < filterList.size(); i++){
                                                String[] s = filterList.get(i);
                                                sp.edit().putString(String.valueOf(i),
                                                        s[0] + "," + s[1] + "," + s[2]
                                                                + "," + s[3]).apply();
                                            }
                                        }
                                        notifyItemRemoved(fh.getAdapterPosition());
                                        ma.setFilterList(filterList);
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
        return filterList.size();
    }
}
