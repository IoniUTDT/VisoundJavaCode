package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Builder;

/**
 * @author ionatan
 *
 */
public abstract class Level {

	private static final String TAG = Level.class.getName();

	public enum LISTAdeNIVELES {
		Ejemplos(LISTAdeRECURSOS.ImagenesEjemplos, TIPOdeNivel.Ejemplos),
		AngulosTutorial(LISTAdeRECURSOS.UmbralAngulosTutorial, TIPOdeNivel.Angulos),
		ParalelismoTutorial(LISTAdeRECURSOS.UmbralParalelismoTutorial, TIPOdeNivel.Paralelismo),
		;
		public static final int levelVersion = Builder.levelVersionFinal;
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;

		private LISTAdeNIVELES(LISTAdeRECURSOS listaDeRecursos, TIPOdeNivel tipoDeNivel) {
			this.listaDeRecursos = listaDeRecursos;
			this.tipoDeNivel = tipoDeNivel;
		}
	}

	public enum LISTAdeRECURSOS {
		ImagenesEjemplos (TIPOSdeRECURSOS.ImagenesTutorial),
		UmbralAngulosTransferencia (TIPOSdeRECURSOS.Angulos),
		UmbralAngulosTutorial (TIPOSdeRECURSOS.Angulos),
		UmbralParalelismoTransferencia (TIPOSdeRECURSOS.Paralelismo),
		UmbralParalelismoTutorial (TIPOSdeRECURSOS.Paralelismo);
		
		public TIPOSdeRECURSOS tipoDeRecursos;
		
		private LISTAdeRECURSOS(TIPOSdeRECURSOS tipoDeRecursos) {
			this.tipoDeRecursos = tipoDeRecursos;
		}
	}

	public enum TIPOdeNivel {
		Angulos, Ejemplos, Paralelismo;
	}

	public enum TIPOSdeRECURSOS {
		Angulos, ImagenesTutorial, Paralelismo;
		public static final int ResourceVersion = Builder.ResourceVersion;
	}

	public static final String dinamicaPathName = "dinamica.meta";

	public Level () {}
	
	public Level(LISTAdeNIVELES identificador) {
		this.identificadorNivel = identificador;
	}

	public static String folderResources(LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.finalInternalPath + identificador.toString() + "/";
	}
	
	public static Level createLevel(LISTAdeNIVELES identificador) {
		switch (identificador.tipoDeNivel) {
		case Ejemplos:
			return new LevelEjemplos(identificador);
		case Angulos:
		case Paralelismo:
			return new LevelUmbral(identificador);
			default:
				Gdx.app.error(TAG, "Se intento crear un nivel de un tipo desconocido!");
				return null;
		}
	}

	public LISTAdeNIVELES identificadorNivel;
	public LevelAsset levelAssets;
	
	public abstract Trial getNextTrial();

	public abstract boolean goConfiance();

	public abstract void interrupt();

	public abstract boolean islevelCompleted();

	public abstract void levelCompleted();

	public abstract void loadDinamica();

	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion,
			float timeConfiance, int loopsCount);

	
}
