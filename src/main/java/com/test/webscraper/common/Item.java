package com.test.webscraper.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Item {

    private String title;
    private String link;
    private Double price;
    private String date;
    private PageAd pageAd;

    @Override
    public String toString() {
        return "Stan: " + title +
                "\nDatum objave: " + date +
                "\nKat: " + getFloor() +
                "\nGodina izgradnje: " + getYearBuilt() +
                "\nCijena: " + priceString() +
                "\nPovrsina: " + getAreaString() +
                "\nCijena kvadrata: " + priceByArea() +
                "\nLink: " + link;
    }

    private String getYearBuilt() {
        if(pageAd !=null){
            return pageAd.getYearBuilt();
        }
        return "-";
    }

    private String getFloor() {
        if(pageAd !=null) {
            return pageAd.getFloor();
        }
        return "-";
    }

    private String priceString() {
        return String.format("%,.2f", price) + " \u20ac";
    }

    private String getAreaString() {
        if(pageAd !=null){
            return String.format("%,.2f", pageAd.getAreaDouble()) + " m\u00b2";
        }
        return "-";
    }

    private String priceByArea() {
        if(pageAd !=null){
            return String.format("%,.2f", price / pageAd.getAreaDouble()) + " \u20ac/m\u00b2";
        }
        return "-";
    }

    public String hash(){
        return getLink()+getPrice();
    }

}
