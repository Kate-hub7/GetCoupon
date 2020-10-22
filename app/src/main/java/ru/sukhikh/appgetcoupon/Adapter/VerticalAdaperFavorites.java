package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;

import java.util.List;

public class VerticalAdaperFavorites extends RecyclerView.Adapter<VerticalAdaperFavorites.ExampleViewHolder>{


    private Context context;
    public List<List<Shop>> shopListByCategory;
    public List<String> CategoryTitle;
    private final ClickListener listener;
    private FavoritesAdapter.ClickListener listenerFav;
    private String token;

    public interface ClickListener {
        public void ItemData(Shop shopModel);
    }
    public VerticalAdaperFavorites(Context context, List<List<Shop>> shopListbyCategory,
                                   List<String> CategoryTitle, final ClickListener listener, String token) {
        this.context=context;
        this.shopListByCategory =shopListbyCategory;
        this.CategoryTitle = CategoryTitle;
        this.listener=listener;
        this.token = token;

    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder  {

        RecyclerView recyclerView;
        TextView titleCategory;

        public ExampleViewHolder(View itemView, VerticalAdaperFavorites.ClickListener listener) {
            super(itemView);

            recyclerView=(RecyclerView)itemView.findViewById(R.id.items_in_category);
            titleCategory = (TextView)itemView.findViewById(R.id.category_name);
        }

    }
    @NonNull
    @Override
    public VerticalAdaperFavorites.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_fav_by_category,
                parent, false);
        return new VerticalAdaperFavorites.ExampleViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalAdaperFavorites.ExampleViewHolder holder, int position) {

        List<Shop> currentItem = shopListByCategory.get(position);
        String Name = CategoryTitle.get(position);
        listenerFav = new FavoritesAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                listener.ItemData(shopModel);
            }
        };

        holder.titleCategory.setText(Name);
        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(context, currentItem, listenerFav, token);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        holder.recyclerView.setAdapter(favoritesAdapter);
    }

    @Override
    public int getItemCount() { return shopListByCategory.size(); }

    public void filterList(List<List<Shop>> filteredList, List<String> filteredCategory) {

        shopListByCategory = filteredList;
        CategoryTitle = filteredCategory;
        notifyDataSetChanged();
    }
}
