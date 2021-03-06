package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Internet.TIPOdeENVIO;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Builder;

/**
 * @author ionatan
 *
 */
public abstract class Level {

	public enum LISTAdeNIVELES {
		Ejemplos(LISTAdeRECURSOS.ImagenesEjemplos, TIPOdeNivel.Ejemplos, new ELECCION[] {ELECCION.TODAS}, "Tutorial"),
		AngulosTutorial(LISTAdeRECURSOS.RecursosAngulosTutorial, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.TODAS}, "Tutorial Angulos"),
		ParalelismoTutorial(LISTAdeRECURSOS.RecursosParalelismoTutorial, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.TODAS}, "Tutorial Paralelas"), 
		TESTP30(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P30, ELECCION.TODAS}, "Nivel 1"),
		TESTP60(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P60, ELECCION.TODAS}, "Nivel 2"),
		TESTP120(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P120, ELECCION.TODAS}, "Nivel 3"),
		TESTP150(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P150, ELECCION.TODAS}, "Nivel 4"),
		TESTA30(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A30, ELECCION.TODAS}, "Nivel 5"),
		TESTA60(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A60, ELECCION.TODAS}, "Nivel 6"),
		TESTA120(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A120, ELECCION.TODAS}, "Nivel 7"),
		TESTA150(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A150, ELECCION.TODAS}, "Nivel 8"), 
		ENTRENAMIENTOA30INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A30}, "Inicial"),
		ENTRENAMIENTOA30MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A30}, "Intermedio"),
		ENTRENAMIENTOA30FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A30}, "Final"),
		ENTRENAMIENTOP30INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P30}, "Inicial"),
		ENTRENAMIENTOP30MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P30}, "Intermedio"),
		ENTRENAMIENTOP30FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P30}, "Final"),
		ENTRENAMIENTOA60INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A60}, "Inicial"),
		ENTRENAMIENTOA60MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A60}, "Intermedio"),
		ENTRENAMIENTOA60FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A60}, "Final"),
		ENTRENAMIENTOP60INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P60}, "Inicial"),
		ENTRENAMIENTOP60MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P60}, "Intermedio"),
		ENTRENAMIENTOP60FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P60}, "Final"),
		ENTRENAMIENTOA120INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A120}, "Inicial"),
		ENTRENAMIENTOA120MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A120}, "Intermedio"),
		ENTRENAMIENTOA120FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A120}, "Final"),
		ENTRENAMIENTOP120INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P120}, "Inicial"),
		ENTRENAMIENTOP120MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P120}, "Intermedio"),
		ENTRENAMIENTOP120FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P120}, "Final"),
		ENTRENAMIENTOA150INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A150}, "Inicial"),
		ENTRENAMIENTOA150MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A150}, "Intermedio"),
		ENTRENAMIENTOA150FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, new ELECCION[] {ELECCION.A150}, "Final"),
		ENTRENAMIENTOP150INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P150}, "Inicial"),
		ENTRENAMIENTOP150MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P150}, "Intermedio"),
		ENTRENAMIENTOP150FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, new ELECCION[] {ELECCION.P150}, "Final"),
		;
		
		public static final int levelVersion = Builder.levelVersionFinal;
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;
		public String publicName;
		public Array<ELECCION> eleccionesIncluidas = new Array<ELECCION>();

		private LISTAdeNIVELES(LISTAdeRECURSOS listaDeRecursos, TIPOdeNivel tipoDeNivel, ELECCION[] elecciones, String publicName) {
			this.listaDeRecursos = listaDeRecursos;
			this.tipoDeNivel = tipoDeNivel;
			this.eleccionesIncluidas.addAll(elecciones);
			this.publicName = publicName;
		}
	}

	public enum ELECCION {
		TODAS, A30, A60, A120, A150, P30, P60, P120, P150, CONTROL
	}
	
	public enum LISTAdeRECURSOS {
		ImagenesEjemplos (TIPOSdeRECURSOS.ImagenesTutorial),
		RecursosAngulosTransferencia (TIPOSdeRECURSOS.Angulos),
		RecursosAngulosTutorial (TIPOSdeRECURSOS.Angulos),
		RecursosParalelismoTransferencia (TIPOSdeRECURSOS.Paralelismo),
		RecursosParalelismoTutorial (TIPOSdeRECURSOS.Paralelismo);
		
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

	public static class LevelLog {

		Session sesion;
		LISTAdeNIVELES identificadorNivel;
		long instance;
		
		public LevelLog () {
			
		}
		
		public LevelLog(LISTAdeNIVELES identificador, Session sesion) {
			this.instance = TimeUtils.millis();
			this.sesion = sesion;
			this.identificadorNivel = identificador;
			// Hacemos un envio
			
			Internet.crearEnvio(this, TIPOdeENVIO.INICIONIVEL, identificador.toString());
			// Internet.addDataToSend(this, TIPO_ENVIO.NEWLEVEL, identificador.toString());			
		}
		
	}
	
	public static final String dinamicaPathName = "dinamica.meta";

	private static final String TAG = Level.class.getName();

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
	
	public static String folderResourcesBuild(LISTAdeRECURSOS identificador) {
		return ResourcesCategorias.Paths.finalInternalPath + identificador.toString() + "/";
	}
	
	public static String folderResourcesLocal(LISTAdeRECURSOS identificador) {
		return ResourcesCategorias.Paths.InternalResources + identificador.toString() + "/";
	}
	
	public static String folderResourcesLevelBuild(LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.finalInternalPath + identificador.toString() + "/";
	}
	
	public static String folderResourcesLevelLocal (LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.InternalResources + identificador.toString() + "/";
	}

	public LISTAdeNIVELES identificador;
	public LevelAsset levelAssets;
	protected boolean levelCompleted;
	protected LevelLog log;

	public Level () {}
	public Level(LISTAdeNIVELES identificador) {
		this.identificador = identificador;
	}
	public abstract Trial getNextTrial();
	
	public abstract boolean goConfiance();
	
	public abstract void interrupt();
	
	public boolean islevelCompleted() {
		return this.levelCompleted;
	}

	public void levelCompletedAction() {
		this.sendDataLevel();
	}

	public void initLevel (Session sesion) {
		log = new LevelLog(identificador, sesion);
		levelAssets = new LevelAsset(identificador);
		levelCompleted = false;
		specificLoads(sesion);
	}
	
	abstract void specificLoads(Session sesion);

	public JsonTrial loadJsonTrial (int id) {
		return Trial.loadJsonTrial(Level.folderResourcesLevelLocal(identificador), id);	
	}

	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion,
			float timeConfiance, int loopsCount);
	
	public abstract int trialsLeft();

	abstract void sendDataLevel();

	public abstract double getDesviacionActual();

	public abstract int getNivelSenalActual();

	public abstract int getrepeatnumber();
	
}
