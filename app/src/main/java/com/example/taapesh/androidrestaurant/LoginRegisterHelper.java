package com.example.taapesh.androidrestaurant;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginRegisterHelper  {
    private static final String LOGIN_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/login/";
    private static final String USER_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/me/";
    private static final String REGISTER_ENDPOINT = "http://taapesh.pythonanywhere.com/auth/register/";
    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAIL = -1;
    public static final int REGISTER_SUCCESS = 1;
    public static final int REGISTER_FAIL = -1;
    private static final int CONNECTION_TIMEOUT = 7;
    private static final int DATARETRIEVAL_TIMEOUT = 7;

    public JSONObject tryLogin(String email, String password) {
        HttpURLConnection getTokenConn = null;
        HttpURLConnection getUserConn = null;

        Exception _e = null;
        JSONObject loginReturn = new JSONObject();

        try {
            // Encode data
            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            // Setup Http POST request with data
            URL url = new URL(LOGIN_ENDPOINT);
            getTokenConn = (HttpURLConnection) url.openConnection();
            getTokenConn.setRequestMethod("POST");
            getTokenConn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(getTokenConn.getOutputStream());
            wr.write(data);
            wr.flush();
            getTokenConn.connect();
            wr.close();

            JSONObject tokenResult = readInput(getTokenConn);
            String token = tokenResult.getString("auth_token");

            if (token != null) {
                url = new URL(USER_ENDPOINT);
                getUserConn = (HttpURLConnection) url.openConnection();
                getUserConn.setRequestMethod("GET");
                getUserConn.setRequestProperty("Authorization", "Token " + token);
                getUserConn.connect();
                JSONObject userResult = readInput(getUserConn);
                Log.i("LoginResult", userResult.toString());
                String firstName = userResult.getString("first_name");

                if (firstName != null) {
                    loginReturn.put("result", LOGIN_SUCCESS);
                    loginReturn.put("token", token);
                    loginReturn.put("firstName", userResult.getString("first_name"));
                    loginReturn.put("lastName", userResult.getString("last_name"));
                    loginReturn.put("email", userResult.getString("email"));
                    loginReturn.put("id", userResult.getString("id"));
                    loginReturn.put("phoneNumber", userResult.getString("phone_number"));

                    return loginReturn;
                }
                else {
                    // call did not return a user
                }
            }
            else {
                // token was null
            }
        }
        catch (MalformedURLException e) {
            // URL is invalid
            _e = e;
        }
        catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
            _e = e;
        }
        catch (IOException e) {
            // could not read response body
            // (could not create input stream)
            _e = e;
        }
        catch (JSONException e) {
            // response body is no valid JSON string
            _e = e;
        }
        finally {
            if (getTokenConn != null) {
                getTokenConn.disconnect();
            }

            if (getUserConn != null) {
                getUserConn.disconnect();
            }

            if (_e != null) {
                Log.e("LoginException", _e.toString());
            }
        }

        // If we made it here, login was unsuccessful
        try {
            loginReturn.put("result", LOGIN_FAIL);
        } catch (JSONException e) {}

        return loginReturn;
    }

    public JSONObject tryRegister(String firstName, String lastName, String phoneNumber, String email, String password) {
        Exception _e = null;
        HttpURLConnection registerConn = null;
        JSONObject registerReturn = new JSONObject();

        try {
            // Encode data
            String data = URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8");

            // Setup Http POST request with data
            URL url = new URL(REGISTER_ENDPOINT);
            registerConn = (HttpURLConnection) url.openConnection();
            registerConn.setRequestMethod("POST");
            registerConn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(registerConn.getOutputStream());
            wr.write(data);
            wr.flush();
            registerConn.connect();
            wr.close();

            JSONObject registerResult = readInput(registerConn);
            Log.i("RegisterResult", registerResult.toString());

            if (registerResult.get("id") != null) {
                // Registration succeeded, get user info and obtain auth token
                registerReturn.put("result", REGISTER_SUCCESS);
                JSONObject loginResult = tryLogin(email, password);
                int result = (int)loginResult.get("result");

                if (result == LOGIN_SUCCESS) {
                    registerReturn.put("token", loginResult.getString("token"));
                    registerReturn.put("firstName", loginResult.getString("firstName"));
                    registerReturn.put("lastName", loginResult.getString("lastName"));
                    registerReturn.put("email", loginResult.getString("email"));
                    registerReturn.put("id", loginResult.getString("id"));
                    registerReturn.put("phoneNumber", loginResult.getString("phoneNumber"));

                    return registerReturn;
                }
            }
        }
        catch (MalformedURLException e) {
            // URL is invalid
            _e = e;
        }
        catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
            _e = e;
        }
        catch (IOException e) {
            // could not read response body
            // (could not create input stream)
            _e = e;
        }
        catch (JSONException e) {
            // response body is no valid JSON string
            _e = e;
        }
        finally {
            if (registerConn != null) {
                registerConn.disconnect();
            }

            if (_e != null) {
                Log.e("RegisterException", _e.toString());
            }
        }

        // If we made it here, registration was unsuccessful
        try {
            registerReturn.put("result", REGISTER_FAIL);
        } catch (JSONException e) {}

        return registerReturn;
    }

    private JSONObject readInput(HttpURLConnection conn) throws IOException, JSONException {
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
        return new JSONObject(sb.toString());
    }
}
