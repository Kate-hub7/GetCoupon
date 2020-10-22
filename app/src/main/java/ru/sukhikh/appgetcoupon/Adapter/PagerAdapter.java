package ru.sukhikh.appgetcoupon.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.byCategory;
import ru.sukhikh.appgetcoupon.byDate;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

   private List<Category> categoryList;
   private String[] tabs;

    @Override
    public int getItemPosition(@NonNull Object object) {
        
        return POSITION_NONE;
    }

    public PagerAdapter(FragmentManager fm, List<Category> categoryList) {
       super(fm);
       this.categoryList=categoryList;
       tabs = new String[]{"По дате", "По категории"};
   }
    @NonNull
    @Override
    public Fragment getItem(int position) {

       switch (position){
           case 0:
               return new byDate(categoryList);
           case 1:
               return new byCategory(categoryList);
       }
       return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

}
