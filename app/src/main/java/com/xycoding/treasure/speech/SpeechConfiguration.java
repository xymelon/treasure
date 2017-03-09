package com.xycoding.treasure.speech;

import android.net.Uri;

public class SpeechConfiguration {

    //Your credentials can be found in your Nuance Developers portal, under "Manage My Apps".
    public static final String NUANCE_APP_KEY = "14f602baa312ae7a3fcb437133cde07642159e0b8c1a2639cc769370ec722a00682cba0b3e69857d34c8ee6895cfded8e47de3719709aa647f3ed46bb0fe6c18";
    public static final String NUANCE_APP_ID = "NMDPPRODUCTION_NetEase________20170306013002";
    public static final String NUANCE_SERVER_HOST = "kvm.nmdp.nuancemobility.net";
    public static final String NUANCE_SERVER_PORT = "443";
    public static final Uri NUANCE_SERVER_URI = Uri.parse("nmsps://" + NUANCE_APP_ID + "@" + NUANCE_SERVER_HOST + ":" + NUANCE_SERVER_PORT);

    public static final String XUNFEI_APP_ID = "531924c7";

}



