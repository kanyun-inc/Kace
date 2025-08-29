package com.kanyun.kace;

import android.app.Dialog;
import android.view.View;

public class KaceDialogUtils {

    public static <T extends View> T findViewById(Dialog dialog, int id, Class<T> viewClass) {
        return dialog.findViewById(id);
    }

}
