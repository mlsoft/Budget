package net.ddns.mlsoftlaberge.budget.trycorder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class ViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new MyView(this));
        setContentView(new ScopeView(this));
    }

    private class ScopeView extends View {

        public ScopeView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        private float values[]={0.5f,-0.5f,0.25f,};
        private int nbvalues=3;

        public void addvalue(float f) {
            values[nbvalues]=f;
            if(nbvalues>100) {
                for(int i=0;i<100;++i) values[i]=values[i+1];
                nbvalues--;
            }
            invalidate();
        }

        public void clearvalues() {
            nbvalues=0;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            float fx = x;
            float fy=y;
            float middle=fy/2;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.RED);
            canvas.drawLine(0,fy/2,fx-1,fy/2,paint);
            float lasty=middle;
            float newy;
            for(int i=0;i<nbvalues;++i) {
                newy=middle+(values[i]*middle);
                canvas.drawLine((float) i*fx/100,lasty,(float)(i+1)*fx/100,newy,paint);
                lasty=newy;
            }

        }


    }

}