package com.example.linegraph;

import java.util.ArrayList;

import org.apache.http.client.CircularRedirectException;

import com.example.linegraph.FileReader.IFileReaderListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LineGraphView extends View {

	private static final String TAG = "LineGraph";
	private Path mPath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mGridPaint = new Paint();
	private Paint mAxisPaint = new Paint();
	private Paint mTextPaint = new Paint();
	private Paint mPointPaint = new Paint();
	private static final int NUM_OF_UNITS = 20;

	private int mMaxX = 0;
	private int mMaxY = 0;
	private int mMinX = Integer.MAX_VALUE;
	private int mMinY = Integer.MAX_VALUE;

	private int mHeight, mWidth;
	private double mUnitXValue, mUnitYValue;
	private int mXOffset = 50;
	private int mYOffset = 50;

	public LineGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LineGraphView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mLinePaint.setColor(Color.GRAY);
		mLinePaint.setStrokeWidth(3);
		mLinePaint.setStrokeCap(Cap.ROUND);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setAntiAlias(true);
		
		mPointPaint.setColor(Color.BLACK);
		mPointPaint.setStrokeWidth(3);
		mPointPaint.setStyle(Style.FILL);
		mPointPaint.setAntiAlias(true);

		mGridPaint.setColor(Color.GREEN);
		mGridPaint.setStrokeWidth(1);
		mGridPaint.setStrokeCap(Cap.ROUND);
		mGridPaint.setStyle(Style.STROKE); 
		mGridPaint.setAntiAlias(true);

		mAxisPaint.setColor(Color.BLACK);
		mAxisPaint.setStrokeWidth(3);
		mAxisPaint.setStrokeCap(Cap.ROUND);
		mAxisPaint.setStyle(Style.STROKE);
		mAxisPaint.setAntiAlias(true);

		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(12);
		mTextPaint.setAntiAlias(true);

	}

	public void initPath() {
		mHeight = getHeight() - mYOffset;
		mWidth = getWidth() - mXOffset ;

		ArrayList<Point> dataList = ((HomeActivity) getContext()).getData();
		if (dataList != null) {
			getMaxCoordValues(dataList);
			calculateUnitValues();

			mPath.reset();
			mPath.moveTo(mXOffset, mHeight);
			double x = 0, y = 0;
			for (Point point : dataList) {
				x = (mWidth / NUM_OF_UNITS) / mUnitXValue;
				y = (mHeight / NUM_OF_UNITS) / mUnitYValue;
				mPath.lineTo((int) (mXOffset + (point.x * x)), (int)(mHeight - (point.y * y)));
			}
		}
	}

	private void getMaxCoordValues(ArrayList<Point> dataList) {
		for (Point point : dataList) {
			if (point.x > mMaxX) mMaxX = point.x;
			if (point.y > mMaxY) mMaxY = point.y;

			if (point.x < mMinX) mMinX = point.x;
			if (point.y < mMinY) mMinY = point.y;
		}
	}

	private void calculateUnitValues() {
		mUnitXValue = mMaxX / (double) NUM_OF_UNITS;
		mUnitYValue = mMaxY / (double)  NUM_OF_UNITS;

		mUnitXValue = getMaxPossibleUnitValue(mUnitXValue);
		mUnitYValue = getMaxPossibleUnitValue(mUnitYValue);
	}

	private double getMaxPossibleUnitValue (double value) {
		return Math.ceil(value * 2) / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		initPath();
		canvas.save();
		canvas.drawColor(Color.WHITE);

		drawGrid(canvas);
		drawAxces(canvas);
		drawGraph(canvas);

		canvas.restore();
		super.onDraw(canvas);
	}
	
	private void drawGrid(Canvas canvas) {
		int x = mXOffset;
		int y = 0;
		int pixelCountPerUnitX =  mWidth / NUM_OF_UNITS;
		int pixelCountPerUnitY =  mHeight / NUM_OF_UNITS;

		double textAlongX = 0;
		double textAlongY = 0;

		for (int count = 0; count <= NUM_OF_UNITS; count++) {

			/** Draw vertical lines */
			canvas.drawLine(x, mHeight, x, 0, mGridPaint);
			/** Draw horizontal lines */
			canvas.drawLine(mXOffset, mHeight - y, mWidth + mXOffset, mHeight - y, mGridPaint);

			canvas.save();
			canvas.rotate(90, x - 5, mHeight + 20);
			
			/** Draw text along X axis */
			canvas.drawText(textAlongX + "", x - 5, mHeight + 20, mTextPaint);
			canvas.restore();

			float textOffset = (mGridPaint.ascent() + mGridPaint.descent()) /2;
			/** Draw text along Y axis */
			canvas.drawText(textAlongY + "", mXOffset - 20 + textOffset, mHeight - y + 5, mTextPaint);
			
			textAlongX += mUnitXValue;
			textAlongY += mUnitYValue;

			x += pixelCountPerUnitX;
			y += pixelCountPerUnitY;
		}
	}

	private void drawAxces(Canvas canvas) {
		canvas.drawLine(mXOffset, mHeight, mWidth + mXOffset, mHeight , mAxisPaint);
		canvas.drawLine(mXOffset, mHeight, mXOffset, 0, mAxisPaint);
	}

	private void drawGraph(Canvas canvas) {
		canvas.drawPath(mPath, mLinePaint);
		drawPoints(canvas);
	}
	
	private void drawPoints(Canvas canvas) {
		double x = (mWidth / NUM_OF_UNITS) / mUnitXValue;
		double y = (mHeight / NUM_OF_UNITS) / mUnitYValue;
		
		ArrayList<Point> dataList = ((HomeActivity) getContext()).getData();
		if (dataList != null) {
			for (Point point : dataList) {
				canvas.drawCircle((int) (mXOffset + (point.x * x)), 
						(int) (mHeight - (point.y * y)), 3, mPointPaint);
			}
		}
	}

	
}
