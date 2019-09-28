package ru.gamingcore.staffstats.network;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import ru.gamingcore.staffstats.json.Avail;
import ru.gamingcore.staffstats.json.Detail;
import ru.gamingcore.staffstats.json.Emp_data;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.json.JsonData;
import ru.gamingcore.staffstats.json.Upload_data;
import ru.gamingcore.staffstats.utils.ImageUtil;

import static ru.gamingcore.staffstats.network.GetJsonAsync.DATE_DETAIL;
import static ru.gamingcore.staffstats.network.GetJsonAsync.DEFAULT_DATE;

public class ServerWork {
    private static final String TAG = "INWIKE";
    private static final String verif_id = "87433448-7cc0-11e2-9368-001b11b25590";
    private static final String HOST = "http://10.70.1.205/Inwike_HR/hs/Inwike/ID/";
    private static final String AUTH = "web:web";
    private static final String UID = "emp_uid";
    private static final String FILE = "file";

    private String current_uid;

    private Listener listener;
    private GetJsonAsync.AsyncTaskListener resultListener = new GetJsonAsync.AsyncTaskListener() {
        @Override
        public void onResult(String result) {
            if (result == null) {
                return;
            }
            try {
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
            } catch (JSONException ignored) {}

            try {
                JSONArray array = new JSONArray(result);
                List<Detail> details = JsonData.ParseDetail(array);
                List<Avail> avails = JsonData.ParseAvail(array);

                if (avails != null && avails.size() > 0) {
                    listener.onAvails(avails);
                    return;
                }

                if (details != null && details.size() > 0) {
                    listener.onDetails(details);
                    return;
                }
            } catch (JSONException ignored) {}

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

    public void empRating() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_RATING);
        dataAsync.addParam(UID, current_uid);
        dataAsync.execute();
    }

    public void Test(String image) {
        GetJsonAsync dataAsync =new GetJsonAsync();
        dataAsync.setHost("http://10.70.0.149:21000/");
        dataAsync.setCommand("recognize");
        dataAsync.setAuth("");
        dataAsync.setMethod(GetJsonAsync.POST);
        dataAsync.addParam("image", image);
        dataAsync.execute();
    }

    public void empAvail() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_RECOM);
        dataAsync.addParam(UID, current_uid);
        dataAsync.execute();
    }

    public void empDetails() {
        empDetailsKnld();
        empDetailsSoc();
        empDetailsResp();
        empDetailsActiv();
        empDetailsInnov();
        empDetailsEnt();

    }

    private void empDetailsKnld() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_KNLD);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
        dataAsync.execute();
    }

    private void empDetailsSoc() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_SOC);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
        dataAsync.execute();
    }

    private void empDetailsResp() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_RESP);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
        dataAsync.execute();
    }

    private void empDetailsActiv() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_ACTIV);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
        dataAsync.execute();
    }

    private void empDetailsInnov() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_INNOV);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
        dataAsync.execute();
    }

    private void empDetailsEnt() {
        GetJsonAsync dataAsync = setRequest();
        dataAsync.setCommand(GetJsonAsync.EMP_DETAILS_ENT);
        dataAsync.addParam(UID, current_uid);
        dataAsync.addParam(DATE_DETAIL, DEFAULT_DATE);
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

    public interface Listener {
        void onExec_data(Emp_data emp_data);

        void onEmp_rating(Emp_rating emp_rating);

        void onDetails(List<Detail> details);

        void onAvails(List<Avail> details);

        void onError();

        void onUpload();
    }
}