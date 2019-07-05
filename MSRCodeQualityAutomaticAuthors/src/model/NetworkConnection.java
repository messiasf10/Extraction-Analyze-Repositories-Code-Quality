package model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Messias Filho on 19/06/2019.
 */

public class NetworkConnection {

    private static URL url;

    public static InputStreamReader getPage(String linkUrl){
        try {
            url = new URL(linkUrl);
            InputStream inputStream = NetworkConnection.url.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return inputStreamReader;
        } catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }

}
