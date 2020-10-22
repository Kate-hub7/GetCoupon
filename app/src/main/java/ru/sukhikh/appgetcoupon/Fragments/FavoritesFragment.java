package ru.sukhikh.appgetcoupon.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import ru.sukhikh.appgetcoupon.Adapter.PagerAdapter;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import ru.sukhikh.appgetcoupon.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private View root;
    private List<Category> CategoryList;
    private ViewPager pager;
    private boolean isChanged =true;

    public FavoritesFragment(List<Category> JSONList){
        CategoryList = JSONList;
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favorites, container, false);


        final TabLayout tabLayout = root.findViewById(R.id.tabbar);
        pager = root.findViewById(R.id.recyclerPager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), CategoryList);

        pager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state==0)
                    pager.getAdapter().notifyDataSetChanged();
            }
        });

        return root;
    }

}

