package com.zx.zx2000onlinevideo.ui.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * User: ShaudXiao
 * Date: 2016-08-03
 * Time: 16:06
 * Company: zx
 * Description: 应用自定义字体
 * FIXME
 */

public class BrandTextView extends TextView {
    public BrandTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public BrandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public BrandTextView(Context context) {
        super(context);
    }
    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "helvetica_neueltpro_thex.otf"));
        }
    }
}
