package com.zx.zx2000tvfileexploer.interfaces;

public interface IOperationProgressListener {

	 void onOperationFinish(boolean success);

     void onFileChanged(String path);
     
}
