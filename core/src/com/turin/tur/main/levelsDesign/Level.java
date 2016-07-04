package com.turin.tur.main.levelsDesign;

import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.levelsDesign.Resources.LISTAdeRECURSOS;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.Constants.ResourcesCategorias;

public abstract class Level {
	
	public enum LISTAdeNIVELES {
		TestAngulos30(LISTAdeRECURSOS.UmbralAngulosTutorial, TIPOdeNivel.Umbral),
		Tutorial(LISTAdeRECURSOS.ImagenesTutorial, TIPOdeNivel.Tutorial)
		;
		public static final int levelVersion = Builder.levelVersionFinal;
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;
		private LISTAdeNIVELES(LISTAdeRECURSOS listaDeRecursos, TIPOdeNivel tipoDeNivel) {
			this.listaDeRecursos = listaDeRecursos;
			this.tipoDeNivel = tipoDeNivel;
		}
	}

	public enum TIPOdeNivel {
		Tutorial, Umbral;
	}

	public static final String dinamicaPathName = "dinamica.meta";
	public static String folderResources(LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.finalInternalPath+identificador.toString()+"/";
	}
	public LISTAdeNIVELES identificadorNivel;
	public LevelAsset levelAssets;
	
	// Info basica que todos los niveles tienen que tener y que se carga cuando se inicia el programa
	public LevelInfo levelInfo;
	
	Level() {
	}
	
	Level (LISTAdeNIVELES identificador) {
		this.identificadorNivel = identificador;
		this.levelInfo = LevelInfo.loadLevelInfo(identificador);
		this.loadDinamica();
	}
	public abstract Trial getNextTrial();
	public abstract boolean goConfiance();
	public abstract void interrupt();
	public abstract boolean islevelCompleted();
	
	
	public abstract void levelCompleted();
	
	public abstract void loadDinamica();
	
	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion, float timeConfiance,
			int loopsCount);
	
}
