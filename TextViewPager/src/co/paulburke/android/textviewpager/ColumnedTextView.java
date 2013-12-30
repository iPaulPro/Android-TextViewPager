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
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Experimental layout that resembles newspaper columns.
 *
 * @author paulburke (ipaulpro)
 */
public class ColumnedTextView extends LinearLayout {

    protected static final String TAG = "ColumnedTextView";

    private static final String XML_NS = "http://schemas.android.com/apk/res/android";

    public interface OnColumnMeasureListener {
        public void onColumnMeasure(int[] offsets, int totalColumns, int columnsPerPage);
    }

    private static int DEFAULT_COLUMN_COUNT = 2;
    private CharSequence mText;
    private int mNumColumns = DEFAULT_COLUMN_COUNT;
    private OnColumnMeasureListener mListener;

    private PagingLayoutListener.OnPageMeasureListener mOnMeasureListener = new PagingLayoutListener.OnPageMeasureListener() {
        @Override
        public void onPageMeasure(int[] offsets, int totalLines, int linesPerPage) {
            Log.i(TAG, "onPageMeasure offsets count = " + offsets.length + ", totalLines = "
                    + totalLines + ", linesPerPage = " + linesPerPage);
            fillViews(offsets);
        }
    };

    public ColumnedTextView(Context context) {
        this(context, null);
    }

    public ColumnedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        if (attrs != null) {
            mNumColumns = attrs.getAttributeIntValue(XML_NS, "numColumns", DEFAULT_COLUMN_COUNT);
            mText = attrs.getAttributeValue(XML_NS, "text");
        }

        if (mText != null) {
            setText(mText);
        }
    }

    public void setText(CharSequence text) {
        addColumns();

        mText = text;
        TextView textView = (TextView) getChildAt(0);
        textView.setText(mText);
    }

    public void setText(int resId) {
        setText(getContext().getText(resId));
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        if (mText != null)
            setText(mText);
    }

    public OnColumnMeasureListener getOnColumnMeasureListener() {
        return mListener;
    }

    public void setOnColumnMeasureListener(OnColumnMeasureListener listener) {
        mListener = listener;
    }

    private void addColumns() {
        removeAllViews();

        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(48, 48, 48, 48);

        TextView textView = new TextView(getContext(), null, R.attr.textViewPagerStyle);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(
                new PagingLayoutListener(textView, mOnMeasureListener));
        addView(textView, 0, layoutParams);

        for (int i = 1; i < mNumColumns; i++) {
            addView(new TextView(getContext(), null, R.attr.textViewPagerStyle), i, layoutParams);
        }
    }

    private void fillViews(int[] offsets) {
        int childCount = getChildCount();
        Log.i(TAG, "onMeasure childCount = " + childCount);
        for (int i = 0; i < offsets.length && i < childCount; i++) {
            TextView child = (TextView) getChildAt(i);
            int start = offsets[i];
            int end = (i < offsets.length - 1) ? offsets[i + 1] : mText.length();
            Log.i(TAG, "onMeasure child " + i + " start = " + start + " end  =" + end);
            String text = TextViewUtils.getJustifiedText(mText.subSequence(start, end), child);
            child.setText(text);
        }
    }

}
