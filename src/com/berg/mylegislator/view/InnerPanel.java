package com.berg.mylegislator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class InnerPanel extends LinearLayout {

	private Paint innerPaint, linePaint;
	private int mGradientColor = 0xFF999999;
    
	public  InnerPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public  InnerPanel(Context context) {
		super(context);
		init();
	}

	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(255, 50, 50, 50); //gray
		innerPaint.setAntiAlias(true);
		linePaint = new Paint();
		linePaint.setARGB(255, 200, 50, 50); //gray
		linePaint.setAntiAlias(true);
	}
	
	public void setGradientColor(int color) {
		this.mGradientColor = color;
	}
	
	public void setBackgroundColor(int color) {
		innerPaint.setColor(color);
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
    	int width = getMeasuredWidth();
		linePaint.setShader(new RadialGradient(width/2, 0, width/2, mGradientColor, 0xFF75,TileMode.CLAMP));
    	RectF drawRect = new RectF();
    	RectF overlap = new RectF();
    	drawRect.set(2,0, width -2, getMeasuredHeight()-2);
    	overlap.set(2,0,width -2,getMeasuredHeight()-20);
    	canvas.drawRect(overlap, innerPaint);
    	canvas.drawRoundRect(drawRect, 18, 18, innerPaint);
    	canvas.drawLine(2,0, width -1, 0, linePaint);

		super.dispatchDraw(canvas);
    }
	
}

