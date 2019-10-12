package com.propya.suraksha.Helpers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.propya.suraksha.BluePrint.OnePlace;
import com.propya.suraksha.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlacesHelperClass {

    Context c;
    OnePlace[] safeplaces;
    ResultReady resultReady;
    public PlacesHelperClass(Context context,ResultReady resultReady)
    {
        this.c = context;
        this.resultReady = resultReady;
    }
//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.3675294,-71.186966&rankby=distance&keyword=atm|hospital|police|railway|station&key=AIzaSyCseULsIncv9KI9csnt5sEM1OrSIt5iOs
    public static String msgg;
    String urlFormat[] = {Constants.URLs.NEARBY_PLACES,"&location=","&rankby=distance","&keyword=","&key="+Constants.NearbyPlacesConstants.PLACESK};
    public void findNearbyPlaces(double latitude, double longitude)
    {
//        HashMap<String,Object> parameters = new HashMap<>();

//        parameters.put("location",""+latitude+","+longitude);
//        parameters.put("rankby","distance");
//        parameters.put("keyword","atm|hospital|police|railway|station");
//        parameters.put("key", Constants.NearbyPlacesConstants.PLACESK);
        String url =urlFormat[0];
        url+=urlFormat[1]+latitude+","+longitude;
        url+=urlFormat[2];
        url+=urlFormat[3]+"atm|hospital|police|railway|station";
        url+=urlFormat[4];
        ApiCallHelpers helpers = new ApiCallHelpers(c);
        helpers.callVolley(url, new HashMap<String, Object>(), new ApiCallHelpers.CallbackVolley() {
            @Override
            public void volleyCallBack(String msg) {
                Log.i(ApiCallHelpers.class.getName(),"error msg :"+msg);
                msgg=msg;
            }

            @Override
            public void volleyCallBack(JSONObject data) {
                Log.i(ApiCallHelpers.class.getName(),"Response");
                Log.i(ApiCallHelpers.class.getName(),data.toString());
                msgg=data.toString();
                try {
                    if(data.getString("status").equals("OK"))
                    {
                        JSONArray results = data.getJSONArray("results");
                        OnePlace[] safeplaceresults = new OnePlace[results.length()];
                        for(int i=0;i<results.length();i++)
                        {
                            OnePlace curPlace = new OnePlace();
                            JSONObject curObj = results.getJSONObject(i);//one result
                            JSONObject location =curObj.getJSONObject("geometry").getJSONObject("location");//geometry
                            curPlace.setLatitude(location.getDouble("lat"));
                            curPlace.setLongitude(location.getDouble("lng"));
                            curPlace.setName(curObj.getString("name"));
                            curPlace.setVicinity(curObj.getString("vicinity"));
                            safeplaceresults[i]=curPlace;

                        }

                        safeplaces=safeplaceresults;
                        resultReady.results(safeplaceresults);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //HANDLE JSON HERE


            }
        });

    }



    public  String getResponse()
    {
        return this.msgg;
    }
    public OnePlace[] getSafeplaces() {
        return safeplaces;
    }

    public interface ResultReady{
        public void results(OnePlace[] results);
    }

}
