package com.example.genesis;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class Main3Activity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private boolean autosearch = true; //TODO: add switching (new Activity, along with menu of bookrefs)
    public static final int PERMISSION_REQUEST_CAMERA = 1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        mScannerView = new ZBarScannerView(this);
        // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view


        // Request permission. This does it asynchronously so we have to wait for onRequestPermissionResult before
        // trying to open the camera.
        if (!haveCameraPermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }

    private boolean haveCameraPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.startCamera();
                } else {
                    finish();
                }
            }
            break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    //@Override
    //   public void onDestroy() {
    //     super.onDestroy();
    //   mScannerView.stopCamera();
    // mScannerView = null;
    // }

    //intended to fire URL opener intents from searches
    public void fire(Intent i) {
        startActivity(i);
    }

    @Override
    public void handleResult(Result rawResult) {

        try {

            BookRef b = new BookRef(rawResult.getContents(), rawResult.getBarcodeFormat(), false, this);
            BookRef.addToList(b);

            //remove; if set to auto-open, immediately call openers before throwing ref out
            // gotta figure out settings activities first
            if (autosearch) {
                b.searchBook();
            }

            mScannerView.resumeCameraPreview(this);

        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "Barcode format not supported; try another book.",
                    Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }


    }

}
