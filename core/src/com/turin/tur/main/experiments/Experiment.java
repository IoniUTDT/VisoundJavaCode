package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.ExperimentLog;
import com.turin.tur.main.experiments.Experiments.GenericSetup;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;

public interface Experiment {
	// Cosas offline
	String getName();
	void makeResources(); // Se encarga de crear los recuros
	void makeLevels(); // Se encarga de armar la estructura de niveles
	// Cosas online de control de flujo de informacion
	Trial getNextTrial(); // Devuelve el proximo trial a usar
	void returnAnswer (boolean answer, float confianza, float selectionTime, float confianceTime, int soundLoops); // Le indica al experimento como salio el trial
	void initGame (Session session); // Se ejecuta cuando se inicial el juego (la idea es que aca se inicialicen todas las variables generales.
	void initLevel (Level level); // Inicia cuestiones generales del nivel
	void levelCompleted(); // Se ejecuta cuando se detiene un nivel (la idea es que aca se generen todos los logs y se envien al servidor)
	boolean islevelCompleted (); // Devuelve la variable levelCompleted (hay que hacerlo asi porque sino no esta en la interfaz
	// Cosas de interfaz general
	Array<LevelStatus> levelsStatus(); // Pasa la info para que la pantalla menu pueda armarse como corresponde
	void interrupt(); // Sirve para ejecutar acciones cuando se interrumpe el experimento porque el usuario decide salir del nivel
	int trialsLeft(); // Indica cuantos trials quedan como maximo (sievr para mostrar indicadores en la interfaz
	
	public abstract class GenericExp implements Experiment {

		final static String TAG = GenericExp.class.getName();

		// Cosas generales
		protected ExpSettings expSettings;
		
		// Cosas que manejan la dinamica en cada ejecucion
		protected Level level;
		protected Session session;
		protected Trial trial;
		public boolean levelCompleted;
		public GenericSetup genericSetup;
		
		// Logs
		protected ExperimentLog expLog;

		
		// Cosas que van mas alla de la interfaz pero que se diferencian en cada experimento
		protected abstract void specificInitLevel(); // Se ejecuta cuando se inicial un nivel especifico (la idea es que aca se inicialicen todas las variables dependientes del nivel)
		protected abstract String getNameTag();
		protected abstract void sendDataLevel();
		
		public void initLevel(Level level) {
			this.level = level;
			this.expLog = new ExperimentLog();
			this.expLog.levelInstance = TimeUtils.millis();
			this.expLog.session = this.session;
			this.expLog.expName = this.getName();
			// Creamos el enviable
			Internet.addDataToSend(this.expLog, TIPO_ENVIO.NEWLEVEL, this.getNameTag());
			this.levelCompleted = false;
			this.specificInitLevel();
		}

		public Array<LevelStatus> levelsStatus() {
			return this.expSettings.levels;
		}
		
		public void initGame(Session session) {
			// Cargamos la info del experimento
			if (!Gdx.files.local(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings").exists()) { // hacemos una copia de la info guardada en internal
				FileHandle from = Gdx.files.internal(Resources.Paths.InternalResources + this.getClass().getSimpleName() + ".settings");
				FileHandle to = Gdx.files.local(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings");
				from.copyTo(to);
			}
			String savedData = FileHelper.readLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings");
			Json json = new Json();
			this.expSettings = json.fromJson(Experiments.ExpSettings.class, savedData);
			this.session = session;
		}
		
		public boolean islevelCompleted() {
			return this.levelCompleted;
		}
		
		public void levelCompleted() {
			for (LevelStatus levelStatus : this.expSettings.levels) {
				if (levelStatus.id == this.level.Id) {
					levelStatus.alreadyPlayed = true;
				}
			}
			Json json = new Json();
			FileHelper.writeLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings", json.toJson(this.expSettings));
			this.sendDataLevel();
		}
	}
}

