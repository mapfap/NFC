package com.mapfap.tap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by mapfap on 6/5/2017 AD.
 */

class APICaller {

    public static final String SERVER = "http://192.168.1.102:3000";
    private static final int DEFAULT_TIMEOUT = 8;
    protected Context context;

    public APICaller(Context baseContext) {
        this.context = baseContext;
    }

    // POST /taps
    public APIResponse sendCardTap(String nfcId) {
        return sendRequest("POST", "/taps/", "nfc=" + nfcId);
    }

    // POST /taps
    public APIResponse sendManualTap(String employeeId) {
        return sendRequest("POST", "/taps/", "code=" + employeeId);
    }

    // GET /employees/search/ 
    public APIResponse findEmployeeByEmployeeId(String employeeId) {
        return sendRequest("GET", "/employees/search/" + employeeId, "");
    }

    // POST /employees/:code/register
    public APIResponse registerEmployeeCard(String nfcId, String employeeId) {
        return sendRequest("POST", "/employees/" + employeeId + "/register" , "nfc=" + nfcId);
    }

    // GET /events/active
    public APIResponse getActiveEvent() {
        return sendRequest("GET", "/events/active", "");

    }

    public APIResponse sendRequest(String method, String url, String body) {
        APIResponse response = new APIResponse();
        HTTPRequestTask request = new HTTPRequestTask(method, SERVER + url, body, response);
        request.execute();
        try {
            request.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            response.isError = true;
            response.errorDetails = "Request Timeout";
        } catch (Exception e) {
            response.isError = true;
            response.errorDetails = e.getMessage();
            e.printStackTrace();
        }
        return response;
    }

    private class HTTPRequestTask extends AsyncTask<Void, Void, Void> {

        private final String method;
        private final String url;
        private final String body;
        private ProgressDialog dialog = new ProgressDialog(context);
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
            this.dialog.setMessage("Sending..");
            this.dialog.show();
        }

        public void performGet() {
            HttpURLConnection urlc;
            urlc = null;
            BufferedReader in = null;
            try {
                URL url = new URL(this.url);
                urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestMethod(this.method);
                urlc.setDoInput(true);
                urlc.setUseCaches(false);
                urlc.setRequestProperty("Accept","application/json");
                int responseCode = urlc.getResponseCode();
                in = new BufferedReader(new InputStreamReader(urlc.getInputStream()),8096);
                StringBuilder fullResponse = new StringBuilder();
                String response;
                while ((response = in.readLine()) != null) {
                    fullResponse.append(response);
                }

                this.response.copy(new Gson().fromJson(fullResponse.toString(), APIWrapper.class).toAPIResponse());
                Log.d("APICaller", fullResponse.toString());
                in.close();
            } catch (Exception e) {
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
            } catch (Exception e) {
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
        }
    }


}
