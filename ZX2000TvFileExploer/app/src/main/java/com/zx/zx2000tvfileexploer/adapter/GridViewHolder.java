package com.zx.zx2000tvfileexploer.adapter;

import android.view.View;
import android.widget.ImageView;

import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.zx.zx2000tvfileexploer.R;

public class GridViewHolder extends OpenPresenter.ViewHolder {
	
	public ImageView iv;
	public ImageView ivFrame;


	public GridViewHolder(View itemView) {
		super(itemView);
		iv = (ImageView)itemView.findViewById(R.id.thumb);
		ivFrame = (ImageView)itemView.findViewById(R.id.file_image_frame);
	}

}
