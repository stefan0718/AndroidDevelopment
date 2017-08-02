package project.myapplication;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageSlideShowAdapter extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private List<String> imageUrls = new ArrayList<>();
    private ImageView imageView;
    private SlideShowOnClickListener slideShowOnClickListener;

    //create an ImageSlideShowAdapter constructor
    public ImageSlideShowAdapter(Context c, List<String> imageUrls){
        this.context = c;
        this.imageUrls = imageUrls;
    }

    @Override
    //remove redundant buffered images
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    //get the number of image urls
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    //will be invoked to load the third image after sliding to the second image
    public Object instantiateItem(ViewGroup view, int position) {
        View v = LayoutInflater.from(view.getContext())
                .inflate(R.layout.image_slideshow, view, false);  //get image view layout
        imageView = (ImageView) v.findViewById(R.id.image);
        //set photo url to imageView
        Picasso.with(this.context).load(imageUrls.get(position)).into(imageView);
        view.addView(v, 0);  //add view into position 0
        v.setOnClickListener(this);  //add OnClickListener on every image
        return v;
    }

    @Override
    //return true if the view is from the object
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    //create an interface so as to set customized onClickListener on Activities
    public interface SlideShowOnClickListener {
        void onSlideShowClick(View v);
    }

    @Override
    public void onClick(View v) {
        if(slideShowOnClickListener != null){
            slideShowOnClickListener.onSlideShowClick(v);
        }
    }

    //set OnClickListener on slide show
    public void setSlideShowOnClickListener(SlideShowOnClickListener listener){
        this.slideShowOnClickListener = listener;
    }
}
