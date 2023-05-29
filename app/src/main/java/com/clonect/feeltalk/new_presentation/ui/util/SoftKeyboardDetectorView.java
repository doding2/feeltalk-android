package com.clonect.feeltalk.new_presentation.ui.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;

import androidx.annotation.Nullable;

public class SoftKeyboardDetectorView extends View {

    private boolean mShownKeyboard;
    private OnShownKeyboardListener mOnShownSoftKeyboard;
    private OnHiddenKeyboardListener onHiddenSoftKeyboard;

    public SoftKeyboardDetectorView(Context context) {
        super(context);
    }

    public SoftKeyboardDetectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Activity activity = (Activity) getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        defaultDisplay.getSize(size);
        int screenHeight = size.y;
        int diffHeight = (screenHeight - statusBarHeight) - h;
        Log.d("FeeltalkInfo", "diffHeight: " + diffHeight);
        if (diffHeight > 100 && !mShownKeyboard) { // 모든 키보드는 100px보다 크다고 가정
            mShownKeyboard = true;
            onShownSoftKeyboard();
        } else if (diffHeight < 100 && mShownKeyboard) {
            mShownKeyboard = false;
            onHiddenSoftKeyboard();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onHiddenSoftKeyboard() {
        if (onHiddenSoftKeyboard != null)
            onHiddenSoftKeyboard.onHiddenSoftKeyboard();
    }

    public void onShownSoftKeyboard() {
        if (mOnShownSoftKeyboard != null)
            mOnShownSoftKeyboard.onShowSoftKeyboard();
    }

    public void setOnShownKeyboard(OnShownKeyboardListener listener) {
        mOnShownSoftKeyboard = listener;
    }

    public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
        onHiddenSoftKeyboard = listener;
    }

    public interface OnShownKeyboardListener {
        public void onShowSoftKeyboard();
    }

    public interface OnHiddenKeyboardListener {
        public void onHiddenSoftKeyboard();
    }
}