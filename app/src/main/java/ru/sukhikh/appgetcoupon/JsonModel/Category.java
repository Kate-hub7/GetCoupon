package ru.sukhikh.appgetcoupon.JsonModel;

import java.util.List;

public class Category{

    private String CategoryName;
    private Integer IsHot;
    private List<Shop> Shops;
    private String UrlDefaultImage;

    public Category(String categoryName, List<Shop> shops, String urlDefaultImage, Integer isHot) {
        CategoryName = categoryName;
        Shops = shops;
        UrlDefaultImage = urlDefaultImage;
        IsHot = isHot;
    }

    public String getCategoryName() { return CategoryName; }

    public List<Shop> getShops() { return Shops; }

    public String getUrlDefaultImage() { return UrlDefaultImage; }

    public Integer getIsHot() { return IsHot; }
}
