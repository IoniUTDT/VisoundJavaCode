package com.turin.tur.main.util.builder;

import com.turin.tur.main.util.Constants.Resources;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private static final String TAG = Builder.class.getName();

	public static int height = Resources.Paths.height;
	public static int width = Resources.Paths.width;
	public static final int ResourceVersion = 130;
	public static final int levelVersion = 23;
	public static final String AppVersion = "UmbralCompletoAngulos"; 
	
	static final Boolean makeLevels = false;
	static final Boolean makeResources = false;
	
	
	public static void build() {

		if (makeResources) {
			ResourcesMaker.BuildResources();
			System.exit(0);
		}	
		if (makeLevels) {
			
			LevelMaker.makeLevels();
			ResourcesExport.createStructure();
			System.exit(0);
		}

	}
}
