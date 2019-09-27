package ru.gamingcore.staffstats.json;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonData {
    private static final String TAG = "INWIKE";

    public static Emp_data ParseExec(JSONObject obj) {
        Emp_data emp_data = new Emp_data();
        try {
            emp_data.firstname = obj.getString("firstname");
            emp_data.secondname = obj.getString("secondname");
            emp_data.lastname = obj.getString("lastname");
            String tmp = obj.getString("photo");
            byte[] buf = Base64.decode(tmp, Base64.NO_WRAP);
            emp_data.photo = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            emp_data.organization = obj.getString("organization");
            emp_data.department = obj.getString("department");
            emp_data.position = obj.getString("position");
            emp_data.type = obj.getString("type");
            emp_data.schedule = obj.getString("schedule");
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getLocalizedMessage());
            return null;
        }
        return emp_data;
    }

    public static List_violation ParseViolation(JSONObject obj) {
        List_violation list_violation = new List_violation();
        try {
            JSONArray violations = obj.getJSONArray("violation_list");
            list_violation.violations = new ArrayList<>(violations.length());

            for (int j = 0; j < violations.length(); j++) {
                JSONObject data = violations.getJSONObject(j);
                Violation violation = new Violation();
                violation.violation_id = data.getString("violation_id");
                violation.violation_name = data.getString("violation_name");
                list_violation.violations.add(violation);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getLocalizedMessage());
            return null;
        }
        return list_violation;
    }

    public static Allow_scan ParseAllowScan(JSONObject obj) {
        Allow_scan allow_scan = new Allow_scan();
        try {
            String tmp = obj.getString("Scan");

            byte[] buf = Base64.decode(tmp, Base64.NO_WRAP);

            allow_scan.scan = BitmapFactory.decodeByteArray(buf, 0, buf.length);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getLocalizedMessage());
            return null;
        }
        return allow_scan;
    }
}
