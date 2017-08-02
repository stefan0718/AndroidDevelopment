package project.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

//customize a image gallery adapter
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    private List<String> imageUrls;
    private Context context;
    private GalleryAdapter.GalleryOnClickListener galleryOnClickListener;

    //construct an adapter
    public GalleryAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    //create an image holder
    public class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageThumbnail;

        public ImageHolder(View v) {
            super(v);
            imageThumbnail = (ImageView) v.findViewById(R.id.image_thumbnail);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery, parent, false);  //get image view from layout gallery
        imageView.setOnClickListener(this);  //add OnClickListener on every item
        return new ImageHolder(imageView);
    }

    @Override  //bind image to views
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GalleryAdapter.ImageHolder) {
            String imageUrl = imageUrls.get(position);  //get the image url with position
            ImageHolder ih = (ImageHolder) holder;
            //set photo url to imageView
            Picasso.with(this.context).load(imageUrl).into(ih.imageThumbnail);
            ih.itemView.setTag(position);  //store position through onClickListener
        }
    }

    @Override  //get the number of images
    public int getItemCount() {
        return imageUrls.size();
    }

    //create an interface so as to set customized onClickListener on Activities
    public interface GalleryOnClickListener {
        void onGalleryItemClick(View v, int position);
    }

    @Override
    public void onClick(View v) {
        if(galleryOnClickListener != null){
            galleryOnClickListener.onGalleryItemClick(v, (int) v.getTag());
        }
    }

    //set OnClickListener on gallery items
    public void setGalleryOnClickListener(GalleryAdapter.GalleryOnClickListener listener){
        this.galleryOnClickListener = listener;
    }
}
