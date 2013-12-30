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

import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Experimental
 */
public class TextViewUtils {

    private static final String TAG = "TextViewUtils";

    private TextViewUtils() {
    }

    /**
     * Not ready for prime time.
     */
    public static String getJustifiedText(CharSequence text, TextView view) {
        ArrayList<String> lineList = lineBreak(String.valueOf(text), view.getPaint(),
                view.getLayout().getWidth());

        return TextUtils.join(" ", lineList).replaceFirst("\\s", "");
    }

    /**
     * TODO Respect \n
     */
    private static ArrayList<String> lineBreak(String text, Paint paint, float contentWidth) {
        String[] wordArray = text.split("\\s");
        ArrayList<String> lineList = new ArrayList<String>();
        String myText = "";

        for (String word : wordArray) {
            if (paint.measureText(myText + " " + word) <= contentWidth)
                myText = myText + " " + word;
            else {
                int totalSpacesToInsert = (int) ((contentWidth - paint.measureText(myText)) / paint
                        .measureText(" "));
                lineList.add(justifyLine(myText, totalSpacesToInsert));
                myText = word;
            }
        }
        lineList.add(myText);
        return lineList;
    }

    private static String justifyLine(String text, int totalSpacesToInsert) {
        String[] wordArray = text.split("\\s");
        String toAppend = " ";

        while (totalSpacesToInsert >= wordArray.length - 1 && wordArray.length > 1) {
            toAppend = toAppend + " ";
            totalSpacesToInsert = totalSpacesToInsert - (wordArray.length - 1);
        }
        int i = 0;
        String justifiedText = "";
        for (String word : wordArray) {
            if (i < totalSpacesToInsert)
                justifiedText = justifiedText + word + " " + toAppend;

            else
                justifiedText = justifiedText + word + toAppend;

            i++;
        }

        return justifiedText;
    }
}
