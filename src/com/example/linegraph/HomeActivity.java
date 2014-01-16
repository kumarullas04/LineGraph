package com.example.linegraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.linegraph.FileReader.IFileReaderListener;

public class HomeActivity extends Activity {

	private static final String TAG = "LineGraph";
	private FileReader mFileReader;
	private ProgressDialog mProgresDialog;
	private LineGraphView mLineGraphView;
	private static final int TEXT_FILE_REQUEST_CODE = 1111;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFileReader = new FileReader(this);
		setContentView(R.layout.activity_home);
		mLineGraphView = (LineGraphView) findViewById(R.id.lineGraphView);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mLineGraphView.invalidate();
	}
	
	public ArrayList<Point> getData() {
		
		if (mFileReader.isFileReadingComplete()) {
			return mFileReader.getData();
		} else {
			if (mProgresDialog != null && mProgresDialog.isShowing()) return null;
			initProgressDialog();
			mFileReader.setFileReaderListener(new IFileReaderListener() {
				
				@Override
				public void onComplete(boolean isSuccess) {
					mProgresDialog.dismiss();
					if (isSuccess) {
						mLineGraphView.invalidate();
					} else {
						handleError();
					}
				}
			});
		}
		return null;
	}
	
	private void initProgressDialog() {
		mProgresDialog = new ProgressDialog(this);
		mProgresDialog.setMessage("File Reading is in progress");
		mProgresDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgresDialog.show();
	}
	
	private void handleError() {
		Toast.makeText(this, "Unsupported file format.", Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.import_txt_file) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		    intent.setType("text/plain");
		    intent.addCategory(Intent.CATEGORY_OPENABLE);
		    startActivityForResult(intent, TEXT_FILE_REQUEST_CODE);
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TEXT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			
			String filePath = uri.getPath();
			try {
				mFileReader.readFromFile(filePath);
				mLineGraphView.invalidate();
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "File could not be found!", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

}
