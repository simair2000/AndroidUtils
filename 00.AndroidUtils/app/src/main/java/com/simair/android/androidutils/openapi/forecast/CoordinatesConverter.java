package com.simair.android.androidutils.openapi.forecast;

import android.util.Log;

/**
 * Created by simair on 17. 7. 11.<br />
 * 위도(latitude), 경도(longitude) 좌표와 동네예보 좌표간의 변환<br />
 * OpenAPI 사용자 활용가이드(기상청 신규 동네예보정보조회서비스)v1.3 참고
 */

public class CoordinatesConverter {

    private static final String TAG = CoordinatesConverter.class.getSimpleName();

    private static volatile CoordinatesConverter instance;
    private static final int NX = 149; // X축 격자점 수
    private static final int NY = 253; // Y축 격자점 수
    private static double PI;
    private static double DEGRAD;
    private static double RADDEG;
    private static double re;
    private static double olon;
    private static double olat;
    private static double sn;
    private static double sf;
    private static double ro;
    private LamcParameter map;

    private CoordinatesConverter() {
        map = new LamcParameter();
        map.Re = (float) 6371.00877;
        map.grid = (float) 5.0;
        map.slat1 = (float) 30.0;
        map.slat2 = (float) 60.0;
        map.olon = (float) 126.0;
        map.olat = (float) 38.0;
        map.xo = 210/map.grid;
        map.yo = 675/map.grid;
        map.first = 0;
    }

    public static CoordinatesConverter getInstance() {
        if(instance == null) {
            synchronized (CoordinatesConverter.class) {
                instance = new CoordinatesConverter();
            }
        }
        return instance;
    }

    /**
     * Lambert Conformal Parameter
     */
    private class LamcParameter {
        public float Re;        // 사용할 지구반경 [km]
        public float grid;      // 격자간격 [km]
        public float slat1;     // 표준위도 [degree]
        public float slat2;     // 표준위도 [degree]
        public float olon;      // 기준점의 경도 [degree]
        public float olat;      // 기준점의 위도 [degree]
        public float xo;        // 기준점의 X좌표 [격자거리]
        public float yo;        // 기준점의 Y좌표 [격자거리]
        public int first;       // 시작여부 (0 = 시작)
    }

    public class Coord {
        public float x;
        public float y;
    }

    public class GeoCode {
        public float longitude;
        public float latitude;
    }

    public GeoCode coord2geo(float x, float y) {
        double slat1;
        double slat2;
        double ra;
        double theta;
        double xn;
        double yn;
        double alat;
        double alon;

        if(x < 1 || x > NX || y < 1 || y > NY) {
            Log.e(TAG, "Range exception");
            return null;
        }
        if(map.first == 0) {
            PI = Math.asin(1.0) * 2.0;
            DEGRAD = PI/180.0;
            RADDEG = 180.0/PI;

            re = map.Re / map.grid;
            slat1 = map.slat1 * DEGRAD;
            slat2 = map.slat2 * DEGRAD;
            olon = map.olon * DEGRAD;
            olat = map.olat * DEGRAD;

            sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            sf = Math.tan(PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            ro = Math.tan(PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);
            map.first = 1;
        }

        xn = x - map.xo;
        yn = ro - y + map.yo;
        ra = Math.sqrt(xn * xn + yn * yn);
        if(sn < 0) {
            ra *= -1;
        }
        alat = Math.pow((re * sf / ra), (1.0/sn));
        alat = 2.0 * Math.atan(alat) - PI * 0.5;
        if(Math.abs(xn) <= 0) {
            theta = 0;
        } else {
            if(Math.abs(yn) <= 0) {
                theta = PI * 0.5;
                if(xn < 0) {
                    theta *= -1;
                }
            } else {
                theta = Math.atan2(xn, yn);
            }
        }
        alon = theta / sn + olon;
        GeoCode ret = new GeoCode();
        ret.latitude = (float) (alat * RADDEG);
        ret.longitude = (float) (alon * RADDEG);
        return ret;
    }

    public Coord geo2coord(double lat, double lon) {
        double slat1;
        double slat2;
        double ra;
        double theta;

        if(map.first == 0) {
            PI = Math.asin(1.0) * 2.0;
            DEGRAD = PI/180.0;
            RADDEG = 180.0/PI;

            re = map.Re / map.grid;
            slat1 = map.slat1 * DEGRAD;
            slat2 = map.slat2 * DEGRAD;
            olon = map.olon * DEGRAD;
            olat = map.olat * DEGRAD;

            sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            sf = Math.tan(PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            ro = Math.tan(PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);
            map.first = 1;
        }

        ra = Math.tan(PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        theta = lon * DEGRAD - olon;
        if(theta > PI) {
            theta -= 2.0 * PI;
        }
        if(theta < -PI) {
            theta += 2.0 * PI;
        }
        theta *= sn;
        Coord ret = new Coord();
        ret.x = (float)(ra * Math.sin(theta)) + map.xo;
        ret.y = (float)(ro - ra * Math.cos(theta)) + map.yo;
        return ret;
    }
}
