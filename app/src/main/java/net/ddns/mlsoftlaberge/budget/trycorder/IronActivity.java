package net.ddns.mlsoftlaberge.budget.trycorder;

import android.app.ActionBar;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 31/03/16.
 */
public class IronActivity extends AppCompatActivity {

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
        final LinearLayout.LayoutParams tlayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        mSensorLayout.addView(mSensorView,tlayoutParams);


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
    private class SensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[]=new int[3];
        private float mWidth;
        private float mHeight;
        private float mYOffset;
        private float mScale;
        private float mSpeed=0.5f;

        // table of values for the trace
        private int MAXVALUES = 300;
        private float mValues[] = new float[MAXVALUES * 3];
        private int nbValues=0;



        // initialize the 3 colors, and setup painter
        public SensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            for(int i=0; i<(MAXVALUES * 3); ++i) {
                mValues[i]=0.0f;
            }
            nbValues=0;
        }

        // initialize the bitmap to the size of the view, fill it white
        // init the view state variables to initial values
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(Color.BLACK);
            mYOffset = h * 0.5f;
            mScale = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            mSpeed = mWidth/MAXVALUES;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if(mBitmap!=null) {
                    // clear the surface
                    mCanvas.drawColor(Color.WHITE);
                    // draw middle line horizontal
                    mPaint.setColor(0xffaaaaaa);
                    mCanvas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                    // draw the 100 values x 3 rows
                    for(int i=0; i<nbValues-1;++i) {
                        for(int j=0; j<3;++j) {
                            int k=(j*MAXVALUES)+i;
                            float oldx=i*mSpeed;
                            float newx=(i+1)*mSpeed;
                            mPaint.setColor(mColor[j]);
                            mCanvas.drawLine(oldx, mValues[k], newx, mValues[k+1], mPaint);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap,0,0,null);
                }
            }
            super.onDraw(viewcanvas);
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD) {
                        // scroll left when full
                        if(nbValues>=MAXVALUES) {
                            for (int i = 0; i < (MAXVALUES * 3)-1; ++i) {
                                mValues[i] = mValues[i+1];
                            }
                            nbValues--;
                        }
                        // fill the 3 elements in the table
                        for(int i=0; i<3; ++i) {
                            final float v = mYOffset +event.values[i] * mScale;
                            mValues[nbValues+(i*MAXVALUES)]=v;
                        }
                        nbValues++;
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