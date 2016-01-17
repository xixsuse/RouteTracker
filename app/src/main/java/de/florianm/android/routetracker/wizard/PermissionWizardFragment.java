package de.florianm.android.routetracker.wizard;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.florianm.android.routetracker.MainActivity;
import de.florianm.android.routetracker.R;

public class PermissionWizardFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSION = 0x01;

    @Bind(R.id.nextButton)
    protected Button nextButton;
    private boolean showAppInfoSettings;


    private boolean isLocationPermissionGranted() {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(
                        getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void onWizardFragmentDone() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission_wizard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isLocationPermissionGranted()){
            onWizardFragmentDone();
        }
    }

    @OnClick(R.id.nextButton)
    public void onNextButtonClicked(View view){
        if(showAppInfoSettings){
            startAppInfoSettings();
        } else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
        }
    }

    public void startAppInfoSettings(){
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if(REQUEST_CODE_PERMISSION == requestCode){
            for(int i = 0; i < permissions.length; i++){
                String permission = permissions[i];
                
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)){
                    if(PackageManager.PERMISSION_GRANTED == grantResults[i]){
                        onWizardFragmentDone();
                    } else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), 
                                Manifest.permission.ACCESS_FINE_LOCATION)){
                            nextButton.setText(R.string.preferences);
                            showAppInfoSettings = true;
                        }
                    }
                }
            }
        }
    }
}
