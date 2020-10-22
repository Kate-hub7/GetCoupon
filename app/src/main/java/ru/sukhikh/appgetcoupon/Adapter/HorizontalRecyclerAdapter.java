package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ExampleViewHolder>
{
    private Context context;
    List<Shop> horizontalShopList;
    private ClickListener listener;

    public interface ClickListener {
        public void SendData(Shop shopModel);
    }

    public HorizontalRecyclerAdapter(Context context, List<Shop> horizontalShopList, ClickListener listener ){
        this.context=context;
        this.horizontalShopList =horizontalShopList;
        this.listener = listener;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_card_horizontal,
                parent, false);
        return new HorizontalRecyclerAdapter.ExampleViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder( ExampleViewHolder holder, int position) {

        final Shop horizontalModel = horizontalShopList.get(position);
        holder.mTextView1.setText(horizontalModel.getName());
        holder.mTextView2.setText(horizontalModel.getShortShopDescription());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(horizontalModel.getPlaceholderColor()));

        if(horizontalModel.getUrlImage()!=null && horizontalModel.getUrlImage().length()>0){
            Picasso.with(context).load(horizontalModel.getUrlImage()).placeholder(gradientDrawable).into(holder.mImageView);
        }
        else{
            holder.mImageView.setImageResource(R.drawable.icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.SendData(horizontalModel);
            }
        });

    }

    @Override
    public int getItemCount() { return horizontalShopList.size(); }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public RoundedImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ExampleViewHolder(View itemView, final ClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);


        }

    }

}
