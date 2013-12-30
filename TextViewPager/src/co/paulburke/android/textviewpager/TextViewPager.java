/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.paulburke.android.textviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.DirectionalViewPager;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * A ViewPager that pages the provided text based on the available space. The
 * {@link PagerAdapter} is automatically created, as well as the necessary
 * {@link TextView}s. You can specify the style to be used by overriding the
 * {@link R.attr#textViewPagerStyle} in the Activity Theme, or by specifying a
 * layout to be used with {@link #setTextViewLayout(int)};
 *
 * @see TextViewPagerAdapter
 * @see #setText(CharSequence)
 * @see #setText(int)
 * @author paulburke (ipaulpro)
 */
public class TextViewPager extends DirectionalViewPager {

    private static final String TAG = "TextViewPager";
    private static final boolean DEBUG = false;

    private static final String INSTANCE_STATE = "instanceState";
    private static final String STATE_OFFSET = "stateToSave";

    /**
     * Listener to be notified of when the adapter has measured and created the
     * necessary pages.
     */
    public interface OnPageCreatedListener {
        /**
         * Called when the adapter has measured and created the necessary pages.
         * Calls to {@link PagerAdapter#getCount()} will not be correct until
         * after this fires.
         *
         * @param count the total number of pages created.
         */
        public void onPageCreated(int count);
    }

    private final PagingLayoutListener.OnPageMeasureListener mMeasureListener = new PagingLayoutListener.OnPageMeasureListener() {
        @Override
        public void onPageMeasure(int[] offsets, int totalLines, int linesPerPage) {
            if (DEBUG) Log.i(TAG, "onMeasure pages count = " + offsets.length+", totalLines = "+totalLines+", linesPerPage = "+linesPerPage);

            if (mPagerAdapter != null) {
                mPagerAdapter.setOffsets(offsets);
            }

            if (mRestoredOffset > 0) {
                // Find the restored offset page
                for (int i = 0; i < offsets.length; i++) {
                    if (mRestoredOffset < offsets[i]) {
                        setCurrentItem(i - 1, false);
                        break;
                    } else if (i == offsets.length - 1) {
                        setCurrentItem(i, false);
                    }
                }
            }

            // Let the listener know that new pages were created
            if (mPageCreatedListener != null)
                mPageCreatedListener.onPageCreated(offsets.length);
        }
    };

    private TextViewPagerAdapter mPagerAdapter;
    private OnPageCreatedListener mPageCreatedListener;

    private int mRestoredOffset;

    public TextViewPager(Context context) {
        this(context, null);
    }

    public TextViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.textViewPagerStyle);
    }

    public TextViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        mPagerAdapter = new TextViewPagerAdapter(context, mMeasureListener);

        if (attrs != null) {
            TypedArray attributes = context
                    .obtainStyledAttributes(attrs, R.styleable.TextViewPager);

            int layout = attributes.getResourceId(R.styleable.TextViewPager_textViewLayout, -1);
            CharSequence text = attributes.getText(R.styleable.TextViewPager_android_text);

            mPagerAdapter.setTextViewLayout(layout);
            mPagerAdapter.setText(text);

            attributes.recycle();
        }

        setAdapter(mPagerAdapter);
    }

    /**
     * @param text the text to be paged.
     * @attr {@link R.styleable#TextViewPager_android_text}
     */
    public void setText(CharSequence text) {
        mPagerAdapter.setText(text);
    }

    /**
     * @param resId the resource identifier of the text to be paged.
     * @attr {@link R.styleable#TextViewPager_android_text}
     */
    public void setText(int resId) {
        mPagerAdapter.setText(getContext().getResources().getText(resId));
    }

    /**
     * @param resId the resource identifier of the layout to use for the
     *            TextView
     * @attr {@link R.styleable#TextViewPager_textViewLayout}
     */
    public void setTextViewLayout(int resId) {
        mPagerAdapter.setTextViewLayout(resId);
    }

    /**
     * @return the current {@link OnPageCreatedListener}.
     */
    public OnPageCreatedListener getOnPageCreatedListener() {
        return mPageCreatedListener;
    }

    /**
     * @param mPageCreatedListener the listener to be notified when the pages
     *            are created.
     */
    public void setOnPageCreatedListener(OnPageCreatedListener mPageCreatedListener) {
        this.mPageCreatedListener = mPageCreatedListener;
    }

    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());

        // Keep tabs of the current page offset, since the number of lines will
        // change with configurations
        int offset = mPagerAdapter.getOffsetForPosition(getCurrentItem());
        bundle.putInt(STATE_OFFSET, offset);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable(INSTANCE_STATE);
            mRestoredOffset = bundle.getInt(STATE_OFFSET);
        }

        super.onRestoreInstanceState(state);
    }
}
