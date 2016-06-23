package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.util.Constants.Resources;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	final static String TAG = Builder.class.getName();

	static String pathLevelsBackUp = Resources.Paths.LevelsBackUp+TimeUtils.millis()+"/";

	static int contadorTrials = 0;

	static int contadorLevels = 0;

	public static final int ResourceVersion = 20;
	public static final int levelVersion = 20; 
	public static int levelVersionFinal;
	
	static final Boolean makeLevels = true; 
	static final Boolean makeResources = true;
	static final Boolean build = false;

	public Builder (){
		
	}
	
	public void build(Array<Experiment> exps) {

		if (build) {
			if (makeResources) {
				PCBuilder.verifyResourcesVersion();
				Textos.crearTextos();
				for (Experiment exp : exps) {
					exp.makeResources();
				}
			}	
			if (makeLevels) {
				PCBuilder.makeLevels();
				for (Experiment exp : exps) {
					exp.makeLevels();
				}
			}
			System.exit(0);
		}
	}
}
