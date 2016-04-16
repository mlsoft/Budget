package net.ddns.mlsoftlaberge.budget.trycorder;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.widget.Button;

import net.ddns.mlsoftlaberge.budget.R;

import java.io.IOException;

/**
 * Created by mlsoft on 26/03/16.
 */
public class SmallCameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private TextureView mTextureView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mTextureView = new TextureView(this);
        //mTextureView.setSurfaceTextureListener(this);
        //setContentView(mTextureView);

        // default layout
        setContentView(R.layout.activity_small);

        mTextureView = (TextureView) findViewById(R.id.texture_window);
        mTextureView.setSurfaceTextureListener(this);



    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }
}

