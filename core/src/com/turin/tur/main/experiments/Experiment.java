package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Session.SessionLog;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;

public interface Experiment {
	// Cosas offline
	String getName();
	void makeResources(); // Se encarga de crear los recuros
	void makeLevels(); // Se encarga de armar la estructura de niveles
	// Cosas online de control de flujo de informacion
	Trial getTrial(); // Devuelve el proximo trial a usar
	// void createTrial();
	void returnAnswer (boolean answer); // Le indica al experimento como salio el trial
	void initGame (Session session); // Se ejecuta cuando se inicial el juego (la idea es que aca se inicialicen todas las variables generales.
	void initLevel(Level level); // Se ejecuta cuando se inicial un nivel especifico (la idea es que aca se inicialicen todas las variables dependientes del nivel)
	void stopLevel(); // Se ejecuta cuando se detiene un nivel (la idea es que aca se generen todos los logs y se envien al servidor)
	// Cosas de interfaz general
	Array<LevelStatus> levelsStatus(); // Pasa la info para que la pantalla menu pueda armarse como corresponde
	boolean askNoMoreTrials(); // Indica si se completo el nivel o no.
	void interrupt(); // Sirve para ejecutar acciones cuando se interrumpe el experimento porque el usuario decide salir del nivel
	int trialsLeft(); // Indica cuantos trials quedan como maximo (sievr para mostrar indicadores en la interfaz
	boolean islevelCompleted();
	
	public abstract class GenericExp implements Experiment {
		
		// Cosas generales
		protected String expName = "Umbral Generico";
		protected ExpSettings expSettings;
		
		// Cosas que manejan la dinamica en cada ejecucion
		protected Level level;
		protected Session session;
		protected Trial trial;
		protected LevelAsset assets;

		// Logs
		protected SessionLog sessionLog;
		protected boolean levelCompleted;

		protected void event_initLevel() {
			this.sessionLog.levelInstance = TimeUtils.millis();
			// Creamos el enviable
			Internet.sendData(this.sessionLog, TIPO_ENVIO.NEWLEVEL, this.getNameTag());
			this.createTrial();
		}

		
		abstract void createTrial();

		abstract String getNameTag();
		
		protected void event_initGame() {
			// Creamos el log
			this.sessionLog = new SessionLog();
			this.sessionLog.session = this.session;
			this.sessionLog.expName = this.getName();
			// Creamos el enviable
			Internet.sendData(this.sessionLog, TIPO_ENVIO.NEWSESION, this.getNameTag());
		}

		public Array<LevelStatus> levelsStatus() {
			return this.expSettings.levels;
		}
		
		public void initGame(Session session) {
			// Cargamos la info del experimento
			// Gdx.app.debug(TAG, Resources.Paths.resources + this.getClass().getSimpleName() + ".settings");
			String savedData = FileHelper.readFile(Resources.Paths.resources + this.getClass().getSimpleName() + ".settings");
			Json json = new Json();
			this.expSettings = json.fromJson(Experiments.ExpSettings.class, savedData);
			this.session = session;
			this.event_initGame();
		}

		public abstract String getName();
		
		public Trial getTrial() {
			return this.trial;
		}
		
		public boolean islevelCompleted() {
			return this.levelCompleted;
		}
	}
}

