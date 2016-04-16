package net.ddns.mlsoftlaberge.budget.trycorder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.ddns.mlsoftlaberge.budget.R;

public class TrycorderActivity extends AppCompatActivity {

    private Button mCameraButton;
    private Button mSmallcamButton;
    private Button mGraphicButton;
    private Button mCanvasButton;
    private Button mBeepButton;
    private Button mMediaButton;
    private Button mSensorsButton;
    private Button mViewButton;
    private Button mMagneticButton;
    private Button mIronButton;
    private Button mSpeakButton;
    private Button mListenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // default layout
        setContentView(R.layout.trycorder_activity);

        // big cam
        mCameraButton = (Button) findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), LiveCameraActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // small cam
        mSmallcamButton = (Button) findViewById(R.id.smallcam_button);
        mSmallcamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), SmallCameraActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        // graphic on layout
        mGraphicButton = (Button) findViewById(R.id.graphic_button);
        mGraphicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), GraphicActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        // graphic on canvas
        mCanvasButton = (Button) findViewById(R.id.canvas_button);
        mCanvasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), CanvasActivity.class);
                startActivityForResult(intent, 4);
            }
        });

        // graphic on canvas
        mBeepButton = (Button) findViewById(R.id.beep_button);
        mBeepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), BeepActivity.class);
                startActivityForResult(intent, 5);
            }
        });

        // graphic on canvas
        mMediaButton = (Button) findViewById(R.id.media_button);
        mMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), MediaActivity.class);
                startActivityForResult(intent, 6);
            }
        });

        // graphic on canvas
        mSensorsButton = (Button) findViewById(R.id.sensors_button);
        mSensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
                startActivityForResult(intent, 7);
            }
        });

        // graphic on canvas
        mViewButton = (Button) findViewById(R.id.view_button);
        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                startActivityForResult(intent, 8);
            }
        });

        // graphic on draw sensors change
        mMagneticButton = (Button) findViewById(R.id.magnetic_button);
        mMagneticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), MagneticActivity.class);
                startActivityForResult(intent, 9);
            }
        });

        // sensors optimized a la Martin
        mIronButton = (Button) findViewById(R.id.iron_button);
        mIronButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), IronActivity.class);
                startActivityForResult(intent, 10);
            }
        });

        // sensors optimized a la Martin
        mSpeakButton = (Button) findViewById(R.id.speak_button);
        mSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), SpeakActivity.class);
                startActivityForResult(intent, 11);
            }
        });

        // sensors optimized a la Martin
        mListenButton = (Button) findViewById(R.id.listen_button);
        mListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the camera activity
                Intent intent = new Intent(getApplicationContext(), ListenActivity.class);
                startActivityForResult(intent, 12);
            }
        });

    }

    // capture the results of editing activities, and process them
    // save the resulting data when needed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request went well (OK) and the request was EDITMEMO_REQUEST
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            if(data.getStringExtra("GOOD").equals("YES")) {
                // do the job
            } else {
                //finish();
            }
        } else {
            //finish();
        }
    }


}
