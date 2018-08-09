package com.simair.android.androidutils.openapi.kakao;

public enum KaKaoAppKey {
    KEY_NATIVEAPP("2d824584adf2d1bb8a1bad4f4987bdae"),
    KEY_REST_API("e6844a2f1a38385b9739daa75f36e34a"),
    KEY_JAVASCRIPT("2bb3db4eec18d1304bf34818e917f1ae"),
    KEY_ADMIN("888abb03348404c2175fdd029f3384cc"),
    ;

    public final String key;

    KaKaoAppKey(String key) {
        this.key = key;
    }
}
