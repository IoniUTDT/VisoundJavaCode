package com.turin.tur.main.util.builder;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private final String TAG = Builder.class.getName();

	public static final int ResourceVersion = 131;
	public static final int levelVersion = 26;
	public static int levelVersionFinal;
	public static final String AppVersion = "UmbralCompletoAngulos"; 
	public static final boolean categorizar = false;
	
	static final Boolean makeLevels = false;
	static final Boolean makeResources = false;
	

	public Builder (){
		
	}
	
	public void build() {

		if (makeResources) {
			ResourcesMaker maker = new ResourcesMaker();
			System.exit(0);
		}	
		if (makeLevels) {
			
			LevelsConstructor levelconstructor = new LevelsConstructor();
			
			//LevelMaker.makeLevels();
			//ResourcesExport.createStructure();
			System.exit(0);
		}

	}
}
