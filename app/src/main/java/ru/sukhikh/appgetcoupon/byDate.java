package ru.sukhikh.appgetcoupon;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.Adapter.FavoritesAdapter;
import ru.sukhikh.appgetcoupon.Fragments.ShopFragment;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;

import java.util.ArrayList;
import java.util.List;

public class byDate extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private List<Shop> favItemList = new ArrayList<>();
    private List<Category> categoryList;
    private FavoritesAdapter favAdapter;
    private FavoritesAdapter.ClickListener listener;

    public byDate(List<Category> categoryList){
        this.categoryList=categoryList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View FragmentView = inflater.inflate(R.layout.favrecycler, container, false);

        listener=new FavoritesAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                Fragment selectedFragment = new ShopFragment(shopModel);

                FragmentTransaction transaction = getParentFragment().getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.up_to_down, R.anim.exit_up_to_down, R.anim.down_to_up, R.anim.exit_down_to_up);
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };

        if(isOnline(getContext())){
            UpdateFavorites();
        }

        emptyText = FragmentView.findViewById(R.id.empty_view);
        recyclerView = FragmentView.findViewById(R.id.recyclerViewFav);

        SharedPreferences sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = sPref.getString("TOKEN_VALUE", "");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        favAdapter = new FavoritesAdapter(getActivity(), favItemList, listener, token);
        recyclerView.setAdapter(favAdapter);

        if(favItemList.size()==0){
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
        EditText editText = (EditText) FragmentView.findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return FragmentView;
    }

    private void filter(String text) {
        List<Shop> filteredList = new ArrayList<>();

        for (Shop item : favItemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            for(int i=0;i<item.getTags().size();i++)
                if(item.getTags().get(i).toLowerCase().contains(text.toLowerCase()))
                    if(!filteredList.contains(item))
                        filteredList.add(item);
        }
        favAdapter.filterList(filteredList);
    }

    private static boolean isOnline(Context context)//проверка подключения к интернету
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    private void UpdateFavorites(){
        FavoritesDB favDB = new FavoritesDB(getContext());
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor;
        cursor = favDB.select_all_favorite_list();

        if(favItemList.size()!=0){
            favItemList.clear();
        }

        try {
            while (cursor.moveToNext()) {
                boolean count = false;
                String categoryTitle = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_CATEGORY_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavoritesDB.KEY_ID));
                String shopTitle = cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_SHOP_TITLE));
                for (int i = 0; i < categoryList.size(); i++)
                    for (int j = 0; j < categoryList.get(i).getShops().size(); j++) {
                        if (categoryList.get(i).getShops().get(j).getName().equals(shopTitle)) {
                            count=true;
                            favItemList.add(categoryList.get(i).getShops().get(j));
                            if (!(categoryList.get(i).getCategoryName().equals(categoryTitle))) {
                                favDB.update_category(categoryList.get(i).getCategoryName(), categoryTitle, shopTitle);
                            }
                        }
                    }
                if(!count){
                    favDB.remove_fav(id);
                }

            }
        } finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }
}
