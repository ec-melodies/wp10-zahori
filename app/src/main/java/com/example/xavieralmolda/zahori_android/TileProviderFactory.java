package com.example.xavieralmolda.zahori_android;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import android.util.Log;
import com.google.android.gms.maps.model.TileProvider;

/**
 * Created by xavia on 03/08/2016.
 */
public class TileProviderFactory {

    private static final String GEOSERVER_FORMAT =
            "http://h2-strabon.cloudapp.net:8080/ncWMS2/wms" +
                    "?service=WMS" +
                    "&version=1.3.0" +
                    "&request=GetMap" +
                    "&layers=a/Conc:Ud_T1_24139" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=256" +
                    "&height=256" +
                    "&srs=EPSG:900913" +
                    "&transparent=true" +
                    "bgcolor=transparent" +
                    "styles=default-scalar/x-Sst";

    private static final String NCWMS_FORMAT =
            "http://h2-strabon.cloudapp.net:8080/ncWMS2/wms" +
                    "?REQUEST=GetMap&VERSION=1.3.0&STYLES=default-scalar/x-Sst&CRS=EPSG:3857&WIDTH=256&HEIGHT=256" +
                    "&FORMAT=image/png&TRANSPARENT=true&LAYERS=a/Conc:Ud_T1_24139" +
                    "&BBOX=%f,%f,%f,%f" +
                    "&BGCOLOR=transparent";

    // return a geoserver wms tile layer
    public static TileProvider getTileProvider() {
        TileProvider tileProvider = new WMSTileProvider(256,256) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, NCWMS_FORMAT, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                //String s = "http://h2-strabon.cloudapp.net:8080/ncWMS2/wms?REQUEST=GetMap&VERSION=1.3.0&STYLES=default-scalar/x-Sst&CRS=CRS:84&WIDTH=256&HEIGHT=256&FORMAT=image/png&TRANSPARENT=true&LAYERS=a/Conc:Ud_T1_24139&BBOX=1.94483760194399,41.2274771267558,2.22140741572898,41.4760416016821&BGCOLOR=transparent";
                //String s = "http://h2-strabon.cloudapp.net:8080/ncWMS2/wms?REQUEST=GetMap&VERSION=1.3.0&STYLES=default-scalar/x-Sst&CRS=CRS:84&WIDTH=256&HEIGHT=256&TRANSPARENT=true&LAYERS=a/Conc:Ud_T1_24139&BBOX=1.94483760194399,41.2274771267558,2.22140741572898,41.4760416016821&BGCOLOR=transparent";
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        return tileProvider;
    }

}
