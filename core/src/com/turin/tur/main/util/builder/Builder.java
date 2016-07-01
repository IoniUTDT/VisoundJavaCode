package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Listas.LISTAdeRECURSOS;
import com.turin.tur.main.diseno.Listas.TIPOSdeRECURSOS;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.levelsDesign.Resources;
import com.turin.tur.main.util.Constants.ResourcesCategorias;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	final static String TAG = Builder.class.getName();

	static String pathLevelsBackUp = ResourcesCategorias.Paths.LevelsBackUp+TimeUtils.millis()+"/";

	static int contadorTrials = 0;

	static int contadorLevels = 0;

	public static final int ResourceVersion = 22;
	public static final int levelVersion = 22; 
	public static int levelVersionFinal;
	
	static final Boolean makeLevels = false; 
	static final Boolean makeResources = true;
	static final Boolean build = true;

	public static void build(Array<LISTAdeRECURSOS> identificadores) {

		if (build) {
			if (makeResources) {
				PCBuilder.verifyResourcesVersion();
				Textos.crearTextos();
				for (LISTAdeRECURSOS identificador : identificadores) {
					Resources.makeResources(identificador);
				}
			}	
			if (makeLevels) {
				/*
				PCBuilder.makeLevels();
				for (Experiment exp : exps) {
					exp.makeLevels();
				}
				*/
			}
			System.exit(0);
		}
	}
}
