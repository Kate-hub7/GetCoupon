package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.FavoritesDB;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.Fragments.ListFragment;
import ru.sukhikh.appgetcoupon.R;
import ru.sukhikh.appgetcoupon.Fragments.ShopFragment;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;
    private List<Shop> favItemList;
    private FavoritesDB favDB;
    private final ClickListener listener;
    private String token;

    public interface ClickListener {
        public void SendData(Shop shopModel);
    }

    public FavoritesAdapter(Context context, List<Shop> favItemList, ClickListener listener, String token){
        this.context=context;
        this.favItemList=favItemList;
        this.listener=listener;
        this.token =token;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_favorites, parent, false);
        favDB = new FavoritesDB(context);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.ViewHolder holder, int position) {

        final Shop favmodel = favItemList.get(position);
        holder.favTextView.setText(favmodel.getName());
        holder.favTextDescription.setText(favmodel.getShortShopDescription());


        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(favmodel.getPlaceholderColor()));

        if(favmodel.getUrlImage()!=null && favmodel.getUrlImage().length()>0){
            Picasso.with(context).load(favmodel.getUrlImage()).placeholder(gradientDrawable).into(holder.ImageView);
        }
        else{
            holder.ImageView.setImageResource(R.drawable.icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SendData(favmodel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favItemList.size();
    }

    public void filterList(List<Shop> filteredList) {
        favItemList = filteredList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView favTextView;
        TextView favTextDescription;
        Button favBtn;
        ImageView ImageView;

        private List<String> favshops;
        private List<String> tokenAndCheck =new ArrayList<>();

        public ViewHolder( View itemView, ClickListener listener) {
            super(itemView);
            favTextView=itemView.findViewById(R.id.FavText);
            favTextDescription = itemView.findViewById(R.id.FavText2);
            favBtn = itemView.findViewById(R.id.FavButton);
            ImageView= itemView.findViewById(R.id.FavImage);


            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    final Shop favItem= favItemList.get(position);
                    favDB.remove_fav(favItem.getKeyID());
                    removeItem(position);

                    Boolean check = ListFragment.SettingsFragment.getPushState();//true;
                    favshops=favDB.getListShops();
                    if(check==null){
                        check = NotificationManagerCompat.from(context).areNotificationsEnabled();
                    }

                    tokenAndCheck.add(check.toString());
                    tokenAndCheck.add(FavoritesAdapter.this.token);
                    ShopFragment.JsonNotification w;
                    if(!tokenAndCheck.get(1).equals("")){
                        w = (ShopFragment.JsonNotification) new ShopFragment.JsonNotification().execute(tokenAndCheck, favshops);
                    }
                }
            });
        }
        private  void removeItem(int position){
            favItemList.get(position).setFavStatus("0");
            favItemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favItemList.size());
        }
    }
}
