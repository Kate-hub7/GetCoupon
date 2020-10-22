package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HotRecyclerAdapter extends RecyclerView.Adapter<HotRecyclerAdapter.ExampleViewHolder> {
    private Context context;
    private List<Shop> hotShopList;
    private ClickListener listener;

    public interface ClickListener {
        public void SendData(Shop shopModel);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ExampleViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public HotRecyclerAdapter(Context context, ArrayList<Shop> hotShopList, ClickListener listener) {
       this.context=context;
       this.listener=listener;
        this.hotShopList = hotShopList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_card_hot,
                parent, false);
        return new HotRecyclerAdapter.ExampleViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        final Shop currentItem = hotShopList.get(position);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(currentItem.getPlaceholderColor()));

        if(currentItem.getUrlImage()!=null && currentItem.getUrlImage().length()>0){
            Picasso.with(context).load(currentItem.getUrlImage()).placeholder(gradientDrawable).into(holder.mImageView);
        }
        else{
            holder.mImageView.setImageResource(R.drawable.icon);
        }

        holder.mTextView1.setText(currentItem.getName());
        holder.mTextView2.setText(currentItem.getShortShopDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.SendData(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotShopList.size();
    }

}
