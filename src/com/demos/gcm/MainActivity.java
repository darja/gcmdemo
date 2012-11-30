package com.demos.gcm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    private GCMHelper mSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSession = GCMHelper.getInstance(this);
    }

    @SuppressWarnings("UnusedParameters")
    public void gcmRegister(View view) {
        mSession.register();
    }

    @SuppressWarnings("UnusedParameters")
    public void gcmUnregister(View view) {
        mSession.unregister();
    }
}
