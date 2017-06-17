package com.mapfap.tap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by mapfap on 6/5/2017 AD.
 */

class APICaller {

    public static final String SERVER = "http://13.228.28.4";
    private static final int DEFAULT_TIMEOUT = 10000;
    protected MainActivity activity;
    protected ProgressDialog dialog;
    protected UserState userState;

    public APICaller(MainActivity activity, ProgressDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    // POST /taps
    public void sendCardTap(UserState userState, String nfcId, boolean autoCreateEmployee) {
        this.userState = userState;
        sendRequest("POST", "/taps/", "nfc=" + nfcId+ "&" + "auto_create_employee=" + autoCreateEmployee);
    }

    // POST /taps
    public void sendManualTap(UserState userState, String employeeId, boolean autoCreateEmployee) {
        this.userState = userState;
        sendRequest("POST", "/taps/", "code=" + employeeId + "&" + "auto_create_employee=" + autoCreateEmployee);
    }

    // GET /employees/search/ 
    public void findEmployeeByEmployeeId(UserState userState, String employeeId) {
        this.userState = userState;
        sendRequest("GET", "/employees/search/" + employeeId, "");
    }

    // POST /employees/:code/register
    public void registerEmployeeCard(UserState userState, String nfcId, String employeeId) {
        this.userState = userState;
        sendRequest("POST", "/employees/register" , "nfc=" + nfcId + "&" + "code=" + employeeId );
    }

    // GET /events/active
    public void getActiveEvent(UserState userState) {
        this.userState = userState;
        sendRequest("GET", "/events/active", "");

    }

    public void sendRequest(String method, String url, String body) {
        APIResponse response = new APIResponse();
        HTTPRequestTask request = new HTTPRequestTask(method, SERVER + url, body, response);
        request.execute();
    }

    private class HTTPRequestTask extends AsyncTask<Void, Void, Void> {

        private final String method;
        private final String url;
        private final String body;
        private APIResponse response;

        public HTTPRequestTask(String method, String url, String body, APIResponse response) {
            this.method = method;
            this.url = url;
            this.body = body;
            this.response = response;
            Log.d("APICaller", this.url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            dialog.setProgress(0);
        }

        public void performGet() {
            HttpURLConnection urlc;
            urlc = null;
            BufferedReader in = null;
            try {
                URL url = new URL(this.url);
                dialog.setProgress(10);
                urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(DEFAULT_TIMEOUT);
                urlc.setReadTimeout(DEFAULT_TIMEOUT);
                urlc.setRequestMethod(this.method);
                urlc.setDoInput(true);
                urlc.setUseCaches(false);
                urlc.setRequestProperty("Accept","application/json");
                int responseCode = urlc.getResponseCode();
                dialog.setProgress(40);
                in = new BufferedReader(new InputStreamReader(urlc.getInputStream()),8096);
                StringBuilder fullResponse = new StringBuilder();
                String response;
                while ((response = in.readLine()) != null) {
                    fullResponse.append(response);
                }
                dialog.setProgress(90);
                this.response.copy(new Gson().fromJson(fullResponse.toString(), APIWrapper.class).toAPIResponse());
                Log.d("APICaller", fullResponse.toString());
                in.close();
                dialog.setProgress(100);
            } catch (FileNotFoundException e) {
                response.isError = true;
                response.errorDetails = "Missing Employee ID";
            } catch (SocketTimeoutException e) {
                response.isError = true;
                response.errorDetails = "Couldn't reach server at " + this.url;
                e.printStackTrace();
            } catch (Exception e) {
                response.isError = true;
                response.errorDetails = e.toString();
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void performPost() {
            HttpURLConnection urlc;
            urlc = null;
            OutputStreamWriter out = null;
            DataOutputStream dataout = null;
            BufferedReader in = null;
            try {
                URL url = new URL(this.url);
                urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(DEFAULT_TIMEOUT);
                urlc.setReadTimeout(DEFAULT_TIMEOUT);
                urlc.setRequestMethod(this.method);
                urlc.setDoOutput(true);
                urlc.setDoInput(true);
                urlc.setUseCaches(false);
                urlc.setAllowUserInteraction(false);
                urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlc.setRequestProperty("Accept","application/json");
                dataout = new DataOutputStream(urlc.getOutputStream());

                // perform POST operation
                dataout.writeBytes(this.body);

                int responseCode = urlc.getResponseCode();
                in = new BufferedReader(new InputStreamReader(urlc.getInputStream()),8096);

                StringBuilder fullResponse = new StringBuilder();
                String response;
                // write html to System.out for debug
                while ((response = in.readLine()) != null) {
                    fullResponse.append(response);
                }

                this.response.copy(new Gson().fromJson(fullResponse.toString(), APIWrapper.class).toAPIResponse());
                Log.d("APICaller", fullResponse.toString());
                in.close();
            } catch (SocketException e) {
                response.isError = true;
                response.errorDetails = "Couldn't reach server at " + this.url;
                e.printStackTrace();
            } catch (Exception e) {
                response.isError = true;
                response.errorDetails = e.getMessage();
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (this.method.equals("GET")) {
                performGet();
            } else if (this.method.equals("POST")) {
                performPost();
            } else {
                Log.e("APICaller", "Unsupported method '" + this.method + "'");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            response.userState = userState;
            activity.onAPIRespond(response);
        }
    }


}
