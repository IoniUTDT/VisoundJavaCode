package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.levelsDesign.Level.LISTAdeRECURSOS;
import com.turin.tur.main.levelsDesign.Level.TIPOdeNivel;
import com.turin.tur.main.levelsDesign.LevelEjemplos;
import com.turin.tur.main.levelsDesign.LevelUmbralStatic;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	final static String TAG = Builder.class.getName();

	static int contadorTrials = 0;

	public static final int ResourceVersion = 26;
	public static final int levelVersion = 24; 
	public static int levelVersionFinal = levelVersion; // Se modifica mas adelante si corresponde
	
	static final Boolean makeLevels = false; 
	static final Boolean makeResources = false;

	public static void buildResources(Array<LISTAdeRECURSOS> identificadores) {
		if (makeResources) {
			PCBuilder.verifyResourcesVersion();
			Textos.crearTextos();
			for (LISTAdeRECURSOS identificador : identificadores) {
				if (identificador== LISTAdeRECURSOS.ImagenesEjemplos) {
					LevelEjemplos.buildResources (identificador);
				} else {
					LevelUmbralStatic.buildResources (identificador);
				}
			}
			System.exit(0);
		}	
	}
	
	public static void buildLevels (LISTAdeNIVELES[] listAdeNIVELESs) {
		if (makeLevels) {
			PCBuilder.CheckLevels();
			for (Level.LISTAdeNIVELES identificador : listAdeNIVELESs) {
				if (identificador.tipoDeNivel == TIPOdeNivel.Ejemplos) {
					LevelEjemplos.buildLevel (identificador);
				}
				if (identificador.tipoDeNivel == TIPOdeNivel.Angulos) {
					LevelUmbralStatic.buildLevel (identificador);
				}
				if (identificador.tipoDeNivel == TIPOdeNivel.Paralelismo) {
					LevelUmbralStatic.buildLevel (identificador);
				}
			}
			System.exit(0);
		}
	}
}
