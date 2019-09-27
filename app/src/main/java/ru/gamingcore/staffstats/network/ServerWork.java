package ru.gamingcore.staffstats.network;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ru.gamingcore.staffstats.json.Emp_data;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.json.JsonData;
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
    private GetJsonAsync.AsyncTaskListener resultListener = new GetJsonAsync.AsyncTaskListener() {
        @Override
        public void onResult(String result) {
            try {
                if (result == null) {
                    throw new JSONException("null string");
                }

                JSONObject obj = new JSONObject(result);
                Emp_data emp_data = JsonData.ParseExec(obj);
                Emp_rating emp_rating = JsonData.ParseRating(obj);
                Upload_data upload_data = JsonData.ParseUpload(obj);

                if (upload_data != null) {
                    listener.onUpload();
                    return;
                }
                if (emp_rating != null) {
                    listener.onEmp_rating(emp_rating);
                    return;
                }
                if (emp_data != null) {
                    listener.onExec_data(emp_data);
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

    public void empRating() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_RATING);
        dataAsync.addParam(UID, current_uid);
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

    public interface Listener {
        void onExec_data(Emp_data emp_data);

        void onEmp_rating(Emp_rating emp_rating);

        void onError();

        void onUpload();
    }


}