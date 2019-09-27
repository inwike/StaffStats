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
    private static final String TAG = "INWIKE";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String EXEC_DATA = "emp_data";
    public static final String UPLOAD_PHOTO = "upload_photo";
    public static final String ALLOW_SCAN = "allow_scan";
    public static final String CREATE_VIOLATION = "create_violation";
    public static final String LIST_VIOLATION = "list_violation";


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


    public interface AsyncTaskListener {
        void onResult(String result);
        void onError();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String answer = "";

        try {

            if(host == null || auth == null || command == null)
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
                throw new Exception("Error " + connection.getResponseCode());
            }

            InputStream content = connection.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));

            String line;
            while ((line = in.readLine()) != null) {
                answer = answer.concat(line);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getLocalizedMessage());

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

    class Param {
        String name;
        String value;

        Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}