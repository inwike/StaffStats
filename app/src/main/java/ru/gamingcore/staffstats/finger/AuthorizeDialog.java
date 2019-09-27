package ru.gamingcore.staffstats.finger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import ru.gamingcore.staffstats.activity.MainActivity;


public class AuthorizeDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private EditText mEditText;
    private SharedPreferences mPreferences;
    private FingerprintHelper mFingerprintHelper;
    private static final String PIN = "pin";


    @NonNull
    @Override
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_fingerprint, null, false);

        builder.setTitle(R.string.app_name)
                .setCancelable(true)
                .setView(view)
                .setPositiveButton(R.string.enter,this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditText = view.findViewById(R.id.editText);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        prepareLogin();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPreferences.contains(PIN)) {
            prepareSensor();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFingerprintHelper != null) {
            mFingerprintHelper.cancel();
        }
    }

    private void prepareLogin() {

        final String pin = mEditText.getText().toString();
        if (pin.length() > 0) {
            savePin(pin);
        } else {
            Toast.makeText(getContext(), "pin is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePin(String pin) {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, getContext())) {
            String encoded = CryptoUtils.encode(pin);
            mPreferences.edit().putString(PIN, encoded).apply();
        }
    }

    private void prepareSensor() {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, getContext())) {
            FingerprintManagerCompat.CryptoObject cryptoObject = CryptoUtils.getCryptoObject();
            if (cryptoObject != null) {
            //    Toast.makeText(getContext(), "use fingerprint to login", Toast.LENGTH_LONG).show();
                mFingerprintHelper = new FingerprintHelper(getContext());
                mFingerprintHelper.startAuth(cryptoObject);
            } else {
                mPreferences.edit().remove(PIN).apply();
                Toast.makeText(getContext(), "new fingerprint enrolled. enter pin again", Toast.LENGTH_SHORT).show();
            }

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