package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.levelsDesign.LevelMaker;
import com.turin.tur.main.levelsDesign.Resources;
import com.turin.tur.main.levelsDesign.Resources.LISTAdeRECURSOS;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	final static String TAG = Builder.class.getName();

	static int contadorTrials = 0;

	public static final int ResourceVersion = 22;
	public static final int levelVersion = 22; 
	public static int levelVersionFinal = levelVersion; // Se modifica mas adelante si corresponde
	
	static final Boolean makeLevels = false; 
	static final Boolean makeResources = false;

	public static void buildResources(Array<Resources.LISTAdeRECURSOS> identificadores) {
		if (makeResources) {
			PCBuilder.verifyResourcesVersion();
			Textos.crearTextos();
			for (Resources.LISTAdeRECURSOS identificador : identificadores) {
				Resources.makeResources(identificador);
			}
			System.exit(0);
		}	
	}
	
	public static void buildLevels (Array<Level.LISTAdeNIVELES> identificadores) {
		if (makeLevels) {
			PCBuilder.CheckLevels();
			for (Level.LISTAdeNIVELES identificador : identificadores) {
				LevelMaker.make(identificador);
			}
			System.exit(0);
		}
	}
}
