package net.ddns.mlsoftlaberge.budget.trycorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 31/03/16.
 */
public class MagneticActivity extends AppCompatActivity {

    // the handle to the sensors
    private SensorManager mSensorManager;

    // the new scope class
    private SensorView mSensorView;

    // the button to start it all
    private Button mStartButton;

    // the layout to put sensorview in
    private LinearLayout mSensorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnetic_activity);

        // the start button
        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startsensors();
            }
        });

        // the sensor layout, to contain my sensorview
        mSensorLayout = (LinearLayout) findViewById(R.id.sensor_layout);

        // a sensor manager to obtain sensors data
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // my sensorview that display the sensors data
        mSensorView = new SensorView(this);

        // add my sensorview to the layout
        mSensorLayout.addView(mSensorView, 400, 200);


    }


    @Override
    protected void onResume() {
        super.onResume();
        // link a sensor to the sensorview
        mSensorManager.registerListener(mSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(mSensorView);
        super.onStop();
    }

    // here we start the sensor reading
    private void startsensors() {

    }

    // ============================================================================
    // class defining the sensor display widget
    private class SensorView extends View implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[]=new int[3];
        private float mWidth;
        private float mHeight;
        private float mLastx;
        private float mMaxx;
        private float mYOffset;
        private float mScale;
        private float mLastValue[]=new float[3];
        private float mSpeed=0.5f;

        // initialize the 3 colors, and setup painter
        public SensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }

        // initialize the bitmap to the size of the view, fill it white
        // init the view state variables to initial values
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(0xffffffff);
            mYOffset = h * 0.5f;
            mScale = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            mMaxx = w;
            mLastx = mMaxx;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas canvas) {
            synchronized (this) {
                if(mBitmap!=null) {
                    final Paint paint = mPaint;
                    if(mLastx >= mMaxx) {
                        mLastx=0;
                        final Canvas cavas = mCanvas;
                        final float yoffset=mYOffset;
                        final float maxx = mMaxx;
                        paint.setColor(0xffaaaaaa);
                        cavas.drawColor(0xffffffff);
                        cavas.drawLine(0,yoffset,maxx,yoffset,paint);
                    }
                    canvas.drawBitmap(mBitmap,0,0,null);
                }
            }
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD) {
                        final Canvas canvas = mCanvas;
                        final Paint paint = mPaint;
                        float deltaX = mSpeed;
                        float newX = mLastx + deltaX;
                        for(int i=0; i<3; ++i) {
                            final float v = mYOffset +event.values[i] * mScale;
                            paint.setColor(mColor[i]);
                            canvas.drawLine(mLastx, mLastValue[i], newX, v, paint);
                            mLastValue[i]=v;
                        }
                        mLastx+=mSpeed;
                        invalidate();
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do
        }

    }


}