package com.turin.tur.main.levelsDesign;

import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.levelsDesign.Level.TIPOdeNivel;

public class LevelMaker {

	public static void make(LISTAdeNIVELES identificador) {
		if (identificador.tipoDeNivel == TIPOdeNivel.Tutorial) {
			TutorialLevel.buildLevel (identificador);
		}
	}

}
