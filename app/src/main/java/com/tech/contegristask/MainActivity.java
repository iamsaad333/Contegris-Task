package com.tech.contegristask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainLog";
    String mUrl = "https://www.webrtc-experiment.com/RecordRTC/";

    WebView myWebView;
    int REQUEST_CODE = 2233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWebView();

    }



    private void setWebView() {
        myWebView = new WebView(this);


        Button btnSwitchCam = new Button(this);
        btnSwitchCam.setText("Change Camera");
        myWebView.addView(btnSwitchCam);
//        btnSwitchCam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mCameraSource.getCameraFacing()==CameraSource.CAMERA_FACING_FRONT){
//                    btnSwitchCam.setText("FRONT CAMERA");
//                    if (mCameraSource != null) {
//                        mCameraSource.release();
//                    }
//                    createCameraSource(CameraSource.CAMERA_FACING_BACK);
//                }
//                else{
//                    btnSwitchCam.setText("BACK CAMERA");
//                    if (mCameraSource != null) {
//                        mCameraSource.release();
//                    }
//                    createCameraSource(CameraSource.CAMERA_FACING_FRONT);
//                }
//
//                startCameraSource();
//            }
//        });



        Button btnMute = new Button(this);
        btnMute.setText("Mute");
        myWebView.addView(btnMute);
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                if (audioManager.isMicrophoneMute() == false) {
                    audioManager.setMicrophoneMute(true);
                    btnMute.setText("UnMute");
                    Toast.makeText(MainActivity.this, "Microphone muted", Toast.LENGTH_SHORT).show();
                } else {
                    audioManager.setMicrophoneMute(false);
                    btnMute.setText("Mute");
                    Toast.makeText(MainActivity.this, "Microphone nmuted", Toast.LENGTH_SHORT).show();

                }
            }
        });


        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        //myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient() {
            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.i(TAG, "onPermissionRequest");
                final String[] requestedResources = request.getResources();
                for (String r : requestedResources) {
                    if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        // In this sample, we only accept video capture request.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Allow Permission to camera")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        request.grant(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE, PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                                        Log.d(TAG, "Granted");
                                    }
                                })
                                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        request.deny();
                                        Log.d(TAG, "Denied");
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        break;
                    }
                }
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });


        if (hasCameraPermission() && hasMicPermission()) {
            myWebView.loadUrl(mUrl);
            setContentView(myWebView);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 2233);
        }

    }

    private boolean hasMicPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED;

    }


    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_CODE) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            myWebView.loadUrl(mUrl);
            setContentView(myWebView);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 2233);
        }

    }


}