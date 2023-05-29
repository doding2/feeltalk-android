package com.clonect.feeltalk.new_presentation.ui.main_navigation.home;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

public class CountCenterVerticalSpan extends MetricAffectingSpan {

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
        textPaint.baselineShift -= getBaselineShift(textPaint);
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        textPaint.baselineShift -= getBaselineShift(textPaint);
    }

    private int getBaselineShift(TextPaint tp) {
        float total = tp.ascent() + tp.descent();
        return (int) (total / 12f);
    }
}