package com.kanyun.kace;

import android.view.View;

/**
 * Created by benny at 2023/8/17 15:28.
 */
public class KaceViewUtils {

    public static <T extends View> T findViewById(View view, int id, Class<T> viewClass) {
        return view.findViewById(id);
    }

}
