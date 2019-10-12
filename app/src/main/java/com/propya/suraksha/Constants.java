package com.propya.suraksha;

import android.content.Context;

import android.location.Location;

public class Constants {

    public static boolean runningTriggered=false;
    public static boolean SOSRaised = false;
    private static long defaultTimeOut = 30000;

    public class URLs{
        private static final String baseUrlFirebase = "https://us-central1-suraksha-9c58a.cloudfunctions.net/httpCall/";
        private static final String baseUrlServer = "http://f772636f.ngrok.io/";


        // firebase cloud functions
        public static final String ASK_HELP = baseUrlFirebase+"helpNeeded";


        //own server
        public static final String ASK_PING = baseUrlServer+"pingBackStart";
        public static final String END_PING = baseUrlServer+"endHelp";
        public static final String CALL_ME = baseUrlServer+"callMe";
        public static final String SMS_ME = baseUrlServer+"smsMe";

        public static final String NEARBY_PLACES="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";


    }

    public class NearbyPlacesConstants
    {
        public static final String PLACESK="AIzaSyCs_eULsIncv9KI9csnt5sEM1OrSIt5iOs";
    }

    public class SharedPrefConstants{
        public static final String SHARED_PREF = "sharedPrefBase";
        public static final String TIMEOUT = "sharedPrefTimeOut";


    }


    public class NotifyConstants {
        public static final String FCM_BROADCAST_LOC = "fcmLocationBroadcast";
        public static final String NOTIFICATION_CHANNEL = "askHelpChannel";

    }


    public class RadarConstants{
        public static final String TEST_PUB = "prj_test_pk_72c58e18a11101d9852e3adb4d987a234e007dbd";
        public static final String TEST_PRIVATE = "prj_test_sk_ade89dee51b5caf0862c1b22dd89dba9c4331b0c";
        public static final String LIVE_PUB = "prj_live_pk_a76becbd4647c3e9ad757a226e533215b1a30607";
        public static final String LIVE_PRIVATE = "prj_live_sk_4fe3458ef48cfdf31a3dbf670e67fbb1992e7e7a";

    }



    public static long getTimeout(Context c){
        return c.getSharedPreferences(SharedPrefConstants.SHARED_PREF,Context.MODE_PRIVATE)
                .getLong(SharedPrefConstants.TIMEOUT,Constants.defaultTimeOut);
    }



    public class TrackTravelConstants{

        public static final int interval = 30;
        public static final int checkActivity = 1;
        public static final float buffer = 0.9f;
        public static final String apiKey = "AIzaSyCs_eULsIncv9KI9csnt5sEM1OrSIt5iOs";


    }

}
