
package co.paulburke.android.textviewpagerexample;

import android.app.Activity;
import android.os.Bundle;

import co.paulburke.android.textviewpager.TextViewPager;

public class BasicHorizontalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextViewPager pager = new TextViewPager(this);
        // Needed for restoring state
        pager.setId(R.id.pager);
        // Set the text on the pager, itself
        pager.setText(R.string.lipsum);

        setContentView(pager);
    }
}
