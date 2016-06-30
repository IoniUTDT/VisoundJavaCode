package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.LevelOLD;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.experiments.Experiments.ExperimentLog;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;

public interface Experiment {
	// Cosas offline
	String getExpName();
	String getLevelName();
	void saveSetupExp();
	void loadSetupExp();
	void saveLevelSetup();
	void loadLevelSetup();
	
	void makeResources(); // Se encarga de crear los recuros
	void makeLevels(); // Se encarga de armar la estructura de niveles
	// Cosas online de control de flujo de informacion
	Trial getNextTrial(); // Devuelve el proximo trial a usar
	void returnAnswer (boolean answer, float confianza, float selectionTime, float confianceTime, int soundLoops); // Le indica al experimento como salio el trial
	void initGame (Session session); // Se ejecuta cuando se inicial el juego (la idea es que aca se inicialicen todas las variables generales.
	void initLevel (LevelOLD level); // Inicia cuestiones generales del nivel
	void levelCompleted(); // Se ejecuta cuando se detiene un nivel (la idea es que aca se generen todos los logs y se envien al servidor)
	boolean islevelCompleted (); // Devuelve la variable levelCompleted (hay que hacerlo asi porque sino no esta en la interfaz
	// Cosas de interfaz general
	void interrupt(); // Sirve para ejecutar acciones cuando se interrumpe el experimento porque el usuario decide salir del nivel
	int trialsLeft(); // Indica cuantos trials quedan como maximo (sievr para mostrar indicadores en la interfaz
	boolean goConfiance(); // Marca si tiene que ir a preguntar la confianza o no.
	void specificInitLevel();
	void sendDataLevel();

	
	public abstract class GenericExp implements Experiment {

		// Aca creamos las variables que son comunes a todos los experimentos
		final static String TAG = GenericExp.class.getName();

		// Cosas generales
		public Array<LevelStatus> levelsStatus = new Array<LevelStatus>();
		
		// Cosas que manejan la dinamica en cada ejecucion
		protected LevelOLD level;
		protected Session session;
		protected Trial trial;
		public boolean levelCompleted;
		
		// Logs
		protected ExperimentLog expLog;

		
		
		
		
		// Aca van los metodos que son comunes a todos los experimentos
		public void initLevel(LevelOLD level) {
			this.level = level;
			this.expLog = new ExperimentLog();
			this.expLog.levelInstance = TimeUtils.millis();
			this.expLog.session = this.session;
			this.expLog.expName = this.getExpName();
			this.expLog.levelName = this.getLevelName();
			// Hacemos un envio
			Internet.addDataToSend(this.expLog, TIPO_ENVIO.NEWLEVEL, this.getLevelName());
			this.levelCompleted = false;
			this.specificInitLevel();
		}

		public void initGame(Session session) {
			// Si no hay una copia del estatus de los niveles en la carpeta local (editable) creamos una copia desde la carpeta interna
			if (!Gdx.files.local(Resources.Paths.LocalSettingsCopy + this.getLevelName() + ".settings").exists()) { // hacemos una copia de la info guardada en internal
				FileHandle from = Gdx.files.internal(Resources.Paths.InternalResources + this.getClass().getSimpleName() + ".settings");
				FileHandle to = Gdx.files.local(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings");
				from.copyTo(to);
			}
			String savedData = FileHelper.readLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings");
			Json json = new Json();
			this.levelsStatus = json.fromJson(this.levelsStatus.getClass(), savedData);
			this.session = session;
		}
		
		public boolean islevelCompleted() {
			return this.levelCompleted;
		}
		
		public void levelCompleted() {
			for (LevelStatus levelStatus : this.levelsStatus) {
				if (levelStatus.id == this.level.Id) {
					levelStatus.alreadyPlayed = true;
				}
			}
			Json json = new Json();
			FileHelper.writeLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings", json.toJson(this.levelsStatus));
			this.sendDataLevel();
		}
	}
}

