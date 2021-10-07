package com.example.taobao.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/*工具类用于显示和隐藏键盘*/
public class KeyboardUtil {
    public static void hide(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void show(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view, 0);
    }
}
