package com.berg.mylegislator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TableRow;

import com.berg.mylegislator.R;

public class GradientedTableRow extends TableRow {
	int clickBackground=R.drawable.republican_list_selector_background;
	Paint graidient;
	int mGraidientPrimaryColorDefault = 0xFFFF0000;
	
	public GradientedTableRow(Context context) {
		super(context);
		init();
    	setBackgroundResource(clickBackground);
    	setPadding(15, 5, 15, 5);
	}

	public GradientedTableRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
    	setPadding(15, 5, 15, 5);
	}
	
	public void setGradientColor (int color) {
			mGraidientPrimaryColorDefault = color;
	}
	
	private void init() {
		graidient = new Paint();
		graidient.setARGB(255, 50, 50, 50); //gray
		graidient.setAntiAlias(true);
	}
	

	@Override
    protected void dispatchDraw(Canvas canvas) {
    	int width = getMeasuredWidth();
    	@SuppressWarnings("unused")
		int height = getMeasuredHeight();
		graidient.setShader(new RadialGradient(width/2, 0, width/2, mGraidientPrimaryColorDefault, 0xFF75,TileMode.CLAMP));
    	canvas.drawLine(2,0, width -1, 0, graidient);
		super.dispatchDraw(canvas);
    }
}
