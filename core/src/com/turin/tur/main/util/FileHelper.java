package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;


public class FileHelper {

	private static final String TAG = FileHelper.class.getName();
	
	public static String readInternalFile(String fileName) {
		FileHandle file = Gdx.files.internal(fileName);
		//Gdx.app.debug(TAG, file.file().getAbsolutePath());
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return s;
			}
		}
		Gdx.app.error(TAG,"Archivo: "+fileName+". No se lo ha encontrado o esta vacio");
		return "";
	}

	public static String readLocalFile(String fileName) {
		FileHandle file = Gdx.files.local(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return s;
			}
		}
		Gdx.app.error(TAG,"Archivo: "+fileName+" No se lo ha encontrado o esta vacio");
		return "";
	}
	
	public static void writeLocalFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(s, false);
	}

	
}
