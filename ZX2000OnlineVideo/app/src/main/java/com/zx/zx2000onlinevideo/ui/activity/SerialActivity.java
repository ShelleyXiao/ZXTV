package com.zx.zx2000onlinevideo.ui.activity;

import android.graphics.Color;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.androidtvwidget.view.GridViewTV;
import com.zx.zx2000onlinevideo.R;
import com.zx.zx2000onlinevideo.ui.view.ViewStatusTitleView;

import butterknife.BindView;

/**
 * User: ShaudXiao
 * Date: 2016-08-08
 * Time: 16:54
 * Company: zx
 * Description:
 * FIXME
 */

public class SerialActivity extends BaseActivity {


    @BindView(R.id.status_bar)
    ViewStatusTitleView mStatusBar;
    @BindView(R.id.movie_details_img_id)
    ImageView mMovieDetailsImgId;
    @BindView(R.id.serial_gridview)
    GridViewTV mSerialGridview;
    @BindView(R.id.serial_movie_details_main)
    LinearLayout mSerialMovieDetailsMain;

    private View oldView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_serial;
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void initialized() {
        mSerialGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        mSerialGridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView button = (TextView) view.findViewById(R.id.serial_item_but);
                button.setTextColor(Color.parseColor("#57fffa"));
                button.setBackgroundResource(R.drawable.lemon_servial_pree);
                if (oldView != null) {
                    TextView button2 = (TextView) oldView.findViewById(R.id.serial_item_but);
                    button2.setTextColor(Color.parseColor("#b3aeae"));
                    button2.setBackgroundResource(R.drawable.lemon_servial_none);
                }
                oldView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSerialGridview.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                //初始化选择
                oldView = mSerialGridview.getChildAt(0 - mSerialGridview.getFirstVisiblePosition());
                if (oldView != null) {
                    TextView button = (TextView) oldView.findViewById(R.id.serial_item_but);
                    button.setTextColor(Color.parseColor("#57fffa"));
                    button.setBackgroundResource(R.drawable.lemon_servial_pree);
                }
            }
        });
    }

}
