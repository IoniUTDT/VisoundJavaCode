package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
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
		AngulosTutorial(LISTAdeRECURSOS.UmbralAngulosTutorial, TIPOdeNivel.Angulos),
		Ejemplos(LISTAdeRECURSOS.ImagenesEjemplos, TIPOdeNivel.Ejemplos),
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
	
	public static String folderResources(LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.finalInternalPath + identificador.toString() + "/";
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
		return Trial.loadJsonTrial(Level.folderResources(identificador), id);	
	}

	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion,
			float timeConfiance, int loopsCount);
	
	public abstract int trialsLeft();

	abstract void sendDataLevel();

	
}
