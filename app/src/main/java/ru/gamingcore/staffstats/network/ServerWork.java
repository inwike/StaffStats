package ru.gamingcore.staffstats.network;

import android.graphics.Bitmap;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.gamingcore.staffstats.json.Allow_scan;
import ru.gamingcore.staffstats.json.Emp_data;
import ru.gamingcore.staffstats.json.JsonData;
import ru.gamingcore.staffstats.json.List_violation;
import ru.gamingcore.staffstats.json.Upload_data;
import ru.gamingcore.staffstats.utils.ImageUtil;

public class ServerWork {
    private static final String TAG = "INWIKE";

    private static final String verif_id = "87433448-7cc0-11e2-9368-001b11b25590";
    private static final String HOST = "http://10.70.1.205/Inwike_HR/hs/Inwike/ID/";
    private static final String AUTH = "web:web";
    // emp_data?emp_uid=87433448-7cc0-11e2-9368-001b11b25590
    private static final String UID = "emp_uid";
    private static final String ALLOW_ID = "allow_id";
    private static final String FILE = "file";

    private String current_uid;

    private Listener listener;

    public interface Listener {
        void onExec_data(Emp_data emp_data);

        void onAllow_scan(Allow_scan allow_scan);

        void onList_violation(List_violation list_violation);

        void onError();

        void onUpload();
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

    public void execData() {
        current_uid = verif_id;
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

    public void setPhoto(Bitmap bitmap) {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setMethod(GetJsonAsync.POST);
        dataAsync.setCommand(GetJsonAsync.UPLOAD_PHOTO);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(FILE, ImageUtil.convert(bitmap));
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
                Emp_data emp_data = JsonData.ParseExec(obj);
                Upload_data upload_data = JsonData.ParseUpload(obj);

                List_violation list_violation = JsonData.ParseViolation(obj);
                Allow_scan allow_scan = JsonData.ParseAllowScan(obj);

                if (upload_data != null) {
                    listener.onUpload();
                    return;
                }
                if (emp_data != null) {
                    listener.onExec_data(emp_data);
                    return;
                }
                if (list_violation != null) {
                    listener.onList_violation(list_violation);
                    return;
                }
                if (allow_scan != null) {
                    listener.onAllow_scan(allow_scan);
                    return;
                }
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