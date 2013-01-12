package com.nekomeshi312.opencvcameratest;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.nekomeshi312.opencvcameratest.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;

public class MainActivity extends Activity
							implements CvCameraViewListener{


	private static final String LOG_TAG = "MainActivity";
	private CameraBridgeViewBase mOpenCvCameraView;
	private Mat mHsv = null;
	private byte[] mImg = null;
	
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(LOG_TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
       OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		mImg = new byte[width*height*3];
		mHsv = new Mat(height, width, CvType.CV_8UC3);
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mHsv.release();
	}

	@Override
	public Mat onCameraFrame(Mat inputFrame) {
		// TODO Auto-generated method stub
		Imgproc.cvtColor(inputFrame, mHsv, Imgproc.COLOR_RGB2HSV);
		mHsv.get(0, 0, mImg);
		final int height = mHsv.rows();
		final int width = mHsv.cols();
		for(int y = 0;y < height;y++){
			final int yy = y*width;
			for(int x = 0;x < width;x++){
				final int pos = (yy + x)*3;
				mImg[pos] =   (byte) ((mImg[pos] + mSft/3) % 180);
			}
		}
		mHsv.put(0, 0, mImg);
		Imgproc.cvtColor(mHsv, mHsv, Imgproc.COLOR_HSV2RGB);
		return mHsv;
	}
	
	private int mSft = 0;
	private int mXpos = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int x = (int) event.getX();
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mXpos = x;
				break;
			case MotionEvent.ACTION_MOVE:
				mSft += (x - mXpos);
				mXpos = x;
				break;
		}
		return true;
	}

	
}
