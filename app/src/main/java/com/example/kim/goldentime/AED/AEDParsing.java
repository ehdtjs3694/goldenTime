package com.example.kim.goldentime.AED;

/**
 * Created by PL1 on 2017-04-10.
 */

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class AEDParsing {
    public final static String AED_URL = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire";
    public final static String KEY = "zKxD9Bvrm%2Fcfz0ZmYf7FUKEc0ZaFsfUK9jbQm1NDlCU%2FcgUBNNbEHB98fTCBoKxASP%2BO%2FDnXcgJp14wdX4i69Q%3D%3D";
    public static double lon = 0.0;
    public static double lat =0.0;

    public AEDParsing(LatLng latLng) {
        try {
            lon = latLng.longitude;
            lat = latLng.latitude;
            apiParserSearch();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws Exception
     */
    public ArrayList<AEDDTO> apiParserSearch() throws Exception {
        URL url = new URL(getURLParam(null));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");

        String tag = null;
        int event_type = xpp.getEventType();

        ArrayList<AEDDTO> list = new ArrayList<AEDDTO>();

        String wgs84Lon = null, wgs84Lat= null,name=null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("wgs84Lon")){
                    wgs84Lon = xpp.getText();
                    System.out.println(wgs84Lon);
                }else if(tag.equals("wgs84Lat")){
                    wgs84Lat = xpp.getText();
                }else if(tag.equals("org")){
                    name = xpp.getText();
                }
            }else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
                if (tag.equals("item")) {
                    AEDDTO entity = new AEDDTO();
                    entity.setXpos(Double.valueOf(wgs84Lon));
                    entity.setYpos(Double.valueOf(wgs84Lat));
                    entity.setName(name);

                    list.add(entity);
                }
            }
            event_type = xpp.next();
        }
        System.out.println(list.size());
        return list;
    }

    private String getURLParam(String search){
        String url = AED_URL+"?ServiceKey="+ KEY + "&WGS84_LON=" + lon + "&WGS84_LAT="+ lat ;
        if(search != null){
            url = url+"&org"+search;
        }
        return url;
    }

    /*public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        new AEDParsing();
    }*/

}


