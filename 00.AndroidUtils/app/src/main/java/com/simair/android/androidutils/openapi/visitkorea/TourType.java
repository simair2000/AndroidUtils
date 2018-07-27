package com.simair.android.androidutils.openapi.visitkorea;


import android.content.Context;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;

/**
 * Created by simair on 17. 7. 18.
 */

public enum TourType {
    TOUR_SPOT(R.string.tour_spot) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 12;
            } else {
                return 76;
            }
        }
    },
    CULTURAL_FACILITIES(R.string.cultural_facilities) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 14;
            } else {
                return 78;
            }
        }
    },
    EVENTS(R.string.tour_event) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 15;
            } else {
                return 85;
            }
        }
    },
    TOUR_COURSE(R.string.tour_course) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 25;
            } else {
                return 0;
            }
        }
    },          // 한국어만 지원
    LEISURE_SPORTS(R.string.leports) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 28;
            } else {
                return 75;
            }
        }
    },
    ACCOMMODATIONS(R.string.accommodation) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 32;
            } else {
                return 80;
            }
        }
    },
    SHOPPING(R.string.shopping) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 38;
            } else {
                return 79;
            }
        }
    },
    DINING(R.string.dining) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 39;
            } else {
                return 82;
            }
        }
    },
    TRANSPORTATION(R.string.transportation) {
        public int getCode(Context context) {
            String locale = Utils.getCurrentLocale(context);
            if(locale.contains("ko")) {
                return 0;
            } else {
                return 77;
            }
        }
    },    // 외국어만 지원
    ;

    public int stringRes;

    TourType(int stringRes) {
        this.stringRes = stringRes;
    }

    abstract int getCode(Context context);
}
