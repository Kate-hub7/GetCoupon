package ru.sukhikh.appgetcoupon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import ru.sukhikh.appgetcoupon.JsonModel.PromoCode;
import ru.sukhikh.appgetcoupon.JsonModel.Shop;
import ru.sukhikh.appgetcoupon.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ExampleViewHolder> {
    private Context context;
    private List<PromoCode> promoCodeList;
    private String UrlImage;
    private String PlaceholderColor;
    private ClickListener listener;

    public interface ClickListener {
        public void SendData(PromoCode coupon);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView mImageView;
        public TextView mTextView1;
        public Button button;
        public TextView BeginData;
        public TextView EndData;



        public ExampleViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.button);
            BeginData = itemView.findViewById(R.id.beginData);
            EndData = itemView.findViewById(R.id.endData);
        }
    }

    public CouponListAdapter(Context context, Shop shopModel, ClickListener listener) {

        this.context=context;
        this.listener = listener;
        UrlImage = shopModel.getUrlImage();
        PlaceholderColor = shopModel.getPlaceholderColor();
        promoCodeList = shopModel.getPromocodes();
    }

    @Override
    public CouponListAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_coupon,
                parent, false);
        return new CouponListAdapter.ExampleViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {

        final PromoCode couponModel = promoCodeList.get(position);
        holder.mTextView1.setText(couponModel.getPromoDescription());
        holder.button.setText(couponModel.getCoupon());


        SimpleDateFormat formatData = new SimpleDateFormat("dd.MM.yyyy");
        holder.BeginData.setText("Добавлено: "+formatData.format(new Date(couponModel.getAddingDate())));
        holder.EndData.setText("Действует до: "+formatData.format(new Date(couponModel.getEstimatedDate())));

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(PlaceholderColor));

        if(UrlImage!=null && UrlImage.length()>0){
            Picasso.with(context).load(UrlImage).placeholder(gradientDrawable).into(holder.mImageView);
        }
        else{
            holder.mImageView.setImageResource(R.drawable.icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SendData(couponModel);
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SendData(couponModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promoCodeList.size();
    }

}
