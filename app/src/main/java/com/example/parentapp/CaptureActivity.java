package com.example.parentapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class CaptureActivity extends com.journeyapps.barcodescanner.CaptureActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("SCAN_RESULT");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SCAN_RESULT", result);
            setResult(RESULT_OK, resultIntent);
            finish();  // Finish the activity and return to the previous one
        }
    }
}
