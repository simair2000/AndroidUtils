package com.simair.android.androidutils.openapi.land.data;

/**
 * Created by simair on 17. 7. 21.<br />
 * 지역코드
 */

public enum LAWDCode {
    CODE_11110(11110, "서울특별시 종로구"),
    CODE_11140(11140, "서울특별시 중구"),
    CODE_11170(11170, "서울특별시 용산구"),
    CODE_11200(11200, "서울특별시 성동구"),
    CODE_11215(11215, "서울특별시 광진구"),
    CODE_11230(11230, "서울특별시 동대문구"),
    CODE_11260(11260, "서울특별시 중랑구"),
    CODE_11290(11290, "서울특별시 성북구"),
    CODE_11305(11305, "서울특별시 강북구"),
    ;

    public final int code;
    public final String location;

    LAWDCode(int code, String location) {
        this.code = code;
        this.location = location;
    }
}
