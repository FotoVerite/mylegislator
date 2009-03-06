package com.berg.mylegislator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class Panel extends LinearLayout {

	private Paint innerPaint, borderPaint ;
    
	public  Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public  Panel(Context context) {
		super(context);
		init();
	}

	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(255, 75, 75, 75); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 200, 200, 200);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(3);
	}
	
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	public void setBorderPaint(int aColor) {
		this.borderPaint.setColor(aColor);
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 20, 20, innerPaint);
		canvas.drawRoundRect(drawRect, 20, 20, borderPaint);
		
		super.dispatchDraw(canvas);
    }
	
}

