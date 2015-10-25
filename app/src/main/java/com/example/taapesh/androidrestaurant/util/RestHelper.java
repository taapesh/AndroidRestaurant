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
import java.net.URL;
import java.net.URLEncoder;

public class RestHelper {

    private static int testServerId = 4;
    private static final String TABLES_ENDPOINT = "http://taapesh.pythonanywhere.com/tables/";
    private static final String SERVER_TABLES_ENDPOINT = "http://taapesh.pythonanywhere.com/servertables/";
    private static final String REQUEST_ENDPOINT = "http://taapesh.pythonanywhere.com/makerequest/";
    private static final String TABLE_ADDR_ENDPOINT = "http://taapesh.pythonanywhere.com/gettableaddr/";
    private static final String JOIN_TABLE_ENDPOINT = "http://taapesh.pythonanywhere.com/createtable/";

    // errors
    private static final String TABLE_DNE = "-1";

    public static JSONArray getServerTables(int serverId) {
        HttpURLConnection conn = null;
        Exception _e = null;

        try {
            String data = URLEncoder.encode("serverId", "UTF-8") + "=" + URLEncoder.encode(""+serverId, "UTF-8");
            conn = setupPostRequest(data, SERVER_TABLES_ENDPOINT);
            JSONArray tablesResult = readInputArray(conn);
            //Log.i("TABLES", tablesResult.toString());
            return tablesResult;
        }
        catch (IOException e) {
            _e = e;
        }
        catch (JSONException e) {
            _e = e;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (_e != null) {
                Log.e("GetTablesException", _e.toString());
            }
        }
        return null;
    }

    public static JSONObject createOrJoinTable(String userId, String firstName, String lastName, String email, String addr, int tableNum) {
        HttpURLConnection conn = null;
        Exception _e;
        JSONObject result = null;

        try {
            String data = URLEncoder.encode("tableNum", "UTF-8") + "=" + URLEncoder.encode(""+tableNum, "UTF-8");
            data += "&" + URLEncoder.encode("addr", "UTF-8") + "=" + URLEncoder.encode(addr, "UTF-8");
            conn = setupPostRequest(data, TABLE_ADDR_ENDPOINT);
            result = readInputObject(conn);
            if (result != null) {
                Log.i("CreateOrJoinResult", result.toString());

                if (result.has("ownerId")) {
                    joinTable(result);
                    // Return the table that already exists
                    return result;
                }
                else if (result.has("error")) {
                    if (result.get("error").equals(TABLE_DNE)) {
                        // Connect to a server, then create the table
                        // int serverId = connectToServer()
                        JSONObject createResult = createTable(userId, testServerId, firstName, lastName, email, tableNum, addr);
                        return createResult;
                    }
                }
            }
            else {
                // Blank JSON response
            }
        }
        catch (Exception e) {
            _e = e;
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static int leaveTable(int userId) {
        return -1;
    }

    public static int joinTable() {
        return -1;
    }

    public static JSONObject createTable(String userId, int serverId, String firstName, String lastName, String email, int tableNum, String addr) {
        HttpURLConnection conn = null;

        try {
            String data = URLEncoder.encode("tableNum", "UTF-8") + "=" + URLEncoder.encode(""+tableNum, "UTF-8");
            data += "&" + URLEncoder.encode("serverId", "UTF-8") + "=" + URLEncoder.encode(""+serverId, "UTF-8");
            data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(addr, "UTF-8");
            data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("ownerFirstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("ownerLastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("ownerEmail", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

            URL url = new URL(TABLES_ENDPOINT);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            conn.connect();
            wr.close();

            JSONObject result = readInputObject(conn);
            Log.i("CreateTableResult", result.toString());
            return result;
        }
        catch (IOException e) {

        }
        catch (JSONException e) {

        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static void joinTable(JSONObject tableData) {

    }

    public static JSONObject makeRequest(int ownerId) {
        HttpURLConnection conn = null;
        Exception _e = null;

        try {
            conn = setupGetRequest(REQUEST_ENDPOINT + ownerId);
            JSONObject result = readInputObject(conn);
            Log.i("RequestResult", result.toString());
            return result;
        }
        catch (IOException e) {
            _e = e;
        }
        catch (JSONException e) {
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


    private static JSONArray readInputArray(HttpURLConnection conn) throws JSONException, IOException {
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

        if (sb.length() == 0) {
            return null;
        }
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
        if (sb.length() == 0) {
            return null;
        }
        return new JSONObject(sb.toString());
    }

    private static HttpURLConnection setupGetRequest(String dataUrl) throws IOException {
        URL url = new URL(dataUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }

    private static HttpURLConnection setupPostRequest(String data, String endpoint) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        conn.connect();
        wr.close();
        return conn;
    }
}
