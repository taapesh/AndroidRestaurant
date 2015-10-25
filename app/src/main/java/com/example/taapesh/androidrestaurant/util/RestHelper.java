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

    private static final String TABLES_ENDPOINT = "http://taapesh.pythonanywhere.com/tables/";
    private static final String SERVER_TABLES_ENDPOINT = "http://taapesh.pythonanywhere.com/servertables/";
    private static final String REQUEST_ENDPOINT = "http://taapesh.pythonanywhere.com/makerequest/";

    public static JSONArray getServerTables(int serverId) {
        HttpURLConnection getTablesConn = null;
        Exception _e = null;

        try {
            String data = URLEncoder.encode("serverId", "UTF-8") + "=" + URLEncoder.encode(""+serverId, "UTF-8");
            URL url = new URL(SERVER_TABLES_ENDPOINT);
            getTablesConn = (HttpURLConnection) url.openConnection();
            getTablesConn.setRequestMethod("POST");
            getTablesConn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(getTablesConn.getOutputStream());
            wr.write(data);
            wr.flush();
            getTablesConn.connect();
            wr.close();
            JSONArray tablesResult = readInputArray(getTablesConn);
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
        return null;
    }

    public static JSONObject createOrJoinTable(String addr, int tableNum) {
        HttpURLConnection conn = null;
        Exception _e;

        try {
            String data = URLEncoder.encode("tableNum", "UTF-8") + "=" + URLEncoder.encode(""+tableNum, "UTF-8");
            data += URLEncoder.encode("addr", "UTF-8") + "=" + URLEncoder.encode(addr, "UTF-8");
            URL url = new URL(TABLES_ENDPOINT);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            conn.connect();
            JSONObject result = readInputObject(conn);
            Log.i("CreateOrJoinResult", result.toString());

            if (result.has("ownerId")) {
                // Return the table that already exists
                return result;
            }
            else {
                // Create the table
            }
        }
        catch (JSONException e) {
            _e = e;
        }
        catch (MalformedURLException e) {
            _e = e;
        }
        catch (IOException e) {
            _e = e;
        }
        return null;
    }

    public static int leaveTable(int userId) {
        return -1;
    }

    public static int joinTable() {
        return -1;
    }

    public static JSONObject makeRequest(int ownerId) {
        HttpURLConnection conn = null;
        Exception _e = null;

        try {
            URL url = new URL(REQUEST_ENDPOINT + ownerId);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            JSONObject result = readInputObject(conn);
            Log.i("RequestResult", result.toString());
            return result;
        }
        catch (MalformedURLException e) {
            _e = e;
        }
        catch (JSONException e) {
            _e = e;
        }
        catch (IOException e) {
            _e = e;
        }
        finally {
            if (_e != null) {
                Log.i("RequestException", _e.toString());
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    private static JSONArray readInputArray(HttpURLConnection conn) throws IOException, JSONException {
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

        return new JSONArray(sb.toString());
    }

    private static JSONObject readInputObject(HttpURLConnection conn) throws IOException, JSONException {
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

        return new JSONObject(sb.toString());
    }
}
