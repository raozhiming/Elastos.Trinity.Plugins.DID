/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.trinity.plugins.did;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.did.DIDAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DIDPluginAdapter implements DIDAdapter {
    private final String TAG = "DIDPluginAdapter";
    private final int callbackId;
    private final CallbackContext callbackContext;
    private final String request = "{\"method\":\"resolvedid\",\"params\":{\"id\":\"%s\",\"all\":false}, \"id\": \"%s\"}";
    private String resolver = "https://coreservices-didsidechain-privnet.elastos.org";

    DIDPluginAdapter(int id, CallbackContext callbackContext) {
        this.callbackId = id;
        this.callbackContext = callbackContext;
    }

    private void sendEvent(JSONObject info) throws JSONException {
        info.put("id", callbackId);

        PluginResult res = new PluginResult(PluginResult.Status.OK, info);
        res.setKeepCallback(true);
        callbackContext.sendPluginResult(res);
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public String sendPost(String did, String id) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String didRequestBody = String.format(this.request, did, id);
            Log.d(TAG, "resolve url: " + this.resolver + " request body:" + didRequestBody);

            URL url = new URL(this.resolver);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(didRequestBody);

            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null){
                    stringBuilder.append(line);
                }
                rd.close();
            }
            else {
                Log.d(TAG, conn.getResponseMessage());
            }

            conn.disconnect();
        }
        catch (IOException e) {
            String msg = "sendPost exception: " + e.toString();
            Log.e(TAG, msg);
        }

        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }

    @Override
    public boolean createIdTransaction(String payload, String memo) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("payload", payload);
            ret.put("memo", memo);
            sendEvent(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String resolve(String did) {
        return sendPost(did, "");
    }
}