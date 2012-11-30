package com.demos.gcm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PushActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        TextView messageView = (TextView) findViewById(R.id.push_message);
        messageView.setText(message);
    }
}