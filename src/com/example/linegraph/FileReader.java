package com.example.linegraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

public class FileReader {

	private static final String TAG = "LineGraph";
	private volatile ArrayList<Point> mGraphData = new ArrayList<Point>();
	private boolean mIsFileReadingInProgress = false;
	private IFileReaderListener mFileReaderListener = null;

	interface IFileReaderListener {
		public void onComplete(boolean isSuccess);
	}
	
	public FileReader(Context context) {
		new ReadFile().execute(context.getResources().openRawResource(R.raw.sample_input));
	}

	public void readFromFile(String filePath) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(new File(filePath));
		new ReadFile().execute(inputStream);
	}
	
	public void setFileReaderListener(IFileReaderListener listener) {
		mFileReaderListener = listener;
	}
	
	public boolean isFileReadingComplete() {
		return !mIsFileReadingInProgress;
	}
	
	public ArrayList<Point> getData() {
		if (mIsFileReadingInProgress) {
			return null;
		} else return mGraphData;
	}
	
	private boolean readFile(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter(" |\\n");
		try {
			while (scanner.hasNext()) {
				String pointX = scanner.next();
				Point point = null;
				if (scanner.hasNext()) {
					String pointY = scanner.next();
					point = new Point(Integer.parseInt(pointX), Integer.parseInt(pointY));
					mGraphData.add(point);
				}
			}
		} catch (Exception e) {
			/** Catch all the exceptions */
			e.printStackTrace();
			return false;

		} finally {
			scanner.close();
		}
		return true;
	}

	class ReadFile extends AsyncTask<InputStream, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(TAG, "Reading from the file...");
			mIsFileReadingInProgress = true;
			mGraphData.clear();
		}
		
		@Override
		protected Boolean doInBackground(InputStream... params) {
			return readFile(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) Log.i(TAG, "File read successfully.");
			else Log.i(TAG, "File reading was unsuccesful.");
			mIsFileReadingInProgress = false;
			if (mFileReaderListener != null) {
				mFileReaderListener.onComplete(result);
				mFileReaderListener = null;
			}
		}
	}
}
