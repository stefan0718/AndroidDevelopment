package project.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

//customize a recommended view adapter to bind data and show it on main activity
public class RecommendedViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private List<FoursquarePlace> fps;
    private Context context;
    private RecommendedViewOnClickListener recommendedViewListener;

    //construct an adapter
    public RecommendedViewAdapter(List<FoursquarePlace> list, Context context) {
        this.fps = list;
        this.context = context;
    }

    //create a holder for adapter
    public class RecommendedViewHolder extends RecyclerView.ViewHolder {

        private StrokedTextView rName;
        private StrokedTextView rRating;
        private ImageView rImage;

        public RecommendedViewHolder(View v) {
            super(v);
            rName = (StrokedTextView) v.findViewById(R.id.recommended_name);
            rRating = (StrokedTextView) v.findViewById(R.id.recommended_rating);
            rImage = (ImageView) v.findViewById(R.id.recommended_image);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recommendedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_view, parent, false);  //get data list view
        RecommendedViewHolder rvh = new RecommendedViewHolder(recommendedView);
        recommendedView.setOnClickListener(this);  //add OnClickListener on every item
        return rvh;
    }

    //create an interface of recommended view onClickListener
    public interface RecommendedViewOnClickListener {
        void onCardClick(View view, FoursquarePlace fp);
    }

    @Override
    public void onClick(View v) {
        if(recommendedViewListener != null){
            //get and pass fp data
            recommendedViewListener.onCardClick(v, (FoursquarePlace)v.getTag());  //get fp data
        }
    }

    //set OnClickListener on RecommendedView
    public void setRecommendedViewOnClickListener(RecommendedViewOnClickListener listener){
        this.recommendedViewListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecommendedViewHolder) {
            FoursquarePlace fp = fps.get(position);
            RecommendedViewHolder rvh = (RecommendedViewHolder) holder;
            rvh.rName.setText(fp.getName());
            rvh.rRating.setText(Float.toString(fp.getRating()));
            Picasso.with(this.context).load(fp.getPhotoUrls().get(0)).into(rvh.rImage);
            rvh.itemView.setTag(fp);  //store fp data & pass data through onClickListener
        }
    }

    @Override
    public int getItemCount() {
        return fps.size();
    }
}
