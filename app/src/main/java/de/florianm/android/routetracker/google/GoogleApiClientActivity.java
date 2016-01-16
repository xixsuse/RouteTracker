package de.florianm.android.routetracker.google;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

public abstract class GoogleApiClientActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String TAG_ERROR_DIALOG = "google_client_error_dialog";
    private static final String STATE_RESOLVING_ERROR = "state_resolving_error";

    private GoogleApiClient googleApiClient;
    private boolean resolvingError;

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getApplicationContext());
        builder.addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);
        googleApiClient = createGoogleApiClient(builder);

        if (null != savedInstanceState) {
            resolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR);
        }
    }

    @NonNull
    protected abstract GoogleApiClient createGoogleApiClient(GoogleApiClient.Builder builder);

    @Override
    protected void onStart() {
        super.onStart();

        if (!resolvingError) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, resolvingError);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (resolvingError) {
            return;
        }

        if (connectionResult.hasResolution()) {
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            resolvingError = true;
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ErrorDialogFragment.ARG_ERROR_CODE, errorCode);
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), TAG_ERROR_DIALOG);
    }

    private void onDialogDismissed() {
        resolvingError = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_RESOLVE_ERROR == requestCode) {
            resolvingError = false;
            if (RESULT_OK == resultCode) {
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    public class ErrorDialogFragment extends DialogFragment {
        public static final String ARG_ERROR_CODE = "arg_error_code";

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = getArguments().getInt(ARG_ERROR_CODE);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    getActivity(),
                    errorCode,
                    REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            ((GoogleApiClientActivity) getActivity()).onDialogDismissed();
        }
    }
}
