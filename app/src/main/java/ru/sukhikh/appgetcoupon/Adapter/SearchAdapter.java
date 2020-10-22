package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ExampleViewHolder> {
    private Context context;
    private List<Shop> searchList;
    private final ClickListener listener;
    private View v;

    public interface ClickListener {
        public void SendData(Shop data);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        private View mUnderLine;

        public ExampleViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mUnderLine = itemView.findViewById(R.id.underline);
        }
    }

    public SearchAdapter(Context context, List<Shop> searchList, ClickListener listener) {

        this.context=context;
        this.listener=listener;
        this.searchList = searchList;
    }

    @Override
    public SearchAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_search,
                parent, false);
        return new SearchAdapter.ExampleViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, final int position) {

        if(position==0) {
            CardView card = (CardView) v;
            card.setRadius(25);
        }
        final Shop shopModel = searchList.get(position);
        final String data = shopModel.getName();
        holder.mTextView1.setText(shopModel.getName());
        holder.mTextView2.setText(shopModel.getShortShopDescription());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(shopModel.getPlaceholderColor()));

        if(shopModel.getUrlImage()!=null && shopModel.getUrlImage().length()>0){
            Picasso.with(context).load(shopModel.getUrlImage()).placeholder(gradientDrawable).error(R.drawable.icon).into(holder.mImageView);
        }
        else{
            holder.mImageView.setImageResource(R.drawable.icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SendData(shopModel);
            }
        });
        if(position==getItemCount()-1){
            holder.mUnderLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public void filterList(List<Shop> filteredList) {
        searchList = filteredList;
        notifyDataSetChanged();
    }
}
