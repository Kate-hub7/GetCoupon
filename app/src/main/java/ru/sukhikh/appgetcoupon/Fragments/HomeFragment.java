package ru.sukhikh.appgetcoupon.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import ru.sukhikh.appgetcoupon.Adapter.VerticalRecyclerAdapter;
import ru.sukhikh.appgetcoupon.JsonModel.Ad;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment{

    private View FragmentView;
    private RecyclerView VerticalRecycler;
    private String adsUrl;
    private List<List<Ad>> adListByCategory = new ArrayList<>();
    private VerticalRecyclerAdapter adapter;
    private List<Category> CategoryList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final RefreshWork refreshWork;
    private  VerticalRecyclerAdapter.ClickListener listener;

    public interface RefreshWork {
        public void RW();
    }

    public HomeFragment(List<Category> JSONList,String AdsUrl, RefreshWork refreshWork){
        CategoryList = JSONList;
        this.refreshWork = refreshWork;
        adsUrl=AdsUrl;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView=inflater.inflate(R.layout.fragment_home, container, false);


        listener=new VerticalRecyclerAdapter.ClickListener() {
            @Override
            public void SendData(Category categoryModel) {
                Fragment selectedFragment = new SeeAllFragment(categoryModel);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.right_to_left, R.anim.exit_right_to_left, R.anim.left_to_right, R.anim.exit_left_to_right);
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void ItemData(Shop shopModel) {
                Fragment selectedFragment = new ShopFragment(shopModel);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.up_to_down, R.anim.exit_up_to_down, R.anim.down_to_up, R.anim.exit_down_to_up);
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
        VerticalRecycler = (RecyclerView)FragmentView.findViewById(R.id.recyclerViewHome);
        VerticalRecycler.setHasFixedSize(true);
        VerticalRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        JsonAds w = (JsonAds) new JsonAds().execute(adsUrl);


        swipeRefreshLayout = (SwipeRefreshLayout)FragmentView.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                if(!isOnline(getContext())){
                    Toast.makeText(getContext(), "Проверьте подключение к интернету ", Toast.LENGTH_LONG).show();
                }
                else {
                    refreshWork.RW();
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        return FragmentView;
    }
    public static boolean isOnline(Context context)//проверка подключения к интернету
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public class JsonAds extends AsyncTask<String, List<String>, Void> {

        @Override
        protected Void doInBackground(String... strings){

            HttpURLConnection connection = null;
            InputStream stream = null;

            try {

                connection = (HttpURLConnection) new URL(strings[0]).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "GetCoupon Android 0.0" );
                connection.setRequestProperty("Authorization", "Basic YWxpdmUtZ2V0Y291cG9uLXVzZXI6OW1sNzBEbzdqWHRtMDVIS0s=");
                connection.connect();

                stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONArray array = new JSONArray(finalJson);

                for(int i=0;i<CategoryList.size();i++)
                    adListByCategory.add(new ArrayList<Ad>());

                for(int i=0;i<array.length();i++)
                {
                    JSONArray adsArr = array.getJSONObject(i).getJSONArray("adsList");
                    ArrayList<Ad> adsList = new ArrayList<>();
                    for(int j=0;j<adsArr.length();j++)
                        adsList.add(new Ad(adsArr.getJSONObject(j).getString("imageLink"),
                                adsArr.getJSONObject(j).getString("websiteLink"),
                                adsArr.getJSONObject(j).getInt("priority")));

                    Collections.sort(adsList, new Comparator<Ad>() {
                        @Override
                        public int compare(Ad o1, Ad o2) {
                            return Integer.compare(o2.getPriority(), o1.getPriority());
                        }
                    });
                    adListByCategory.set(array.getJSONObject(i).getInt("linkedSection"), adsList);
                }

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new VerticalRecyclerAdapter(getActivity(), CategoryList, adListByCategory,listener);
            VerticalRecycler.setAdapter(adapter);
        }
    }
}

