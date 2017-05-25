package com.zx.zx2000tvfileexploer.ui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.fileutil.FileSettingsHelper;
import com.zx.zx2000tvfileexploer.fileutil.FileSortHelper;
import com.zx.zx2000tvfileexploer.ui.base.BaseFileOperationActivity;
import com.zx.zx2000tvfileexploer.utils.Logger;

/**
 * User: ShaudXiao
 * Date: 2016-08-31
 * Time: 10:55
 * Company: zx
 * Description:
 * FIXME
 */

public class SettingActivivty extends BaseFileOperationActivity implements View.OnFocusChangeListener {


    private RadioGroup mRadioGroup;
    private CheckBox mShowHideCheck;
    private TextView mShowExplain;

    private FileSettingsHelper mFileSettingsHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        setCurPath(getString(R.string.settting_title));

        mRadioGroup = (RadioGroup) findViewById(R.id.setting_sort_type);
        mShowHideCheck = (CheckBox) findViewById(R.id.showhide_checkbox);
        mShowExplain = (TextView) findViewById(R.id.menu_explain);

        findViewById(R.id.sort_name).setOnFocusChangeListener(this);
        findViewById(R.id.sort_size).setOnFocusChangeListener(this);
        findViewById(R.id.sort_time).setOnFocusChangeListener(this);
        findViewById(R.id.sort_type).setOnFocusChangeListener(this);
        mShowHideCheck.setOnFocusChangeListener(this);

        mFileSettingsHelper = FileSettingsHelper.getInstance(this);

        setUpViews();

        findViewById(R.id.sort_name).requestFocus();
    }

    @Override
    protected void initialized() {
        super.initialized();
    }

    @Override
    public void updateDiskInfo() {

    }


    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.sort_name ||
                view.getId() == R.id.sort_time ||
                view.getId() == R.id.sort_size ||
                view.getId() == R.id.sort_size) {
            mShowExplain.setText(R.string.sort_type_title_explain);

        } else if (view.getId() == R.id.showhide_checkbox) {
            mShowExplain.setText(R.string.show_hiden_file_explain);
        }
    }

    private void setUpViews() {

        if (mFileSettingsHelper.getBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false)) {
            mShowHideCheck.setChecked(true);
        } else {
            mShowHideCheck.setChecked(false);
        }

        FileSortHelper.SortMethod sortMethod = mFileSettingsHelper.getSortType();
        switch (sortMethod) {
            case NAME:
                ((RadioButton)findViewById(R.id.sort_name)).setChecked(true);
                break;
            case DATE:
                ((RadioButton)findViewById(R.id.sort_time)).setChecked(true);
                break;
            case SIZE:
                ((RadioButton)findViewById(R.id.sort_size)).setChecked(true);
                break;
            case TYPE:
                ((RadioButton)findViewById(R.id.sort_type)).setChecked(true);
                break;
            default:
                break;
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.sort_name:
                        mFileSettingsHelper.putSortType(FileSortHelper.SortMethod.NAME);
                        break;
                    case R.id.sort_size:
                        mFileSettingsHelper.putSortType(FileSortHelper.SortMethod.SIZE);
                        break;
                    case R.id.sort_time:
                        mFileSettingsHelper.putSortType(FileSortHelper.SortMethod.DATE);
                        break;
                    case R.id.sort_type:
                        mFileSettingsHelper.putSortType(FileSortHelper.SortMethod.TYPE);
                        break;
                }
            }
        });

        mShowHideCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Logger.getLogger().d("***************mShowHideCheck**checked**** ");
                    mFileSettingsHelper.putBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, true);
                } else {
                    Logger.getLogger().d("***************mShowHideCheck****** ");
                    mFileSettingsHelper.putBoolean(FileSettingsHelper.KEY_SHOW_HIDEFILE, false);
                }

            }
        });

//        mShowHideCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(mShowHideCheck.isChecked()) {
////                    mShowHideCheck.setChecked(false);
////                } else {
////                    mShowHideCheck.setChecked(true);
////                }
//                Logger.getLogger().d("************** click");
//                mShowHideCheck.toggle();
//            }
//        });
    }
}
