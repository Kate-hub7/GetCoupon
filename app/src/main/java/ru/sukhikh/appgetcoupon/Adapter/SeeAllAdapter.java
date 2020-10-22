package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.FavoritesDB;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.Fragments.ListFragment;
import ru.sukhikh.appgetcoupon.R;
import ru.sukhikh.appgetcoupon.Fragments.ShopFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SeeAllAdapter extends RecyclerView.Adapter<SeeAllAdapter.ViewHolder> {

    private List<Shop> shopList;
    private String CategoryTitle;
    private Context context;
    private FavoritesDB favDB;
    private final ClickListener listener;
    private String token;

    private List<String> favshops;
    private List<String> tokenAndCheck =new ArrayList<>();

    public interface ClickListener {
        public void SendData(Shop shopModel);
    }

    public SeeAllAdapter(String token, List<Shop> shopList, String Title, Context context, ClickListener listener) {
        this.shopList = shopList;
        CategoryTitle = Title;
        this.context = context;
        this.listener=listener;
        this.token = token;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        favDB = new FavoritesDB(context);
        //create table on first
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            createTableOnFirstStart();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_shop,
                parent,false);
        return new ViewHolder(view, listener);
    }



    @Override
    public void onBindViewHolder(SeeAllAdapter.ViewHolder holder, int position) {
        final Shop shopItem = shopList.get(position);

        readCursorData(shopItem, holder);
        holder.titleTextView.setText(shopItem.getName());
        holder.TextDescription.setText(shopItem.getShortShopDescription());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(shopItem.getPlaceholderColor()));

        if(shopItem.getUrlImage()!=null && shopItem.getUrlImage().length()>0){
            Picasso.with(context).load(shopItem.getUrlImage()).placeholder(gradientDrawable).into(holder.imageView);
        }
        else{
            holder.imageView.setImageResource(R.drawable.icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SendData(shopItem);
            }
        });
        if(position==getItemCount()-1){
            holder.underLine.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public void filterList(List<Shop> filteredList) {
        shopList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        TextView titleTextView;
        TextView TextDescription;
        View underLine;
        Button favBtn;

        public ViewHolder( View itemView, ClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.textView);
            TextDescription = itemView.findViewById(R.id.textView2);
            favBtn = itemView.findViewById(R.id.buttonLike);
            underLine = itemView.findViewById(R.id.underline);

            //add to fav btn
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Shop shopItem = shopList.get(position);


                    if(shopItem.getFavStatus().equals("0")) {
                        shopItem.setFavStatus("1");
                        Date AddTime = new Date();
                        favDB.insertIntoTheDatabase(shopItem.getName(), CategoryTitle, AddTime.getTime(),
                                 shopItem.getKeyID(), shopItem.getFavStatus());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else{
                        shopItem.setFavStatus("0");
                        favDB.remove_fav(shopItem.getKeyID());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                    Boolean check = ListFragment.SettingsFragment.getPushState();//true;
                    if(check==null){
                        check = NotificationManagerCompat.from(context).areNotificationsEnabled();
                    }
                    tokenAndCheck.add(check.toString());
                    favshops=favDB.getListShops();
                    tokenAndCheck.add(token);
                    ShopFragment.JsonNotification w;
                    if(!tokenAndCheck.get(1).equals("")){
                        w = (ShopFragment.JsonNotification) new ShopFragment.JsonNotification().execute(tokenAndCheck, favshops);
                    }
                }
            });
        }
    }

    private void createTableOnFirstStart() {
      //  favDB.insertEmpty();

        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void readCursorData(Shop shopItem, ViewHolder viewHolder) {
        Cursor cursor = favDB.read_all_data(shopItem.getKeyID());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavoritesDB.FAVORITE_STATUS));
                shopItem.setFavStatus(item_fav_status);

                //check fav status
                if (item_fav_status != null && item_fav_status.equals("1")) {
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed()){
                cursor.close();
            }

            db.close();
        }

    }

}

