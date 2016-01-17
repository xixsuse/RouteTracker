package de.florianm.android.routetracker.wizard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.florianm.android.routetracker.R;
import de.florianm.android.routetracker.RouteTrackerApplication;

public class WizardActivity extends AppCompatActivity {

    @Bind(R.id.fragment_container)
    protected FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        ButterKnife.bind(this);


        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(null == fragment){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PermissionWizardFragment())
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RouteTrackerApplication.getEventBus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RouteTrackerApplication.getEventBus().unregister(this);
    }
}
