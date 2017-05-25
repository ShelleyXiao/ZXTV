package com.zx.zx2000tvfileexploer;

import com.zx.zx2000tvfileexploer.utils.SDCardUtils;

public interface GlobalConsts {
    public static final String KEY_BASE_SD = "key_base_sd";

    public static final String KEY_SHOW_CATEGORY = "key_show_category";

    public static final String INTENT_EXTRA_TAB = "TAB";

    public static final String ROOT_PATH = "/";

    public static final String SDCARD_PATH = ROOT_PATH + "sdcard";
    
    public static final String FLASH_AB_PATH = SDCardUtils.getInternalStorageDirectoryPath();
    
    public static final String TF_AB_PATH = SDCardUtils.getExternalStorageDirectoryPath(true);
    
    public static final String OTA_AB_PATH = SDCardUtils.getExternalStorageDirectoryPath(false);
    
    public static final String MAIN_MENU_TAB = "main_menu_tab";

    public static final String FILEUPDATEBROADCAST = "action.intent.action_file_update_broadast";

    public static final String EXTRA_DIR_PATH = "com.zx.zx2000tvfileexploer.extra.DIR_PATH";

    public static final String EXTRA_DIALOG_FILE_HOLDER = "com.zx.zx2000tvfileexploer.extra.DIALOG_FILE";

    public static final int OPERATION_UP_LEVEL = 3;

    public static final int TAKE_PHOTO = 1001;

    public static final int INTENT_EXTRA_APK_VLAUE = 0x01;
    public static final int INTENT_EXTRA_MUSIC_VLAUE = 0x01 << 1;
    public static final int INTENT_EXTRA_PICTURE_VLAUE = 0x01 << 2;
    public static final int INTENT_EXTRA_VIDEO_VLAUE = 0x01 << 3;
    public static final int INTENT_EXTRA_ALL_VLAUE = 0x01 << 4;
    public static final int INTENT_EXTRA_SEARTCH_VALUE = 0X01 << 5;

    public static final int INTENT_EXTRA_PIC_CURSR = 0X01;
    public static final int INTENT_EXTRA_PIC_PATH = 0X02;

    public static final String INTENT_ACTION_PIC_SHOW = "com.zx.fileexplore.picture.show";

    public static final String KEY_PREF_OTG = "uri_usb_otg";

    public static final String KEY_INTENT_PROCESS_VIEWER = "openprocesses";
    public static final String TAG_INTENT_FILTER_FAILED_OPS = "failedOps";
    public static final String TAG_INTENT_FILTER_GENERAL = "general_communications";
    public static final String ARGS_KEY_LOADER = "loader_cloud_args_service";
    public static final String LAUNCHER_LEXA= "lexa";
    public static final String LOAD_LIST= "loadlist";
}
