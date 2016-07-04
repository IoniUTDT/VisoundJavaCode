package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.Level.LISTAdeRECURSOS;
import com.turin.tur.main.levelsDesign.Level.TIPOdeNivel;
import com.turin.tur.main.levelsDesign.LevelAngulos;
import com.turin.tur.main.levelsDesign.LevelEjemplos;
import com.turin.tur.main.levelsDesign.LevelUmbral;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	final static String TAG = Builder.class.getName();

	static int contadorTrials = 0;

	public static final int ResourceVersion = 23;
	public static final int levelVersion = 23; 
	public static int levelVersionFinal = levelVersion; // Se modifica mas adelante si corresponde
	
	static final Boolean makeLevels = false; 
	static final Boolean makeResources = true;

	public static void buildResources(Array<LISTAdeRECURSOS> identificadores) {
		if (makeResources) {
			PCBuilder.verifyResourcesVersion();
			Textos.crearTextos();
			for (LISTAdeRECURSOS identificador : identificadores) {
				if (identificador== LISTAdeRECURSOS.ImagenesEjemplos) {
					LevelEjemplos.buildResources (identificador);
				} else {
					LevelUmbral.buildResources (identificador);
				}
			}
			System.exit(0);
		}	
	}
	
	public static void buildLevels (Array<Level.LISTAdeNIVELES> identificadores) {
		if (makeLevels) {
			PCBuilder.CheckLevels();
			for (Level.LISTAdeNIVELES identificador : identificadores) {
				if (identificador.tipoDeNivel == TIPOdeNivel.Ejemplos) {
					LevelEjemplos.buildLevel (identificador);
				}
				if (identificador.tipoDeNivel == TIPOdeNivel.Angulos) {
					LevelAngulos.buildLevel (identificador);
				}
			}
			System.exit(0);
		}
	}
}
