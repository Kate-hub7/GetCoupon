package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.JsonModel.Ad;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VerticalRecyclerAdapter extends RecyclerView.Adapter<VerticalRecyclerAdapter.ExampleViewHolder> {

    private Context context;
    public List<Category> categoryList;
    public List<List<Ad>> arrayAds;
    private final ClickListener listener;
    private HorizontalRecyclerAdapter.ClickListener listenerHorizintal;
    private HotRecyclerAdapter.ClickListener listenerHot;


    public interface ClickListener {
        public void SendData(Category categoryModel);
        public void ItemData(Shop shopModel);
    }
    public VerticalRecyclerAdapter(Context context, List<Category> categoryList, List<List<Ad>> arrayAds, final ClickListener listener) {
        this.context=context;
        this.categoryList =categoryList;
        this.arrayAds = arrayAds;
        this.listener=listener;

    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder  {

        RecyclerView recyclerView;
        RecyclerView recyclerAds;
        TextView titleCategory;
        Button SeeAll;

        public ExampleViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            recyclerView=(RecyclerView)itemView.findViewById(R.id.items_in_category);
            recyclerAds=(RecyclerView)itemView.findViewById(R.id.items_in_ad);
            titleCategory = (TextView)itemView.findViewById(R.id.category_name);
            SeeAll= (Button)itemView.findViewById(R.id.see_all);

        }

    }
    @Override
    public VerticalRecyclerAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_vertical,
                parent, false);
        return new VerticalRecyclerAdapter.ExampleViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, final int position) {
        final Category verticalModel= categoryList.get(position);
        final List<Ad> AdModel = arrayAds.get(position);
        final String title = verticalModel.getCategoryName();
        ArrayList<Shop> singleItem = verticalModel.getShops();

        listenerHorizintal = new HorizontalRecyclerAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                listener.ItemData(shopModel);
            }
        };
        listenerHot = new HotRecyclerAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                listener.ItemData(shopModel);
            }
        };

        holder.SeeAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                listener.SendData(verticalModel);
            }
        });

        holder.titleCategory.setText(title);
        if(position==0)
        {
            holder.SeeAll.setVisibility(View.GONE);
            HotRecyclerAdapter hotRecyclerAdapter = new HotRecyclerAdapter(context, singleItem, listenerHot);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(hotRecyclerAdapter);
        }
        else {
            HorizontalRecyclerAdapter horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(context, singleItem, listenerHorizintal);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(horizontalRecyclerAdapter);
        }

        if(position==7){
            holder.SeeAll.setVisibility(View.VISIBLE);
        }
        AdsAdapter adsAdapter;
        if(position== categoryList.size()-1){
            adsAdapter = new AdsAdapter(context, AdModel, false);
        }
        else{
            adsAdapter = new AdsAdapter(context, AdModel, true);
        }

        holder.recyclerAds.setHasFixedSize(true);
        holder.recyclerAds.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerAds.setAdapter(adsAdapter);



    }

    @Override
    public int getItemCount() { return categoryList.size();}

}
