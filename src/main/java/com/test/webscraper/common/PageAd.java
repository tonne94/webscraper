package com.test.webscraper.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class PageAd {
    private String location;
    private String type;
    private String numFloors;
    private String numRooms;
    private String floor;
    private String maxFloor;
    private String area;
    private Double areaDouble;
    private String yearBuilt;
    private String yearRenovated;
    private String balcony;
    private String energy;
    private String id;

}
