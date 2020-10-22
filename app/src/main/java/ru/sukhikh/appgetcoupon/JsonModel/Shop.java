package ru.sukhikh.appgetcoupon.JsonModel;

import java.util.List;

public class Shop{

    private String Name;
    private String ShopDescription;
    private String ShortShopDescription;
    private Integer IsHot;
    private String UrlWebsite;
    private String UrlImage;
    private String UrlPrevImage;
    private String PlaceholderColor;
    private String CategoryName;
    private String KeyID;
    private String FavStatus;
    private List<String> Tags;
    private List<PromoCode> Promocodes;

    public Shop(String name, String shopDescription, String shortShopDescription,
                Integer isHot, String urlWebsite, String urlImage, String urlPrevImage,
                String placeholderColor, List<PromoCode> promocodes,
                String favStatus, List<String> tags, String categoryName) {
        Name = name;
        ShopDescription = shopDescription;
        ShortShopDescription = shortShopDescription;
        IsHot = isHot;
        UrlWebsite = urlWebsite;
        UrlImage = urlImage;
        UrlPrevImage = urlPrevImage;
        PlaceholderColor = placeholderColor;
        Promocodes = promocodes;
        KeyID = String.valueOf(Name.hashCode());
        FavStatus = favStatus;
        Tags = tags;
        CategoryName = categoryName;
    }

    public String getName() { return Name; }

    public String getShopDescription() { return ShopDescription; }

    public String getShortShopDescription() { return ShortShopDescription; }

    public Integer getIsHot() { return IsHot; }

    public String getUrlWebsite() { return UrlWebsite; }

    public String getUrlImage() { return UrlImage; }

    public String getUrlPrevImage() { return UrlPrevImage; }

    public String getPlaceholderColor() { return PlaceholderColor; }

    public List<PromoCode> getPromocodes() { return Promocodes; }

    public String getKeyID() { return KeyID; }

    public String getFavStatus() { return FavStatus; }

    public void setFavStatus(String favStatus) { FavStatus = favStatus; }

    public List<String> getTags() { return Tags; }

    public String getCategoryName() { return CategoryName; }
}
