package ru.sukhikh.appgetcoupon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import ru.sukhikh.appgetcoupon.Fragments.FavoritesFragment;
import ru.sukhikh.appgetcoupon.Fragments.HomeFragment;
import ru.sukhikh.appgetcoupon.Fragments.ListFragment;
import ru.sukhikh.appgetcoupon.Fragments.SearchFragment;
import ru.sukhikh.appgetcoupon.JsonModel.Category;
import ru.sukhikh.appgetcoupon.JsonModel.PromoCode;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.Onboard.CustomIntro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<Category> MainList = new ArrayList<>();
    List<String> settings = new ArrayList<>();
    private HomeFragment.RefreshWork refreshWork;
    WeakReference wrActivity;

    SharedPreferences sPref;

    String url = "https://usrnm242.github.io/getcoupon/conf.json";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wrActivity = new WeakReference<MainActivity>(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SaveToken();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        boolean isFirstStart = getPrefs.getBoolean("FIRST_START", true);

        if (isFirstStart) {
            Intent i = new Intent(MainActivity.this, CustomIntro.class);
            startActivity(i);
        }

        refreshWork = new HomeFragment.RefreshWork() {
            @Override
            public void RW() {
                MainList.clear();
                JsonTask w = (MainActivity.JsonTask) new JsonTask().execute(url, "false");
            }
        };


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(getIntent().getExtras()!= null){
            JsonTask w = (JsonTask) new JsonTask().execute(url,"true");
        }else{
            JsonTask w = (JsonTask) new JsonTask().execute(url, "false");
        }
    }
    private void SaveToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("TOKEN_VALUE", token);
                ed.commit();
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected( MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment(MainList, settings.get(4), refreshWork);
                            setTitle("Home");
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment(MainList);
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment(MainList);
                            break;
                        case R.id.nav_list:
                            selectedFragment= new ListFragment(settings);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };


    public class JsonTask extends AsyncTask<String, List<String>, List<Category>> {

        String AdsUrl;
        private JSONObject Connect(String url) throws IOException, JSONException {
            HttpURLConnection connection = null;
            InputStream stream = null;
            connection = (HttpURLConnection) new URL(url).openConnection();
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
            JSONObject obj = new JSONObject(finalJson);
            return obj;
        }
        @Override
        protected List<Category> doInBackground(String... strings){

            HttpURLConnection connection = null;
            InputStream stream = null;

            try {
                JSONObject obj = Connect(strings[0]);
                settings.add(obj.getString("GooglePlayLink"));
                settings.add(obj.getString("contactEmail"));
                settings.add(obj.getString("androidLicense"));
                settings.add(obj.getString("businessCardWebsite"));

                JSONObject newObj = obj.getJSONObject("defaultServer");
                String newUrl = newObj.getString("serverAddress")+newObj.getString("androidJson");
                settings.add(newObj.getString("serverAddress")+newObj.getString("androidAds"));
                AdsUrl = newObj.getString("serverAddress")+newObj.getString("androidAds");

                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "GetCoupon Android 0.0" );
                connection.setRequestProperty("Authorization", "Basic YWxpdmUtZ2V0Y291cG9uLXVzZXI6OW1sNzBEbzdqWHRtMDVIS0s=");
                if(strings[1].equals("true")){
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setDefaultUseCaches(false);
                    connection.setUseCaches(false);
                }
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

                for(int i=0;i<array.length();i++)
                {
                    JSONArray ListShop = array.getJSONObject(i).getJSONArray("shops");
                    List<Shop> shops = new ArrayList<>();
                    for(int j=0;j<ListShop.length();j++)
                    {
                        JSONArray ListCoupon = ListShop.getJSONObject(j).getJSONArray("promoCodes");
                        List<PromoCode> promoCodes = new ArrayList<>();
                        for(int k=0;k<ListCoupon.length();k++)
                            promoCodes.add(new PromoCode(ListCoupon.getJSONObject(k).getString("coupon"),
                                    ListCoupon.getJSONObject(k).getLong("addingDate"),
                                    ListCoupon.getJSONObject(k).getLong("estimatedDate"),
                                    ListCoupon.getJSONObject(k).getString("promoCodeDescription")));

                        List<String> tag = new ArrayList<>();
                        for(int k=0;k<ListShop.getJSONObject(j).getJSONArray("tags").length();k++){
                            tag.add(ListShop.getJSONObject(j).getJSONArray("tags").get(k).toString());
                        }
                        shops.add(new Shop(ListShop.getJSONObject(j).getString("name"),
                                ListShop.getJSONObject(j).getString("shopDescription"),
                                ListShop.getJSONObject(j).getString("shopShortDescription"),
                                ListShop.getJSONObject(j).getInt("priority"),
                                ListShop.getJSONObject(j).getString("websiteLink"),
                                ListShop.getJSONObject(j).getString("previewImageLink"),
                                ListShop.getJSONObject(j).getString("imageLink"),
                                ListShop.getJSONObject(j).getString("placeholderColor"),
                                promoCodes, "0",tag,
                                array.getJSONObject(i).getString("categoryName")));
                    }
                    Collections.sort(shops, new Comparator<Shop>() {
                        @Override
                        public int compare(Shop o1, Shop o2) {
                            return Integer.compare(o2.getIsHot(), o1.getIsHot());
                        }
                    });
                    MainList.add(new Category(array.getJSONObject(i).getString("categoryName"),
                            shops,
                            array.getJSONObject(i).getString("defaultImageLink"),
                            array.getJSONObject(i).getInt("priority")));
                }
                Collections.sort(MainList, new Comparator<Category>() {
                    @Override
                    public int compare(Category o1, Category o2) {
                        return Integer.compare(o2.getIsHot(), o1.getIsHot());
                    }
                });

                return MainList;
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
        protected void onPostExecute(final List<Category> result) {
            super.onPostExecute(result);
            final Activity activity = (Activity) wrActivity.get();
            if (activity != null && !activity.isFinishing())
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(MainList, AdsUrl,refreshWork)).commitAllowingStateLoss();
        }
    }

}

