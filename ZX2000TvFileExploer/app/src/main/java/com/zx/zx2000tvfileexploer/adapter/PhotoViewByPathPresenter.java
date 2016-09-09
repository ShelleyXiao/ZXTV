package com.zx.zx2000tvfileexploer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.entity.FileInfo;
import com.zx.zx2000tvfileexploer.fileutil.FileIconHelper;

import java.util.List;

/**
 * User: ShaudXiao
 * Date: 2016-09-08
 * Time: 15:20
 * Company: zx
 * Description:
 * FIXME
 */

public class PhotoViewByPathPresenter extends OpenPresenter {

    List<FileInfo> mDatas;

    FileIconHelper mFileIconHelper;

    public PhotoViewByPathPresenter(Context context, List<FileInfo> datas) {
        mDatas = datas;
        mFileIconHelper = new FileIconHelper(context);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
        ImageView imageView = (ImageView) gridViewHolder.iv;
        ImageView imageViewFrame = (ImageView) gridViewHolder.ivFrame;
        mFileIconHelper.setIcon(mDatas.get(position), imageView, imageViewFrame);
    }
}
