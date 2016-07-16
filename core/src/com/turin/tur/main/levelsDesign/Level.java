package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.RunningSound;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.InternetNuevo;
import com.turin.tur.main.util.InternetNuevo.TIPOdeENVIO;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Builder;

/**
 * @author ionatan
 *
 */
public abstract class Level {

	public enum LISTAdeNIVELES {
		Ejemplos(LISTAdeRECURSOS.ImagenesEjemplos, TIPOdeNivel.Ejemplos, ELECCION.TODAS),
		AngulosTutorial(LISTAdeRECURSOS.RecursosAngulosTutorial, TIPOdeNivel.Angulos, ELECCION.TODAS),
		ParalelismoTutorial(LISTAdeRECURSOS.RecursosParalelismoTutorial, TIPOdeNivel.Paralelismo, ELECCION.TODAS), 
		TESTP30(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.TODAS),
		TESTP60(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.TODAS),
		TESTP120(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.TODAS),
		TESTP150(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.TODAS),
		TESTA30(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.TODAS),
		TESTA60(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.TODAS),
		TESTA120(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.TODAS),
		TESTA150(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.TODAS), 
		ENTRENAMIENTOA30INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A30),
		ENTRENAMIENTOA30MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A30),
		ENTRENAMIENTOA30FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A30),
		ENTRENAMIENTOP30INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P30),
		ENTRENAMIENTOP30MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P30),
		ENTRENAMIENTOP30FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P30),
		ENTRENAMIENTOA60INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A60),
		ENTRENAMIENTOA60MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A60),
		ENTRENAMIENTOA60FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A60),
		ENTRENAMIENTOP60INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P60),
		ENTRENAMIENTOP60MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P60),
		ENTRENAMIENTOP60FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P60),
		ENTRENAMIENTOA120INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A120),
		ENTRENAMIENTOA120MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A120),
		ENTRENAMIENTOA120FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A120),
		ENTRENAMIENTOP120INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P120),
		ENTRENAMIENTOP120MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P120),
		ENTRENAMIENTOP120FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P120),
		ENTRENAMIENTOA150INICIAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A150),
		ENTRENAMIENTOA150MEDIO(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A150),
		ENTRENAMIENTOA150FINAL(LISTAdeRECURSOS.RecursosAngulosTransferencia, TIPOdeNivel.Angulos, ELECCION.A150),
		ENTRENAMIENTOP150INICIAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P150),
		ENTRENAMIENTOP150MEDIO(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P150),
		ENTRENAMIENTOP150FINAL(LISTAdeRECURSOS.RecursosParalelismoTransferencia, TIPOdeNivel.Paralelismo, ELECCION.P150),
		;
		
		public static final int levelVersion = Builder.levelVersionFinal;
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;
		public ELECCION eleccion;

		private LISTAdeNIVELES(LISTAdeRECURSOS listaDeRecursos, TIPOdeNivel tipoDeNivel, ELECCION eleccion) {
			this.listaDeRecursos = listaDeRecursos;
			this.tipoDeNivel = tipoDeNivel;
			this.eleccion = eleccion;
		}
	}

	public enum ELECCION {
		TODAS, A30, A60, A120, A150, P30, P60, P120, P150
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

	public class LevelLog {

		Session sesion;
		LISTAdeNIVELES identificadorNivel;
		long instance;
		
		public LevelLog(LISTAdeNIVELES identificador, Session sesion) {
			this.instance = TimeUtils.millis();
			this.sesion = sesion;
			this.identificadorNivel = identificador;
			// Hacemos un envio
			InternetNuevo.agregarEnvio(this, TIPOdeENVIO.NIVEL, identificador.toString());
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
		specificLoads();
	}
	
	abstract void specificLoads();

	public JsonTrial loadJsonTrial (int id) {
		return Trial.loadJsonTrial(Level.folderResourcesLevelLocal(identificador), id);	
	}

	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion,
			float timeConfiance, int loopsCount);
	
	public abstract int trialsLeft();

	abstract void sendDataLevel();

	
}
