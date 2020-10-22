package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.JsonModel.Ad;
import ru.sukhikh.appgetcoupon.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ExampleViewHolder>
{
    private Context context;
    private boolean isFullAd;
    private List<Ad> adList;

    public AdsAdapter(Context context, List<Ad> adList, boolean isFullAd){
        this.context=context;
        this.adList =adList;
        this.isFullAd = isFullAd;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v;
        if(isFullAd){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_ad,
                    parent, false);
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_full_ad,
                    parent, false);
        }
        return new AdsAdapter.ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder( ExampleViewHolder holder, int position) {

        final Ad horizontalModel = adList.get(position);

        if(horizontalModel.getUrlImage()!=null && horizontalModel.getUrlImage().length()>0){
            Picasso.with(context).load(horizontalModel.getUrlImage()).into(holder.mImageView);
        }
        else{
            holder.mImageView.setImageResource(R.drawable.icon);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(horizontalModel.getUrlWebsite()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() { return adList.size(); }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.adImage);

        }

    }

}
