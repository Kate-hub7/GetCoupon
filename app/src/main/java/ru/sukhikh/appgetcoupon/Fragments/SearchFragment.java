package ru.sukhikh.appgetcoupon.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.Adapter.SearchAdapter;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;


import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Shop> shopList;
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View FragmentView;
    private SearchAdapter.ClickListener listener;

    public SearchFragment(List<Category> JSONList){

        shopList = new ArrayList<>();
        for(int i=0;i<JSONList.size();i++) {
            ArrayList<Shop> ShopList = JSONList.get(i).getShops();
            shopList.addAll(ShopList);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView=inflater.inflate(R.layout.fragment_search, container, false);

        listener=new SearchAdapter.ClickListener() {
            @Override
            public void SendData(Shop shopModel) {
                Fragment selectedFragment = new ShopFragment(shopModel);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.up_to_down, R.anim.exit_up_to_down, R.anim.down_to_up, R.anim.exit_down_to_up);
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };

        buildRecyclerView();
        final AutoCompleteTextView editText = FragmentView.findViewById(R.id.edittext);

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
        final String[] tagList = {"подарок", "обувь", "одежда", "сумка", "разное",
                "образование", "смартфон", "доставка", "ресторан", "кафе"};

        final ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, tagList);
        editText.setAdapter(adapter);

        return FragmentView;
    }
    private void filter(String text) {
        List<Shop> filteredList = new ArrayList<>();

        for (Shop item : shopList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            for(int i=0;i<item.getTags().size();i++)
                if(item.getTags().get(i).toLowerCase().contains(text.toLowerCase())) {
                    if (!filteredList.contains(item)) {
                        filteredList.add(item);
                    }
                }
        }
        mAdapter.filterList(filteredList);
    }

    private void buildRecyclerView() {
        mRecyclerView =  FragmentView.findViewById(R.id.recyclerViewSearch);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new SearchAdapter(getActivity(), shopList, listener);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
