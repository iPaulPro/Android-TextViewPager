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

package co.paulburke.android.textviewpagerexample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.widget.TextView;

import co.paulburke.android.textviewpager.TextViewPager;
import co.paulburke.android.textviewpager.TextViewPager.OnPageCreatedListener;
import co.paulburke.android.textviewpager.TextViewPagerIndicator;

public class StyledVerticalActivity extends Activity {

    private TextView mNumber;
    private TextViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_styled_vertical);

        mNumber = (TextView) findViewById(R.id.page_number);

        mPager = (TextViewPager) findViewById(R.id.pager);
        // We don't know how many pages there are until we've measured
        mPager.setOnPageCreatedListener(new OnPageCreatedListener() {
            @Override
            public void onPageCreated(int count) {
                setPageNumber(mPager.getCurrentItem());
            }
        });

        final TextViewPagerIndicator indicator = (TextViewPagerIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        // If not using an indicator, you'd set this on the TextViewPager itself
        indicator.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setPageNumber(position);
            }
        });
    }

    private void setPageNumber(int position) {
        mNumber.setText(getString(R.string.page_number, position + 1,
                mPager.getAdapter().getCount()));
    }
}
