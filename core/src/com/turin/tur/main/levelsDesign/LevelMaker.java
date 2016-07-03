package com.turin.tur.main.levelsDesign;

import com.turin.tur.main.diseno.Listas.LISTAdeNIVELES;
import com.turin.tur.main.diseno.Listas.TIPOdeNivel;

public class LevelMaker {

	public static void make(LISTAdeNIVELES identificador) {
		if (identificador.tipoDeNivel == TIPOdeNivel.Tutorial) {
			TutorialLevel.buildLevel (identificador);
		}
	}

}
