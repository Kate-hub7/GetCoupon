package ru.sukhikh.appgetcoupon.JsonModel;

public class PromoCode {

    private String Coupon;
    private Long AddingDate;
    private Long EstimatedDate;
    private String PromoDescription;
    private String Website;

    public PromoCode(String coupon, Long addingDate, Long estimatedDate, String promoDescription) {
        Coupon = coupon;
        AddingDate = addingDate;
        EstimatedDate = estimatedDate;
        PromoDescription = promoDescription;
    }

    public String getCoupon() { return Coupon; }

    public Long getAddingDate() { return AddingDate; }

    public Long getEstimatedDate() { return EstimatedDate; }

    public String getPromoDescription() { return PromoDescription; }

    public String getWebsite() { return Website; }
}
