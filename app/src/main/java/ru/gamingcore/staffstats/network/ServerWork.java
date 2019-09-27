package ru.gamingcore.staffstats.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import ru.gamingcore.staffstats.network.GetJsonAsync;

public class ServerWork {
    private static final String TAG = "INWIKE";

    private static final String verif_id = "66b457e4-7c02-11e2-9362-001b11b25590";
    private static final String HOST = "http://46.174.89.208:6060/Inwike/hs/Inwike/ID/";
    private static final String AUTH = "web:web";

    private static final String UID = "exec_uid";
    private static final String ALLOW_ID = "allow_id";


    private String current_uid;

    private Listener listener;

    public interface Listener {
        void onError();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    private GetJsonAsync setRequest() {
        GetJsonAsync dataAsync = new GetJsonAsync();
        dataAsync.setListener(resultListener);
        dataAsync.setHost(HOST);
        dataAsync.setAuth(AUTH);
        return dataAsync;
    }

    public void execData(String uid) {
        current_uid = uid;

        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EXEC_DATA);
        dataAsync.addParam(UID, current_uid);
        dataAsync.execute();
    }

    public void allowScan(String allow_id) {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.ALLOW_SCAN);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(ALLOW_ID, allow_id);
        dataAsync.execute();
    }


    public void listViolation() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.LIST_VIOLATION);
        dataAsync.execute();

    }

    private GetJsonAsync.AsyncTaskListener resultListener = new GetJsonAsync.AsyncTaskListener() {
        @Override
        public void onResult(String result) {
            try {
                if (result == null) {
                    throw new JSONException("null string");
                }

                JSONObject obj = new JSONObject(result);

                //Exec_data exec_data = JsonData.ParseExec(obj);

             /* if (allow_scan != null) {
                    listener.onAllow_scan(allow_scan);
                    return;
                }*/
            } catch (JSONException e) {
                Log.e(TAG, "JSONException " + e);
                if (listener != null) {
                    listener.onError();
                }
            }
            if (listener != null) {
                listener.onError();
            }
        }

        @Override
        public void onError() {
            if (listener != null) {
                listener.onError();
            }
        }
    };

/*
    //(c) http://www.pocketmagic.net/?p=1662
    private String m_szDevIDShort = "35" + //we make this look like a valid IMEI
            Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
            Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
            Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
            Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
            Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
            Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
            Build.USER.length() % 10; //13 digits */
}