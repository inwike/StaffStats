package ru.gamingcore.staffstats.network;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetJsonAsync extends AsyncTask<Void, Void, String> {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String EXEC_DATA = "emp_data";
    public static final String UPLOAD_PHOTO = "upload_photo";
    public static final String EMP_RATING = "emp_rating";
    public static final String EMP_RECOM = "emp_recom";
    public static final String EMP_UID = "emp_uid";
    public static final String EMP_SMILE = "emp_smile";


    public static final String EMP_DETAILS_KNLD = "detail_knld";
    public static final String EMP_DETAILS_SOC = "detail_soc";
    public static final String EMP_DETAILS_RESP = "detail_resp";
    public static final String EMP_DETAILS_ACTIV = "detail_activ";
    public static final String EMP_DETAILS_INNOV = "detail_innov";
    public static final String EMP_DETAILS_ENT = "detail_ent";
    public static final String DEFAULT_DATE = "20190901";
    public static final String DATE_DETAIL = "date_detail";


    private static final String TAG = "INWIKE";
    private String method = GET;
    private String host;
    private String auth;
    private String command;
    private List<Param> params = new ArrayList<>();

    private AsyncTaskListener listener;

    public void setListener(AsyncTaskListener listener) {
        this.listener = listener;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void addParam(String name, String value) {
        params.add(new Param(name, value));
    }

    @Override
    protected String doInBackground(Void... voids) {
        String answer = "";

        try {

            if (host == null || auth == null || command == null)
                throw new Exception("error null");

            if (method.contains(GET)) {
                host = String.format("%s%s%s", host, command, getParams());
            } else {
                host = String.format("%s%s", host, command);
            }

            URL url = new URL(host);

            byte[] encoding = Base64.encode(auth.getBytes(), Base64.NO_WRAP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; param=value");
            connection.setRequestProperty("Authorization", "Basic ".concat(new String(encoding)));

            if (method.contains(POST)) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStream os = connection.getOutputStream();
                os.write(getPostParams().getBytes(StandardCharsets.UTF_8));
                connection.connect();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error " + connection.getResponseCode() + connection.getResponseMessage());
            }

            InputStream content = connection.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));

            String line;
            while ((line = in.readLine()) != null) {
                answer = answer.concat(line);
            }

        } catch (Exception e) {
            Log.e(TAG, command + " = Exception " + e.getLocalizedMessage());

            if (listener != null) {
                listener.onError();
            }
        }
        return answer;
    }

    @Override
    protected void onPostExecute(String answer) {
        if (listener != null) {
            listener.onResult(answer);
        }
    }

    private String getParams() {
        String result = "?";

        for (Param param : params) {
            result = result.concat(String.format("%s=%s&", param.name, param.value));
        }
        return result;
    }

    private String getPostParams() {
        JSONObject result = new JSONObject();
        for (Param param : params) {
            try {
                result.put(param.name, param.value);
            } catch (JSONException ignored) {
            }
        }
        return result.toString();
    }

    public interface AsyncTaskListener {
        void onResult(String result);

        void onError();
    }

    class Param {
        String name;
        String value;

        Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}