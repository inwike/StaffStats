package ru.gamingcore.staffstats.finger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.DialogFragment;

import ru.gamingcore.staffstats.R;


public class AuthorizeDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String PIN = "pin";
    private EditText Login;
    private EditText Pwd;

    private SharedPreferences mPreferences;
    private FingerprintHelper mFingerprintHelper;
    public OnMyDialogClick mOnMyDialogClick;

    public interface OnMyDialogClick {
        void onPositiveButtonClick(String login, String pwd);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_login, null, false);

        builder.setTitle(R.string.app_name)
                .setCancelable(true)
                .setView(view)
                .setPositiveButton(R.string.enter, this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Login = view.findViewById(R.id.Login);
        Pwd = view.findViewById(R.id.Pwd);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //prepareLogin();
        final String login = Login.getText().toString();
        final String pwd = Pwd.getText().toString();

        if (mOnMyDialogClick != null) {
            mOnMyDialogClick.onPositiveButtonClick(login, pwd);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (mPreferences.contains(PIN)) {
            prepareSensor();
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFingerprintHelper != null) {
            mFingerprintHelper.cancel();
        }
    }


    public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {
        private Context mContext;
        private CancellationSignal mCancellationSignal;

        FingerprintHelper(Context context) {
            mContext = context;
        }

        void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
            mCancellationSignal = new CancellationSignal();
            FingerprintManagerCompat manager = FingerprintManagerCompat.from(mContext);

            manager.authenticate(cryptoObject, 0, mCancellationSignal, this, null);
        }

        void cancel() {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
         /*   Cipher cipher = result.getCryptoObject().getCipher();
            String encoded = mPreferences.getString(PIN, null);
            String decoded = CryptoUtils.decode(encoded, cipher);*/
            dismiss();
        }

        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show();
        }

    }


}