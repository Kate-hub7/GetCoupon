package ru.sukhikh.appgetcoupon.JsonModel;

public class Ad{

    private String urlImage;
    private String UrlWebsite;
    private Integer priority;


    public Ad(String urlImage, String urlWebsite, Integer priority){

        this.urlImage=urlImage;
        this.UrlWebsite = urlWebsite;
        this.priority = priority;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getUrlWebsite() { return UrlWebsite; }

    public Integer getPriority() { return priority; }
}
