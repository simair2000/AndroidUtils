package com.simair.android.androidutils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

public class ZoomableImageView extends ImageView {
    private static final String TAG = ZoomableImageView.class.getSimpleName();
    private final Context context;

    private float width;
    private float height;
    private GestureDetector mGestureDetector = null;

    private static final int MODE_NONE = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    private static final int MODE_WAIT = 3; // second finger up
    private int mMode = MODE_NONE;
    private int mBackupMode = MODE_NONE;
    private PointF mStart = new PointF();

    private Bitmap mImageBitmap = null;
    private PointF mInitPos = new PointF();
    private Matrix mSavedMatrix = new Matrix();
    private float mScaleInit = 1.0f;
    private float mOldDist;

    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = 1.0f;

    private GestureDetector.OnDoubleTapListener mDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.w(TAG, "onDoubleTap...");
            Matrix mat = getImageMatrix();
            Log.e(TAG, "MAT:" + mat.toString());
            float[] matVal = new float[9];
            mat.getValues(matVal);

            Bitmap bitmap = null;
            Drawable drawable = getDrawable();
            if(drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if(drawable instanceof GlideBitmapDrawable) {
                bitmap = ((GlideBitmapDrawable)drawable).getBitmap();
            }

            float scaleInit = Math.min((float) width / (float) bitmap.getWidth(),
                    (float) height / (float) bitmap.getHeight());
            if (matVal[0] < scaleInit * 2)
                mat.setScale(scaleInit * 2, scaleInit * 2, e.getX(), e.getY());
            else
                mat.setScale(scaleInit, scaleInit, e.getX(), e.getY());

            mat.getValues(matVal);
            PointF initPos = new PointF(Math.max(0,
                    (width - (float) bitmap.getWidth() * matVal[0]) / 2), Math.max(0,
                    (height - (float) bitmap.getHeight() * matVal[4]) / 2));
            Log.e(TAG, "DoubleTab : mInitPos=(" + initPos.x + "," + initPos.y + ")");

            if (matVal[2] > initPos.x)
                matVal[2] = initPos.x;
            if (matVal[5] > initPos.y)
                matVal[5] = initPos.y;
            if (matVal[2] + bitmap.getWidth() * matVal[0] < width - initPos.x)
                matVal[2] = width - initPos.x - bitmap.getWidth() * matVal[0];
            if (matVal[5] + bitmap.getHeight() * matVal[4] < height - initPos.y)
                matVal[5] = height - initPos.y - bitmap.getHeight() * matVal[4];
            mat.setValues(matVal);
            Log.e(TAG, "MAT:" + mat.toString());
            setImageMatrix(mat);
            invalidate();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };


