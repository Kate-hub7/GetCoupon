package ru.sukhikh.appgetcoupon.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.sukhikh.appgetcoupon.Adapter.CouponListAdapter;
import ru.sukhikh.appgetcoupon.FavoritesDB;
import ru.sukhikh.appgetcoupon.Fragments.ListFragment;
import ru.sukhikh.appgetcoupon.JsonModel.PromoCode;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.sukhikh.appgetcoupon.R;

public class ShopFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CouponListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View FragmentView;
    private Shop ShopModel;
    private CouponListAdapter.ClickListener listener;
    private List<String> favshops;
    private List<String> tokenAndCheck =new ArrayList<>();
    private Boolean check;

    private RoundedImageView PreviewImage;
    private ImageButton FavButton;
    private CircleImageView Image;
    private TextView Title;
    private TextView Description;

    private FavoritesDB favDB;

    public ShopFragment(Shop shopModel){ ShopModel = shopModel; }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView = inflater.inflate(R.layout.fragment_shop, container, false);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        favDB = new FavoritesDB(getActivity());


        listener = new CouponListAdapter.ClickListener() {
            @Override
            public void SendData(final PromoCode Coupon) {

                TextView ShopName = bottomSheetDialog.findViewById(R.id.shopName);
                ShopName.setText(ShopModel.getName());

                TextView CouponDescr = bottomSheetDialog.findViewById(R.id.couponDescription);
                final Button CouponName = bottomSheetDialog.findViewById(R.id.couponName);
                TextView ExpireDate = bottomSheetDialog.findViewById(R.id.date);

                CouponDescr.setText(Coupon.getPromoDescription());
                CouponName.setText(Coupon.getCoupon());
                SimpleDateFormat formatData = new SimpleDateFormat("dd.MM.yyyy");
                ExpireDate.setText("Дата окончания "+formatData.format(new Date(Coupon.getEstimatedDate())));

                Button share = bottomSheetDialog.findViewById(R.id.shareCoupon);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "👋 Привет!\n\n"+
                                Coupon.getPromoDescription()+"\n\uD83D\uDE80 Подробности можешь узнать в GetCoupon");
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent,"Поделиться"));
                    }
                });

               CouponName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", CouponName.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        final String test = CouponName.getText().toString();
                        CouponName.setText("Успешно скопировано");
                        CouponName.setBackgroundResource(R.drawable.button_style_copy);
                        CouponName.setTextColor(getResources().getColor(R.color.white));

                        final Timer timer = new Timer();

                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        CouponName.setText(test);
                                        CouponName.setBackgroundResource(R.drawable.button_style);
                                        CouponName.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                });
                            }
                        }, 1000L);
                    }
                });
                bottomSheetDialog.show();
            }
        };
        buildRecyclerView();
        return FragmentView;
    }

    private void buildRecyclerView() {

        PreviewImage = FragmentView.findViewById(R.id.PrevImage);
        FavButton = FragmentView.findViewById(R.id.FavButton);
        Image = FragmentView.findViewById(R.id.CircleImage);
        Title = FragmentView.findViewById(R.id.Title);
        Description = FragmentView.findViewById(R.id.description);

        TextView CountCoupon  = FragmentView.findViewById(R.id.textCoupon);
        CountCoupon.setText(getResources().getQuantityString(R.plurals.plurals_coupon, ShopModel.getPromocodes().size(), ShopModel.getPromocodes().size()));
        Button Website = FragmentView.findViewById(R.id.ButtonWeb);
        Website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ShopModel.getUrlWebsite()));
                startActivity(browserIntent);
            }
        });

        Button Share = FragmentView.findViewById(R.id.ButtonShare);
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, SendMessage());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"Поделиться"));
            }
        });

        Title.setText(ShopModel.getName());
        Description.setText(ShopModel.getShopDescription());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(ShopModel.getPlaceholderColor()));

        if(ShopModel.getUrlImage()!=null && ShopModel.getUrlImage().length()>0){
            Picasso.with(getActivity()).load(ShopModel.getUrlImage()).placeholder(gradientDrawable).into(Image);
        }
        else{
            Image.setImageResource(R.drawable.icon);
        }

        if(ShopModel.getUrlPrevImage()!=null && ShopModel.getUrlPrevImage().length()>0){
            Picasso.with(getActivity()).load(ShopModel.getUrlPrevImage()).placeholder(gradientDrawable).into(PreviewImage);
        }
        else{
            PreviewImage.setImageResource(R.drawable.icon);
        }


     final String NameShop = ShopModel.getName();

        if(favDB.check_status(NameShop)){
            FavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        else{
            FavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }


     FavButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if(ShopModel.getFavStatus().equals("0")) {
                 ShopModel.setFavStatus("1");
                 Date AddTime = new Date();
                 favDB.insertIntoTheDatabase(ShopModel.getName(), ShopModel.getCategoryName(), AddTime.getTime(),
                         ShopModel.getKeyID(), ShopModel.getFavStatus());
                 FavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
             }
             else{
                 ShopModel.setFavStatus("0");
                 favDB.remove_fav(ShopModel.getKeyID());
                 FavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
             }
             check = ListFragment.SettingsFragment.getPushState();//true;
             if(check==null){
                 check = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
             }

             tokenAndCheck.add(check.toString());
            favshops=favDB.getListShops();

             SharedPreferences sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
             tokenAndCheck.add(sPref.getString("TOKEN_VALUE", ""));

             JsonNotification w;
             if(!tokenAndCheck.get(1).equals("")){
                 w = (JsonNotification) new JsonNotification().execute(tokenAndCheck, favshops);
             }

         }
     });
       // readCursorData(ShopModel);
        mRecyclerView =  FragmentView.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new CouponListAdapter(getActivity(), ShopModel, listener);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private String SendMessage(){
        String begin = "👋 Привет!\n\n";
        String line1 = "В "+ShopModel.getName()+" сейчас действуют:\n";
        String lines="";
        for(int i=0;i<ShopModel.getPromocodes().size();i++) {
            lines += "\uD83D\uDD25" + ShopModel.getPromocodes().get(i).getCoupon() +
                    ": " + ShopModel.getPromocodes().get(i).getPromoDescription()+"\n\n";
        }
        String end = "\uD83D\uDE80 Подробности можешь узнать в GetCoupon";

        return begin+line1+lines+end;
    }

    public static class JsonNotification extends AsyncTask<List<String>, String, Void> {

        @Override
        protected Void doInBackground(List<String>... arrayLists) {

            HttpURLConnection connection = null;
            DataOutputStream bos = null;
            String url = "http://closeyoureyes.jelastic.regruhosting.ru/add-user-info";

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "GetCoupon Android 0.0");
                connection.setRequestProperty("Authorization", "Basic YWxpdmUtZ2V0Y291cG9uLXVzZXI6OW1sNzBEbzdqWHRtMDVIS0s=");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();

                JSONObject jo = new JSONObject();
                if(arrayLists[0].get(0).equals("true")){
                    jo.put("userPreferPush", true);
                }
                if(arrayLists[0].get(0).equals("false")){
                    jo.put("userPreferPush", false);
                }

                jo.put("deviceToken", arrayLists[0].get(1));
                jo.put("favoriteShops", new JSONArray(arrayLists[1]));

                bos = new DataOutputStream(connection.getOutputStream());
                String test = jo.toString();

                bos.write(test.getBytes(StandardCharsets.UTF_8));
                Log.d("", "server response: " + connection.getResponseCode()+arrayLists[0].get(0));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(connection!=null){
                        connection.disconnect();
                    }
                    if(bos!=null){
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
