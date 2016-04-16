package net.ddns.mlsoftlaberge.budget.trycorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.ddns.mlsoftlaberge.budget.R;
/**
 * Created by mlsoft on 27/03/16.
 */
public class GraphicActivity extends AppCompatActivity {

    private LinearLayout mLayoutView;

    private Button mDessineButton;
    private Button mEffaceButton;
    private Button mCircleButton;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // default layout
        setContentView(R.layout.activity_graphic);

        mLayoutView = (LinearLayout) findViewById(R.id.layout_graphic);

        mBitmap=Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);

        mCanvas=new Canvas(mBitmap);

        mPaint=new Paint();

        // dessine
        mDessineButton = (Button) findViewById(R.id.dessine_button);
        mDessineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dessine();
            }
        });

        // efface
        mEffaceButton = (Button) findViewById(R.id.efface_button);
        mEffaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efface();
            }
        });

        // efface
        mCircleButton = (Button) findViewById(R.id.circle_button);
        mCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circle();
            }
        });

    }

    // =========================================================================================

    public void dessine() {
        mPaint.setColor(Color.parseColor("#CD5C5C"));
        mCanvas.drawRect(100, 100, 50, 50, mPaint);
        mLayoutView.setBackgroundDrawable(new BitmapDrawable(mBitmap));
    }

    public void efface() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mCanvas.drawPaint(mPaint);
        mLayoutView.setBackgroundDrawable(new BitmapDrawable(mBitmap));

    }

    public void circle() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mCanvas.drawCircle(100, 100, 50, mPaint);
        mLayoutView.setBackgroundDrawable(new BitmapDrawable(mBitmap));
    }



}