    public ZoomableImageView(Context context) {
        this(context, null, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(mDoubleTapListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean ret;
        Log.e(TAG, "onTouch........." + event.toString());
        ret = mGestureDetector.onTouchEvent(event);
        Log.e(TAG, "mGestureDetector.onTouchEvent return " + ret);
        if (ret)
            return false;
        int action = (event.getAction() & MotionEvent.ACTION_MASK);
        switch (action) {
            case MotionEvent.ACTION_DOWN: // first finger down only
                mMode = MODE_DRAG;
                mStart.set(event.getX(), event.getY());

                try {

                    Drawable drawable = getDrawable();
                    if(drawable instanceof BitmapDrawable) {
                        mImageBitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else if(drawable instanceof GlideBitmapDrawable) {
                        mImageBitmap = ((GlideBitmapDrawable)drawable).getBitmap();
                    }

                    //((BitmapDrawable)mImageView.getDrawable()).getBitmap().recycle();

                    if (getScaleType() == ImageView.ScaleType.FIT_CENTER) {
                        Log.v(TAG, "mImageView ImageView.ScaleType.FIT_CENTER=>ImageView.ScaleType.MATRIX");
                        setScaleType(ImageView.ScaleType.MATRIX);
                    }
                    // mSavedMatrix = mImageView.getImageMatrix(); // only
                    // reference
                    mSavedMatrix.set(getImageMatrix()); // copy
                    mScaleInit = Math.min((float) width / (float) mImageBitmap.getWidth(),
                            (float) height / (float) mImageBitmap.getHeight());

                    float[] matVal = new float[9];
                    mSavedMatrix.getValues(matVal);
                    mInitPos.set(Math.max(0, (width - (float) mImageBitmap.getWidth() * matVal[0]) / 2),
                            Math.max(0, (height - (float) mImageBitmap.getHeight() * matVal[4]) / 2));

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                try {
                    Matrix mat = getImageMatrix();
                    float[] matVal = new float[9];
                    mat.getValues(matVal);
                    boolean enabled = true;
                    if (matVal[0] > mScaleInit) {
                        enabled = false;
                    } else {
                        enabled = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case MotionEvent.ACTION_UP: // first finger lifted
                if (mMode == MODE_DRAG) {
                    Log.v(TAG, "MotionEvent.ACTION_UP mMode MODE_DRAG");
                }
            case MotionEvent.ACTION_CANCEL:
                mMode = MODE_NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP: // second finger lifted
                if (mMode != MODE_NONE) {
                    mBackupMode = mMode;
                    mMode = MODE_WAIT;
                } else {
                    mMode = MODE_NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger down
                if (mMode != MODE_WAIT) {
                    mOldDist = spacing(event);
                    Log.w(TAG, "ACTION_POINTER_DOWN : dist=" + mOldDist);
                    if (mOldDist > 5f) {
                        mMode = MODE_ZOOM;
                    }
                } else {
                    mMode = mBackupMode;
                    mBackupMode = MODE_NONE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (mMode) {
                    case MODE_DRAG: {
                        Matrix matrix = new Matrix(mSavedMatrix);
                        matrix.postTranslate(event.getX() - mStart.x, event.getY() - mStart.y);
                        float[] matVal = new float[9];
                        matrix.getValues(matVal);

                        // Log.e(LOG_TAG,"DRAG : mInitPos=("+mInitPos.x+","+mInitPos.y+")");
                        // Log.w(LOG_TAG,"DRAG "+matrix.toString());
                        if (matVal[2] > mInitPos.x)
                            matVal[2] = mInitPos.x;
                        if (matVal[5] > mInitPos.y)
                            matVal[5] = mInitPos.y;
                        if (matVal[2] + mImageBitmap.getWidth() * matVal[0] < width - mInitPos.x)
                            matVal[2] = width - mInitPos.x - mImageBitmap.getWidth() * matVal[0];
                        if (matVal[5] + mImageBitmap.getHeight() * matVal[4] < height - mInitPos.y)
                            matVal[5] = height - mInitPos.y - mImageBitmap.getHeight() * matVal[4];
                        matrix.setValues(matVal);

                        setImageMatrix(matrix);
                        Log.w(TAG, "DRAG " + matrix.toString());
                        break;
                    }
                    case MODE_ZOOM: {
                        Matrix matrix = new Matrix(mSavedMatrix);
                        float newDist = spacing(event);
                        float scale = newDist / mOldDist;
                        float[] matVal = new float[9];
                        matrix.getValues(matVal);
                        if (matVal[0] * scale > MAX_ZOOM * mScaleInit)
                            scale = MAX_ZOOM * mScaleInit / matVal[0];
                        else if (matVal[0] * scale < MIN_ZOOM * mScaleInit)
                            scale = MIN_ZOOM * mScaleInit / matVal[0];
                        matrix.postScale(scale, scale, mStart.x, mStart.y);
					/*
					 * float[] matVal = new float[9]; matrix.getValues(matVal); if (matVal[0] > MAX_ZOOM*mScaleInit) {
					 * matVal[0] = MAX_ZOOM*mScaleInit; matVal[4] = MAX_ZOOM*mScaleInit; } else if (matVal[0] <
					 * MIN_ZOOM*mScaleInit) { matVal[0] = MIN_ZOOM*mScaleInit; matVal[4] = MIN_ZOOM*mScaleInit; }
					 */

                        matrix.getValues(matVal);
                        mInitPos.set(Math.max(0, (width - (float) mImageBitmap.getWidth() * matVal[0]) / 2),
                                Math.max(0, (height - (float) mImageBitmap.getHeight() * matVal[4]) / 2));
                        // Log.e(LOG_TAG,"ZOOM : mInitPos=("+mInitPos.x+","+mInitPos.y+")");

                        if (matVal[2] > mInitPos.x)
                            matVal[2] = mInitPos.x;
                        if (matVal[5] > mInitPos.y)
                            matVal[5] = mInitPos.y;
                        if (matVal[2] + mImageBitmap.getWidth() * matVal[0] < width - mInitPos.x)
                            matVal[2] = width - mInitPos.x - mImageBitmap.getWidth() * matVal[0];
                        if (matVal[5] + mImageBitmap.getHeight() * matVal[4] < height - mInitPos.y)
                            matVal[5] = height - mInitPos.y - mImageBitmap.getHeight() * matVal[4];
                        matrix.setValues(matVal);

                        Log.w(TAG, "ZOOM " + matrix.toString());
                        setImageMatrix(matrix);
                    }
                    break;
                    default:
                        break;
                }

        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
