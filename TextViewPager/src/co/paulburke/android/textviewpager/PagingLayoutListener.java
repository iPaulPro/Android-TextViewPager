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

import android.text.Layout;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * An {@link OnGlobalLayoutListener} that determines how many "pages" the
 * current text should be split into by calculating the maximum number of lines
 * that the view can display without clipping.<br>
 * <br>
 * Set an {@link OnPageMeasureListener} to be notified of the character offsets
 * calculated for each page.
 *
 * @author paulburke (ipaulpro)
 */
public class PagingLayoutListener implements OnGlobalLayoutListener {

    private static final String TAG = "PagingLayoutListener";
    private static final boolean DEBUG = true;

    /**
     * Listener used to notify of a completed layout measurement.
     */
    public interface OnPageMeasureListener {
        /**
         * Called when the character offsets have been determined.
         *
         * @param offsets character offsets for the current text.
         * @param totalLines total number of lines in the layout.
         * @param linesPerPage number of lines that fit on this view without
         *            clipping.
         */
        public void onPageMeasure(int[] offsets, int totalLines, int linesPerPage);
    }

    private TextView mView;
    private OnPageMeasureListener mListener;

    /**
     * Creates a new PreDrawListener for the given view.
     *
     * @param view the view to intercept drawing.
     * @param listener the {@link OnPageMeasureListener} to listen for offset
     *            calculations.
     */
    public PagingLayoutListener(TextView view, OnPageMeasureListener listener) {
        mView = view;
        mListener = listener;
    }

    @Override
    public void onGlobalLayout() {
        final Layout layout = mView.getLayout();
        if (layout != null) {
            final int height = mView.getHeight() - mView.getPaddingTop()
                    - mView.getPaddingBottom();
            final int lineCount = layout.getLineCount();

            // Last visible line
            int lastLine = layout.getLineForVertical(height);
            // Bottom of the last visible line
            int lastLineBottom = layout.getLineBottom(lastLine);

            // Check if the layout is taller than the page
            if (lineCount > 0 && lastLineBottom > height) {
                // Determine the number of pages needed
                int pagesCount = (int) Math.ceil(lineCount / (double) lastLine);
                if (DEBUG) Log.d(TAG, "onPreDraw text is too tall! Should be "+pagesCount+" pages.");

                // Determine offsets for each page
                int[] offsets = new int[pagesCount];
                for (int i = 0; i < pagesCount; i++) {
                    offsets[i] = layout.getLineStart(i * lastLine);
                    if (DEBUG) Log.d(TAG, "onPreDraw new page at " + i + ", starting char offset = " + offsets[i]);
                }

                // Clip the text in this view now
                CharSequence text = layout.getText().subSequence(0, offsets[1]);
                mView.setText(text);

                if (mListener != null)
                    mListener.onPageMeasure(offsets, lineCount, lastLine);
            }
        }
    }

}
