package com.simair.android.androidutils.openapi.kakao;

import com.simair.android.androidutils.R;

public enum CategoryGroup {
    ALL("ALL", "모두", R.drawable.ok_mark),
    LargeSupermarket("MT1", "대형마트", R.drawable.market),
    ConvStore("CS2", "편의점", R.drawable.conv_shop),
    Kindergarten("PS3", "어린이집, 유치원", R.drawable.kindergarten),
    School("SC4", "학교", R.drawable.school),

    Academy("AC5", "학원", R.drawable.book),
    ParkingLot("PK6", "주차장", R.drawable.parked_car),
    GasStation("OL7", "주유소, 충전소", R.drawable.gas_station),
    SubwayStation("SW8", "지하철역", R.drawable.subway),

    Bank("BK9", "은행", R.drawable.funds),
    CulturalFacilities("CT1", "문화시설", R.drawable.skyline),
    BrokerageHouse("AG2", "중개업소", R.drawable.visitor),
    PublicInstitution("PO3", "공공기관", R.drawable.public_museum),

    TouristAttraction("AT4", "관광명소", R.drawable.road_map),
    Accommodation("AD5", "숙박", R.drawable.hotel),
    Restaurant("FD6", "음식점", R.drawable.restaurant),
    Cafe("CE7", "카페", R.drawable.coffee_cup),

    Hospital("HP8", "병원", R.drawable.hospital),
    Pharmacy("PM9", "약국", R.drawable.medicine),
    ;

    public final String code;
    public final String displayName;
    public final int imgResId;

    CategoryGroup(String code, String displayName, int imgResId) {
        this.code = code;
        this.displayName = displayName;
        this.imgResId = imgResId;
    }

    public static CategoryGroup valueFromCode(String code) {
        for(CategoryGroup categoryGroup : CategoryGroup.values()) {
            if(categoryGroup.code.equals(code)) {
                return categoryGroup;
            }
        }
        return null;
    }
}
