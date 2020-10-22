package ru.sukhikh.appgetcoupon.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.Adapter.SeeAllAdapter;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;

import ru.sukhikh.appgetcoupon.R;

import java.util.ArrayList;
import java.util.List;

public class SeeAllFragment extends Fragment {

    private SeeAllAdapter Adapter;
    private View FragmentView;
    private Category CategoryModel;
    private SeeAllAdapter.ClickListener listener;

    public SeeAllFragment(Category categoryModel){
        CategoryModel = categoryModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView=inflater.inflate(R.layout.fragment_see_all, container, false);
        listener=new SeeAllAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                Fragment selectedFragment = new ShopFragment(shopModel);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };

        buildRecyclerView();

        Toolbar toolbar = (Toolbar) FragmentView.findViewById(R.id.toolbar);
        toolbar.setTitle(CategoryModel.getCategoryName());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

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

        for (Shop item : CategoryModel.getShops()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            for(int i=0;i<item.getTags().size();i++)
                if(item.getTags().get(i).toLowerCase().contains(text.toLowerCase()))
                    if(!filteredList.contains(item))
                        filteredList.add(item);
        }

        Adapter.filterList(filteredList);
    }

    private void buildRecyclerView() {

        SharedPreferences sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = sPref.getString("TOKEN_VALUE", "");

        RecyclerView recyclerView = FragmentView.findViewById(R.id.recyclerViewList);
        recyclerView.setHasFixedSize(true);
        Adapter=new SeeAllAdapter(token, CategoryModel.getShops(), CategoryModel.getCategoryName(), getActivity(), listener);
        recyclerView.setAdapter(Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
