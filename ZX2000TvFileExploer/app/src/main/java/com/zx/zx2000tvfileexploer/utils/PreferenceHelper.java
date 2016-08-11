package com.zx.zx2000tvfileexploer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {
	
	public static void write(Context ctx, String fileName, String key, int value) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	
	public static void write(Context ctx, String fileName, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}	
	
	public static void write(Context ctx, String fileName, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static int readInt(Context ctx, String fileName, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
	
	public static String readString(Context ctx, String fileName, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}	
	
	public static boolean readBoolean(Context ctx, String fileName, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}
	
	public static void clear(Context ctx, String fileName) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	} 
	
	
}
