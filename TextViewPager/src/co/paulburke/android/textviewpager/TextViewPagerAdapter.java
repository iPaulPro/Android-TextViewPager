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
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A {@link PagerAdapter} that creates pages of text based on the available
 * space for each view.
 *
 * @see PagingLayoutListener
 * @author paulburke (ipaulpro)
 */
public class TextViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "TextViewPagerAdapter";
    static final boolean DEBUG = false;

    private final Context mContext;
    private final PagingLayoutListener.OnPageMeasureListener mMeasureListener;

    private int[] mOffsets = new int[] {};

    private CharSequence mText;
    private int mLayoutRes = -1;
    private int mCount = 1;

    LayoutInflater mInflater;

    public TextViewPagerAdapter(Context context, PagingLayoutListener.OnPageMeasureListener listener) {
        this(context, listener, null);
    }

    public TextViewPagerAdapter(Context context,
            PagingLayoutListener.OnPageMeasureListener listener, CharSequence text) {
        mContext = context;
        mMeasureListener = listener;
        mText = text;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public TextView instantiateItem(final ViewGroup container, final int position) {
        TextView view = null;

        if (mLayoutRes > 0)
            view = (TextView) mInflater.inflate(mLayoutRes, container, false);
        else
            view = new TextView(mContext, null, R.attr.textViewPagerStyle);

        if (mText != null) {
            int offset = 0;
            int end = mText.length();
            int size = mOffsets.length;

            if (size == 0) {
                // Add the OnGlobalLayoutListener, which measures the text and
                // reports paged character offsets if the text layout is taller
                // than the container.
                PagingLayoutListener listener = new PagingLayoutListener(view, mMeasureListener);
                view.getViewTreeObserver().addOnGlobalLayoutListener(listener);

            } else {

                offset = mOffsets[position];
                int lastPage = size - 1;

                // Don't consider the last page measured, in case there is more
                // text to be displayed.
                if (position < lastPage)
                    end = mOffsets[position + 1];
            }

            final CharSequence sub = mText.subSequence(offset, end);
            view.setText(sub != null ? sub : mContext.getText(R.string.unable_to_load_text));
            container.addView(view, 0);

            if (DEBUG) Log.d(TAG, "instantiateItem position = " + position + ", offset = " + offset + ", end = " + end + ", text = " + sub);
        }

        return view;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        final View view = (View) object;
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * @return the text that is being displayed.
     */
    public CharSequence getText() {
        return mText;
    }

    /**
     * @param text the text to display.
     */
    public void setText(CharSequence text) {
        if (DEBUG) Log.i(TAG, "setText length = " + text.length());
        mText = text;
        notifyDataSetChanged();
    }

    /**
     * @return the layout resource identifier used to inflate the TextViews.
     */
    public int getTextViewLayout() {
        return mLayoutRes;
    }

    /**
     * @param resId the layout resource identifier to use to inflate the
     *            TextViews.
     */
    public void setTextViewLayout(int resId) {
        mLayoutRes = resId;
        notifyDataSetChanged();
    }

    /**
     * @param offsets array containing the character offsets for each page.
     */
    public void setOffsets(int[] offsets) {
        mOffsets = offsets;
        mCount = mOffsets.length;
        notifyDataSetChanged();
    }

    /**
     * @return array containing the character offsets for each page.
     */
    public int[] getOffsets() {
        return mOffsets;
    }

    /**
     * @param position position in the adapter to get the character offset from.
     * @return the character offset from the current page.
     */
    public int getOffsetForPosition(int position) {
        return mOffsets[position];
    }

}