package com.esrc.face.android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esrc.sdk.android.ESRC;
import com.esrc.sdk.android.ESRCException;
import com.esrc.sdk.android.ESRCFragment;
import com.esrc.sdk.android.ESRCLicense;
import com.esrc.sdk.android.ESRCType;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "MainActivity";
    private static final String APP_ID = "";  // Application ID.

    // Permission
    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {INTERNET, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    // Property
    private ESRCType.Property mProperty = new ESRCType.Property(
            false,  // Whether visualize result or not. It is only valid If you bind the ESRC Fragment (i.e., Step 2).
            true,  // Whether analyze measurement environment or not.
            true,  // Whether detect face or not.
            true,  // Whether detect facial landmark or not. If enableFace is false, it is also automatically set to false.
            true,  // Whether analyze facial action unit or not. If enableFace or enableFacialLandmark is false, it is also automatically set to false.
            true);  // Whether recognize facial expression or not. If enableFace is false, it is also automatically set to false.

    // Layout variables for FaceBox
    private TextView mFaceBoxText;
    private ImageView mFaceBoxImage;
    private GradientDrawable mFaceBoxDrawable;

    // Layout variables for Facial Expression
    private int[] mFacialExpImageDrawables;
    private View mFacialExpValContainer;
    private ImageView mFacialExpImage;
    private TextView mFacialExpValText;

    // Layout variables for Head Pose
    private View mHeadPoseValContainer;
    private TextView mHeadPoseValText;

    // Layout variables for Attention
    private View mAttentionValContainer;
    private TextView mAttentionValText;

    // Dialog variables
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ESRCFragment.newInstance())
                    .commit();
        }

        // Request Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // Initialize layout
        initLayout();

        // Initialize ESRC
        ESRC.init(APP_ID, this, new ESRCLicense.ESRCLicenseHandler() {
            @Override
            public void onValidatedLicense() {
                // Start
                start();
            }

            @Override
            public void onInvalidatedLicense() {
                Toast.makeText(getApplicationContext(), "Invalid license", Toast.LENGTH_SHORT).show();
            }
        });

        // Timer
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Show alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("If you want to use the ESRC SDK, please visit the homepage: https://www.esrc.co.kr");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                // Close activity
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }, 5000);
            }
        }, 120000);
    }

    @Override
    protected void onDestroy() {
        // Stop
        stop();

        super.onDestroy();
    }

    /**
     * Initialize layout.
     */
    private void initLayout() {
        // Initialize layout for FaceBox
        mFaceBoxText = findViewById(R.id.facebox_text);
        mFaceBoxImage = findViewById(R.id.facebox_image);
        mFaceBoxDrawable = (GradientDrawable) mFaceBoxImage.getBackground();

        // Initialize layout for Facial Expression
        mFacialExpImageDrawables = new int[] {
                R.drawable.ic_facial_exp_anger, R.drawable.ic_facial_exp_disgust, R.drawable.ic_facial_exp_fear,
                R.drawable.ic_facial_exp_happy, R.drawable.ic_facial_exp_sad, R.drawable.ic_facial_exp_surprise,
                R.drawable.ic_facial_exp_neutral
        };
        mFacialExpValContainer = findViewById(R.id.facial_expression_val_container);
        mFacialExpImage = findViewById(R.id.facial_expression_image);
        mFacialExpValText = findViewById(R.id.facial_expression_val_text);

        // Initialize layout for Head Pose
        mHeadPoseValContainer = findViewById(R.id.head_pose_val_container);
        mHeadPoseValText = findViewById(R.id.head_pose_val_text);

        // Initialize layout for Attention
        mAttentionValContainer = findViewById(R.id.attention_val_container);
        mAttentionValText = findViewById(R.id.attention_val_text);
    }

    /**
     * Starts ESRC process.
     */
    private void start() {
        // Start ESRC
        ESRC.start(mProperty, new ESRC.ESRCHandler() {
            @Override
            public void onAnalyzedMeasureEnv(ESRCType.MeasureEnv measureEnv, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onAnalyzedMeasureEnv: " + measureEnv.toString());
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDetectedFace(ESRCType.Face face, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onDetectedFace: " + face.toString());

                    // Show FaceBox
                    int color = getResources().getColor(R.color.primary_color);
                    mFaceBoxText.setTextColor(color);
                    mFaceBoxDrawable.setStroke(8, color);

                    // Show Head Pose and Attention containers (debug)
                    mHeadPoseValContainer.setVisibility(View.VISIBLE);
                    mAttentionValContainer.setVisibility(View.VISIBLE);
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNotDetectedFace(ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onNotDetectedFace");

                    // Hide FaceBox
                    int color = getResources().getColor(R.color.gray);
                    mFaceBoxText.setTextColor(color);
                    mFaceBoxDrawable.setStroke(4, color);

                    // Hide containers
                    mFacialExpValContainer.setVisibility(View.GONE);
                    mHeadPoseValContainer.setVisibility(View.GONE);
                    mAttentionValContainer.setVisibility(View.GONE);
                } else {
                    e.printStackTrace();
                }

            }

            @Override
            public void onDetectedFacialLandmark(ESRCType.FacialLandmark facialLandmark, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onDetectedFacialLandmark: " + facialLandmark.toString());
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnalyzedFacialActionUnit(ESRCType.FacialActionUnit facialActionUnit, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onAnalyzedFacialActionUnit: " + facialActionUnit.toString());
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecognizedFacialExpression(ESRCType.FacialExpression facialExpression, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onRecognizedFacialExpression: " + facialExpression.toString());

                    if (e == null) {
                        Log.d(TAG, "onRecognizedFacialExpression: " + facialExpression.toString());

                        // Set Facial Expression values
                        mFacialExpImage.setImageResource(mFacialExpImageDrawables[facialExpression.getEmotion()]);
                        mFacialExpValText.setText(facialExpression.getEmotionStr());

                        // Show container for Facial Expression
                        mFacialExpValContainer.setVisibility(View.VISIBLE);
                    } else {
                        e.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEstimatedHeadPose(ESRCType.HeadPose headPose, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onEstimatedHeadPose: " + headPose.toString());

                    // Set Head Pose values
                    mHeadPoseValText.setText(
                            Long.toString(Math.round(headPose.getThetaX())) + ", " +
                                    Long.toString(Math.round(headPose.getThetaY())) + ", " +
                                    Long.toString(Math.round(headPose.getThetaZ())));

                    // Show container for Head Pose
                    mHeadPoseValContainer.setVisibility(View.VISIBLE);
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecognizedAttention(ESRCType.Attention attention, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onRecognizedAttention: " + attention.toString());

                    // Set Attention values
                    if(attention.getIsAttend()) {
                        mAttentionValText.setText("O");
                    } else {
                        mAttentionValText.setText("X");
                    }

                    // Show container for Attention
                    mAttentionValContainer.setVisibility(View.VISIBLE);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Stop ESRC process.
     */
    private void stop() {
        // Stop ESRC
        ESRC.stop();
    }

    /**
     * Check granted permissions.
     */
    private boolean hasPermissions(String[] permissions) {
        int result;

        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Request permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("If you reject permission, you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]");
                }
                break;
        }
    }

    /**
     * Show dialog for permission.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(com.esrc.face.android.MainActivity.this);
        builder.setTitle("Permission");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}