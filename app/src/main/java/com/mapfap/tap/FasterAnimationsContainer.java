package com.mapfap.tap;

/**
 * Created by mapfap on 6/16/2017 AD.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;


public class FasterAnimationsContainer {
    private class AnimationFrame {
        private int mResourceId;
        private int mDuration;

        AnimationFrame(int resourceId, int duration) {
            mResourceId = resourceId;
            mDuration = duration;
        }

        public int getResourceId() {
            return mResourceId;
        }

        public int getDuration() {
            return mDuration;
        }
    }

    private ArrayList<AnimationFrame> mAnimationFrames; // list for all frames of animation
    private int mIndex; // index of current frame

    private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
    private boolean mIsRunning; // true if the animation prevents starting the animation twice
    private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
    private Handler mHandler; // Handler to communication with UIThread

    private Bitmap mRecycleBitmap;  //Bitmap can recycle by inBitmap is SDK Version >=11

    // Listeners
    private OnAnimationStoppedListener mOnAnimationStoppedListener;
    private OnAnimationFrameChangedListener mOnAnimationFrameChangedListener;
    private Context context;

    private FasterAnimationsContainer(ImageView imageView) {
        init(imageView);
    }

    // single instance procedures
    private static FasterAnimationsContainer sInstance;

    public static FasterAnimationsContainer getInstance(ImageView imageView) {
        if (sInstance == null)
            sInstance = new FasterAnimationsContainer(imageView);
        sInstance.mRecycleBitmap = null;
        return sInstance;
    }

    /**
     * initialize imageview and frames
     *
     * @param imageView
     */
    public void init(ImageView imageView) {
        mAnimationFrames = new ArrayList<AnimationFrame>();
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);

        mHandler = new Handler();
        if (mIsRunning == true) {
            stop();
        }

        mShouldRun = false;
        mIsRunning = false;

        mIndex = -1;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addAllFrames(int[] resIds, int interval) {
        for (int resId : resIds) {
            mAnimationFrames.add(new AnimationFrame(resId, interval));
        }
    }

    private AnimationFrame getNext() {
        mIndex++;
        if (mIndex >= mAnimationFrames.size())
            mIndex = 0;
        return mAnimationFrames.get(mIndex);
    }

    /**
     * Listener of animation to detect stopped
     */
    public interface OnAnimationStoppedListener {
        void onAnimationStopped();
    }

    /**
     * Listener of animation to get index
     */
    public interface OnAnimationFrameChangedListener {
        void onAnimationFrameChanged(int index);
    }

    /**
     * Starts the animation
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;
        mHandler.post(new FramesSequenceAnimation());
    }

    /**
     * Stops the animation
     */
    public synchronized void stop() {
        mShouldRun = false;
    }

    private class FramesSequenceAnimation implements Runnable {

        @Override
        public void run() {
            ImageView imageView = mSoftReferenceImageView.get();
            if (!mShouldRun || imageView == null) {
                mIsRunning = false;
                if (mOnAnimationStoppedListener != null) {
                    mOnAnimationStoppedListener.onAnimationStopped();
                }
                return;
            }
            mIsRunning = true;

            if (imageView.isShown()) {
                AnimationFrame frame = getNext();
                GetImageDrawableTask task = new GetImageDrawableTask(imageView);
                task.execute(frame.getResourceId());
                // TODO postDelayed after onPostExecute
                mHandler.postDelayed(this, frame.getDuration());
            }
        }
    }

    private class GetImageDrawableTask extends AsyncTask<Integer, Void, Drawable> {

        private ImageView mImageView;

        public GetImageDrawableTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            mRecycleBitmap = BitmapFactory.decodeResource(context.getResources(), params[0], options);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), mRecycleBitmap);
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (result != null) {
                mImageView.setImageDrawable(result);
            }
            if (mOnAnimationFrameChangedListener != null) {
                mOnAnimationFrameChangedListener.onAnimationFrameChanged(mIndex);
            }
        }

    }
}