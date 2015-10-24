package com.example.taapesh.androidrestaurant.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RestHelper {
    private static final String SERVER_TABLES_ENDPOINT = "http://taapesh.pythonanywhere.com/servertables/";

    public static JSONArray getServerTables(int serverId) {
        HttpURLConnection getTablesConn = null;

        Exception _e = null;

        try {
            // Encode data
            String data = URLEncoder.encode("serverId", "UTF-8") + "=" + URLEncoder.encode(""+serverId, "UTF-8");
            // Setup Http POST request with data
            URL url = new URL(SERVER_TABLES_ENDPOINT);
            getTablesConn = (HttpURLConnection) url.openConnection();
            getTablesConn.setRequestMethod("POST");
            getTablesConn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(getTablesConn.getOutputStream());
            wr.write(data);
            wr.flush();
            getTablesConn.connect();
            wr.close();
            JSONArray tablesResult = readInput(getTablesConn);
            Log.i("TABLES", tablesResult.toString());
            return tablesResult;
        }
        catch (MalformedURLException e) {
            _e = e;
        }
        catch (IOException e) {
            _e = e;
        }
        catch (JSONException e) {
            _e = e;
        }
        finally {
            if (getTablesConn != null) {
                getTablesConn.disconnect();
            }
            if (_e != null) {
                Log.e("GetTablesException", _e.toString());
            }
        }

        Log.i("SHOULDNTBEHERE", "LOL");
        return null;
    }

    private static JSONArray readInput(HttpURLConnection conn) throws IOException, JSONException {
        // Create JSON object from response content
        InputStream is;
        if (conn.getResponseCode() / 100 == 2) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = rd.readLine()) != null) {
            sb.append(inputLine);
            sb.append("\n");
        }
        rd.close();

        // Return JSONObject result
        return new JSONArray(sb.toString());
    }
}
