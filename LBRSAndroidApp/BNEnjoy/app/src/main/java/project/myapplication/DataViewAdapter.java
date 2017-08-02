package project.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//customize a view adapter to bind data and show it on UI
public class DataViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private List<FoursquarePlace> fps;
    private boolean showFootView = true;
    private DataViewOnClickListener dataViewListener;
    private Context context;

    //construct an adapter
    public DataViewAdapter(List<FoursquarePlace> list, Context context) {
        this.fps = list;
        this.context = context;
    }

    //create a data view holder for adapter so as to avoid repeating creating objects
    public class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView dataCategory, dataDistance, dataOpenStatus;
        private StrokedTextView dataName, dataRating;
        private ImageView dataImage, img_rating, img_priceLevel, likeThisItem;

        public DataViewHolder(View v) {
            super(v);
            dataName = (StrokedTextView) v.findViewById(R.id.data_name);
            dataRating = (StrokedTextView) v.findViewById(R.id.data_rating);
            dataCategory = (TextView) v.findViewById(R.id.data_category);
            dataDistance = (TextView) v.findViewById(R.id.data_distance);
            dataOpenStatus = (TextView) v.findViewById(R.id.data_open_status);
            dataImage = (ImageView) v.findViewById(R.id.data_image);
            img_rating = (ImageView) v.findViewById(R.id.data_star);
            img_priceLevel = (ImageView) v.findViewById(R.id.data_pricelevel);
            likeThisItem = (ImageView) v.findViewById(R.id.like_this_item);
        }
    }

    //create a foot view holder for adapter for foot notice
    public class FootViewHolder extends RecyclerView.ViewHolder {
        public FootViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {  //item view
            View dataView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_list_content, parent, false);  //get data list view
            DataViewHolder dvh = new DataViewHolder(dataView);
            dataView.setOnClickListener(this);  //add OnClickListener on every item
            return dvh;
        } else if (viewType == 1) {  //foot view
            View footView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foot_view_content, parent, false);  //get foot view
            return new FootViewHolder(footView);
        }
        return null;
    }

    //create an interface so as to set customized onClickListener on Activities
    public interface DataViewOnClickListener {
        void onItemClick(View view, FoursquarePlace fp);
    }

    @Override
    public void onClick(View v) {
        if(dataViewListener != null){
            dataViewListener.onItemClick(v, (FoursquarePlace)v.getTag());  //get and pass fp data
        }
    }

    //set OnClickListener on DataView
    public void setDataViewOnClickListener(DataViewOnClickListener listener){
        this.dataViewListener = listener;
    }

    @Override  //put data to views
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataViewHolder) {
            //get the foursquare place of each view position
            final FoursquarePlace fp = fps.get(position);
            final DataViewHolder dvh = (DataViewHolder) holder;
            dvh.dataName.setText(fp.getName());  //venue name
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : fp.getCategories().entrySet()){
                sb.append(entry.getValue());
                sb.append(" / ");
            }
            sb.setLength(sb.length() - 3);  //remove last string " / "
            dvh.dataCategory.setText(sb.toString());  //venue categories
            if(fp.getDistance() >= 1000){  //show as "#.#km" format
                BigDecimal bd = new BigDecimal(fp.getDistance() / 1000);
                dvh.dataDistance.setText(bd.setScale(1, BigDecimal.ROUND_HALF_UP)
                        + "km   " + fp.getCity());  //venue distance according to the user's location
            }
            else{  //show as "###m" format
                BigDecimal bd = new BigDecimal(fp.getDistance());
                dvh.dataDistance.setText(bd.setScale(0, BigDecimal.ROUND_HALF_UP)
                        + "m   " + fp.getCity());  //venue distance according to the user's location
            }
            //set photo url to imageView
            Picasso.with(this.context).load(fp.getPhotoUrls().get(0)).into(dvh.dataImage);
            dvh.dataRating.setText(Float.toString(fp.getRating()));  //set rating score on item view
            setRatingStar(fp, dvh.img_rating);  //set rating star
            setPriceLevel(fp, dvh.img_priceLevel);  //set price level
            Typeface brook = Typeface.createFromAsset(this.context.getAssets(),
                    "fonts/brook23.ttf");
            dvh.dataOpenStatus.setTypeface(brook);  //set new font "brook23" to dataOpenStatus
            if (fp.getOpenStatus() == 1){
                dvh.dataOpenStatus.setText("Now Open");  //set open state
            }
            else if (fp.getOpenStatus() == 2){
                dvh.dataOpenStatus.setText("Closed");  //set open state
            }
            else if (fp.getOpenStatus() == 0){
                dvh.dataOpenStatus.setVisibility(View.INVISIBLE);  //hide this textView
            }
            dvh.itemView.setTag(fp);  //store fp data & pass data through onClickListener
            final Drawable unClicked = ContextCompat.getDrawable(
                    context, R.mipmap.like);  //un-clicked drawable
            final Drawable clicked = ContextCompat.getDrawable(
                    context, R.mipmap.liked);  //clicked drawable
            final SharedPreferences sp = context.getSharedPreferences("FAVORITES",
                    Context.MODE_PRIVATE);
            if (sp.contains(fp.getId())){  //this fp has been liked
                dvh.likeThisItem.setImageDrawable(clicked);
            }
            else{  //this fp has not been liked yet
                dvh.likeThisItem.setImageDrawable(unClicked);
            }
            dvh.likeThisItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable current = dvh.likeThisItem.getDrawable();
                    //condition that the heart button is clicked
                    if (current.getConstantState().equals(clicked.getConstantState())){
                        dvh.likeThisItem.setImageDrawable(unClicked);
                        sp.edit().remove(fp.getId()).apply();  //remove this sp
                    }
                    else{  //condition that the heart button is un-clicked
                        dvh.likeThisItem.setImageDrawable(clicked);
                        Gson gson = new Gson();
                        String json = gson.toJson(fp);  //parse FoursquarePlace fp to json
                        //save fp as json format
                        sp.edit().putString(fp.getId(), json).apply();
                    }
                }
            });
        }
    }

    //set rating star according to rating score of the foursquare place
    public void setRatingStar(FoursquarePlace fp, ImageView iv){
        if (fp.getRating() == 0){
            iv.setImageResource(R.mipmap.rating_0);
        }
        else if (fp.getRating() > 0 && fp.getRating() <= 1){
            iv.setImageResource(R.mipmap.rating_0_1);
        }
        else if (fp.getRating() > 1 && fp.getRating() <= 2){
            iv.setImageResource(R.mipmap.rating_1_2);
        }
        else if (fp.getRating() > 2 && fp.getRating() <= 3){
            iv.setImageResource(R.mipmap.rating_2_3);
        }
        else if (fp.getRating() > 3 && fp.getRating() <= 4){
            iv.setImageResource(R.mipmap.rating_3_4);
        }
        else if (fp.getRating() > 4 && fp.getRating() <= 5){
            iv.setImageResource(R.mipmap.rating_4_5);
        }
        else if (fp.getRating() > 5 && fp.getRating() <= 6){
            iv.setImageResource(R.mipmap.rating_5_6);
        }
        else if (fp.getRating() > 6 && fp.getRating() <= 7){
            iv.setImageResource(R.mipmap.rating_6_7);
        }
        else if (fp.getRating() > 7 && fp.getRating() <= 8){
            iv.setImageResource(R.mipmap.rating_7_8);
        }
        else if (fp.getRating() > 8 && fp.getRating() <= 9){
            iv.setImageResource(R.mipmap.rating_8_9);
        }
        else if (fp.getRating() > 9 && fp.getRating() <= 10){
            iv.setImageResource(R.mipmap.rating_9_10);
        }
    }

    //set price level icons according to price level of the foursquare place
    public void setPriceLevel(FoursquarePlace fp, ImageView iv){
        if (fp.getPriceLevel() == 1){
            iv.setImageResource(R.mipmap.price_level_1);
        }
        else if (fp.getPriceLevel() == 2){
            iv.setImageResource(R.mipmap.price_level_2);
        }
        else if (fp.getPriceLevel() == 3){
            iv.setImageResource(R.mipmap.price_level_3);
        }
        else if (fp.getPriceLevel() == 4){
            iv.setImageResource(R.mipmap.price_level_4);
        }
        else{  //no price level
            iv.setImageResource(R.mipmap.price_level_0);
        }
    }

    @Override  //get the number of data items
    public int getItemCount() {
        if(showFootView){
            return fps.size() + 1;  //plus foot view if need to show foot view
        }
        else{
            return fps.size();  //without foot view if don't want to show foot view
        }
    }

    public int getItemViewType(int position){
        if (showFootView){  //if need to show foot view
            if (isPositionFootView(position)){  //double check if need to show foot view
                return 1;  //1 means foot view type
            }  //don't want to show foot view. commonly in "all items have been shown" condition
            return 0;  //0 means item view type
        }
        else{
            return 0;
        }
    }

    //determine if the current position is in the last item
    public boolean isPositionFootView(int position){
        return position == getItemCount() - 1;
    }

    //hide the foot view
    public void hideFootView(){
        showFootView = false;
    }

    //re-show the foot view
    public void showFootView(){
        showFootView = true;
    }
}
