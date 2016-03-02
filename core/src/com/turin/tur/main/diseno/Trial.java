package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Boxes.TestBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;

public class Trial {

	public static final String TAG = Trial.class.getName();

	public JsonTrial jsonTrial; // Toda la info del json del trial
	private Array<ExperimentalObject> elementos = new Array<ExperimentalObject>(); // Esto debe ser cargado antes de ejecutarse la creacion de los elementos
	public RunningSound runningSound; // Maneja el sonido
	private int Id; // Id q identifica al trial 
	private ExperimentalObject estimulo;
	public Array<TrainingBox> trainigBoxes = new Array<TrainingBox>();
	public Array<TestBox> testBoxes = new Array<TestBox>();
	public StimuliBox stimuliBox;
	public Array<Box> allBox = new Array<Box>();
	private boolean alreadyAsked = false; // almacena si ya se respondio el trial o no 
	private boolean lastAnswerCorrect; // Guarda si se marco la opcion correcta en un trial en caso de que corresponda
	
	public Trial (Array<ExperimentalObject> elementos, JsonTrial jsonTrial, LevelAsset asset, ExperimentalObject estimulo) {
		this.elementos = elementos;
		this.jsonTrial = jsonTrial;
		this.runningSound = new RunningSound(asset);
		this.estimulo = estimulo;
		this.configureElements();
	}
	
	public void configureElements () {
		Array<Integer> orden = new Array<Integer>();
		// Crea un orden random o no segun corresponda
		for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
			orden.add(i);
		}
		if (this.jsonTrial.randomSort) {
			orden.shuffle();
		}
		// Crea las cajas segun corresponda a su tipo
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))),
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.trainigBoxes.add(box);
			}
		}
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.TEST){
			for (ExperimentalObject elemento : this.elementos) {
				TestBox box = new TestBox(elemento,this.jsonTrial.feedback);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))) + Constants.Box.SHIFT_MODO_SELECCIONAR,
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.testBoxes.add(box);
			}
			// Crea el box de estimulo
			stimuliBox = new StimuliBox(estimulo);
			stimuliBox.SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			allBox.add(stimuliBox);
		}
		// Junta todas las cajas en una unica lista para que funcionen los
		// update, etc.
		for (Box box : testBoxes) {
			allBox.add(box);
		}
		for (Box box : trainigBoxes) {
			allBox.add(box);
		}
	}
	
	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime, this);
		}
	}
	
	/**
	 * Esta funcion devuelve si el trial esta completo o no. En caso de ser un trial de entrenamiento se asume que esta completo cuando todos los boxes se tocaron.
	 * Si no es un trial de entrenamiento simplemente se fija si la marca del trial completo
	 * @return
	 */
	public boolean checkTrialCompleted() { // Se encarga de ver si ya se completo trial o no
		// nos fijamos si hay feedback actuando
		if (this.jsonTrial.modo == TIPOdeTRIAL.ENTRENAMIENTO) {
			if (this.runningSound.running) {
				return false;
			}
		}
		if (this.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			for (TestBox box: this.testBoxes) {
				if (box.givinFeedback) {
					return false;
				}
			}
		}
		// En caso de que no haya feedback nos fijamos si se cumplio el objetivo del trial.
		/*
		if (this.jsonTrial.modo == TIPOdeTRIAL.ENTRENAMIENTO) {
			boolean allCheck = true;
			for (TrainingBox box : trainigBoxes) {
				if (box.alreadySelected == false) {
					allCheck = false;
				}
			}
			if (allCheck) {
				return true;
			} else {
				return false;
			}
		} 
		if (this.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			
		}
		*/
		if (this.alreadyAsked) {
			return true;
		} else {
			return false;
		}
	}

	
	public static class JsonTrial {
		public String caption; // Texto que se muestra debajo
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones
														// de los elementos a
														// mostrar
		public boolean feedback=false; // Sirve para configurar que en algunos test no haya feedback
		public boolean randomSort;
		public int resourceVersion;
		// public String identificador;
		// public ParametrosSetupParalelismo parametrosParalelismo;
		// public JsonResourcesMetaData jsonEstimulo;
		
	}

	
	public static class RunningSound {
		
		private static final String TAG = RunningSound.class.getName();
		
		public ExperimentalObject contenido; // Todo el objeto que se esta reproduciendo
		public Sound sound; // Elemento de sonido
		public boolean running = false; // Si se esta reproduciendo o no
		//public float start = -1; // Cuando comienza la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
		//public float ends = -1; // Cuando termina la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
		public int id; // El id que identifica el recurso del ultimo sonido
		public long instance; // instancia que identifica cada reproduccion unequivocamente
		// public Array<Integer> secuenceId = new Array<Integer>(); // secuencia de los sonidos reproducidos.
		// public SoundLog soundLog = new SoundLog();
		// public String stopReason = "";
		private LevelAsset assets;
		
		// Info para el update
		public NEXT action = NEXT.NADA;
		public float playTime; 
		public ExperimentalObject nextContenido;
		

		public RunningSound (LevelAsset assets) {
			this.assets = assets;
		}
		
		public void update(float deltaTime) {
			if (this.running) {
				this.playTime = this.playTime + deltaTime;
			} 
			if (this.playTime > Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA) {
				this.stop();
			}
			if (this.action == NEXT.PLAY) {
				if (Gdx.graphics.getFramesPerSecond()>40) {
					if (nextContenido!=null) {
						this.play();
						this.playTime =0;
						this.action = NEXT.NADA;
					}
				}
			}
		}
		
		public void play() {
			// Primer detiene cualquier reproduccion previa 
			if (running) {
				stop();
			}
			// Prepara la info en la clase
			contenido = nextContenido;
			id = contenido.resourceId.id;
			instance = TimeUtils.millis();

			// Cargamos el sonido
			this.sound = this.assets.sound(this.id);
			this.sound.play(); 
			this.running = true;
		}

		public void stop() {
			if (running) {
				
				// Detiene el sonido
				sound.stop();
				running = false;
			}
		}
		
		public enum NEXT {
			PLAY,STOP,NADA;
		}
	}
	
	
	// Info heredada
	// private LevelAsset levelAssets;
	// private int levelId;
	
	// Info del sonido
	
	// objetos que se cargan en el load o al inicializar
	// public Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
	// public Array<Integer> orden = new Array<Integer>();


	// Variable que tiene que ver con el estado del trial
	// public boolean alreadySelected = false; // indica si ya se elecciono algo o no
	
	// Variables que llevan el registro
	// public TrialLog log;
	
	
	// constantes
	
	/*
	public void loadTrialElements() {
		
		// Carga la info a partir de los Ids
		for (int elemento : this.jsonTrial.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento, this.levelAssets, this.levelId));
		}
		
		boolean rtaEntreOpciones = false;
		for (int i: this.jsonTrial.elementosId) {
			if (this.jsonTrial.rtaCorrectaId == i){
				rtaEntreOpciones = true;
			}
		}
		if ((this.jsonTrial.rtaRandom) && (rtaEntreOpciones)){ // Pone una random solo si esta seteada como random y la rta esta entre las figuras
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.elementosId[MathUtils.random(this.jsonTrial.elementosId.length-1)], this.levelAssets, level);
		} else {
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.rtaCorrectaId, this.levelAssets, level);
		}
		
		if (new ExperimentalObject(this.jsonTrial.rtaCorrectaId, this.levelAssets, level).categorias.contains(Categorias.Nada, false)) { // Pone si o si una respuesta random si la rta es nada.
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.elementosId[MathUtils.random(this.jsonTrial.elementosId.length-1)], this.levelAssets, level);
		}
		// Crea el log que se carga en el controller
		// this.log = new TrialLog();
	}
	
	
	
	
	
	
	
	
	
	
	/*
	public Trial(int Id, int levelId, LevelAsset levelAssets) {
		// this.levelId = levelId;
		// this.Id = Id;
		// this.levelAssets = levelAssets;
		initTrial(Id);
		createElements();
	}
	*/

		// Seccion encargada de guardar y cargar info de trials

	// devuelve la info de la metadata

	/*
	public static class ParametrosSetupParalelismo {
		public int R;
		public int D;
	}
	*/
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	// Seccion de logs
	public static class TouchLog {
		// Todas las cosas se deberian generar al mismo tiempo
		public long touchInstance; // Instancia que identyifica a cada toque
		public long trialInstance; // Instancia q identifica al trial en el cual se toco
		public int trialId; // Id del trial en el que se toco
		public ResourceId idResourceTouched; // Id del recurso que se toco
		public Array<Categorias> categorias = new Array<Categorias>(); // Lista de categorias a las que pertenece el elemento tocado
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial (entrenamiento test, etc)
		public boolean isTrue; // Indica si se toco el recurso que era la respuesta
		public boolean isStimuli; // indica si el recurso tocado es el estimulo (en general se lo toca para que se reproduzca)
		public float timeSinceTrialStarts; // Tiempo desde que se muestra el trial
		public long soundInstance; // Intancia del ultimo sonido en ejecucion
		public boolean soundRunning; // indica si se esta ejecutando algun sonido
		public float timeLastStartSound; // Tiempo (en el trial) en que comenzo el ultimo sonido 
		public float timeLastStopSound; // Tiempo (en el trial en que terimo el ultimo sonido 
		public int numberOfSoundLoops; // Cantidad de veces que re reprodujo el ultimo sonido
		public Array<Integer> soundIdSecuenceInTrial; // Ids de todos los sonidos que se reprodujeron en el trial
		public long levelInstance; // Registra el level en el que se toco
		public long sessionInstance; // Registra la session en que se toco
		public JsonResourcesMetaData jsonMetaDataTouched; // Guarda la info completa de la meta data del objeto tocado
	}
	*/
	/*
	public static class SoundLog {
		// Variables que se crean con el evento
		public long soundInstance; // identificador de la instancia de sonido en particular
		public ResourceId soundId; // Id al recurso del cual se escucha el sonido
		public Array<Categorias> categorias = new Array<Categorias>(); // Categorias a las que pertenece el sonido en reproduccion
		public long trialInstance; // instancia del Trial en la que se reproduce el sonido
		public int trialId; // Id del trial en el que se reproduce el sonido
		public boolean fromStimuli; // Indica si el sonido viene de un estimulo o no (sino viene de una caja de entrenamiento)
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial en el que se reproduce este sonido
		public int numberOfLoop; // Numero de loop que corresponde a la reproduccion de este sonido
		public float startTimeSinceTrial;  // tiempo en que se inicia la reproduccion del sonido desde que comenzo el trial
		public int numberOfSoundInTrial; // Cantidad de sonidos reproducidos previamente
		public Array<Integer> soundSecuenceInTrial; // Listado de sonidos reproducidos
		// Variables que se generan una vez creado el evento
		public float stopTime; // tiempo en que se detiene el sonido 
		public boolean stopByExit; // Indica si se detuvo el sonido porque se inicio alguna secuencia de cierre del trial (porque se completo el trial, el level, etc)
		public boolean stopByUnselect; // Indica si se detuvo el sonido porque el usuario selecciono algo como parte de la dinamica del juego
		public boolean stopByEnd; // Indica si el sonido se detuvo porque se completo la reproduccion prevista (por ahora esta determinada por el tiempo preestablecido de duracion de los sonidos en 5s. No es el tiempo en que de verdad termina el sonido)
		public long sessionInstance; // Indica la instancia de session en que se reproduce este sonido
		public long levelInstance; // Indica la instancia de level en que se reproduce este sonido
	}
	*/
	
	/*
	public static class TrialLog {
		// Info del envio
		public STATUS status=STATUS.CREADO;
		public long idEnvio;
		
		// Info de arbol del evento 
		public long sessionId; // Instancia de la session a la que este trial pertence
		public long levelInstance; // Intancia del nivel al que este trial pertenece
		public long trialInstance; // identificador de la instancia de este trial.
		// Info del usuario y del trial
		public int trialId; // Id del trial activo
		public long userId; // Id del usuario activo
		public Array<Categorias> categoriasElementos = new Array<Categorias>(); // Listado de categorias existentes en este trial
		public Array<Categorias> categoriasRta = new Array<Categorias>(); // Listado de categorias a las que pertenece la rta valida / estimulo de este trial si la hay
		public ResourceId idRtaCorrecta; // id del recurso correspondiente a la rta correcta para este trial
		public int indexOfTrialInLevel; // posicion de este trial dentro del nivel
		public int trialsInLevel; // Cantidad total de trials en el nivel activo
		public JsonResourcesMetaData jsonMetaDataRta; // Info de la metadata del estimulo/rta  
		
		public long timeTrialStart; // Marca temporal absoluta de cuando se inicia el trial
		public long timeExitTrial; // Marca temporal absoluta de cuando se sale del trial
		public Array<Integer> resourcesIdSort = new Array<Integer>(); // Ids de los recursos en orden segun se completan en la distribucion. Esto es importante porque el orden puede estar randomizado instancia a instancia
		public DISTRIBUCIONESenPANTALLA distribucionEnPantalla; // Distribucion en pantalla de los recursos
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial (test, entrnamiento, etc)
		public float version = Constants.VERSION; // Version del programa. Esto es super importante porque de version a version pueden cambiar los recursos (es decir que el mismo id lleve a otro recurso) y tambien otras cosas como la distribucion en pantalla, etc
		public float resourcesVersion = Builder.ResourceVersion; // Version de los recursos generados

		// Informacion de lo que sucede durante la interaccion del usuario

		// public float timeStartTrialInLevel; // Tiempo en que se crea el trial en relacion al nivel  
		public float timeStopTrialInLevel; // Tiempo en que se termina el trial en relacion al nivel
		public float timeInTrial; // tiempo transcurrido dentro del trial
		public boolean trialCompleted; //Por ahora solo se puede completar un trial en modo training. En modo test no tiene sentido completar el nivel. Este dato se carga de cuando sehace el checkTrialCompleted 
		public Array<ResourceId> resourcesIdSelected = new Array<ResourceId>(); // Lista de elementos seleccionados
		public Array<TouchLog> touchLog = new Array<TouchLog>(); // Secuencia de la info detallada de todos los touch
		public Array<SoundLog> soundLog = new Array<SoundLog>(); // Secuencia de la info detallada de todos los sounds
		public boolean trialExitRecorded; // registra que se guardo la informacion de salida del trial. 
		public String trialTitle;
		public JsonTrial jsonTrial; // Json con toda la info que viene del archivo con los datos del trial
		
		public TrialLog() {
			this.trialInstance = TimeUtils.millis();
		}		
	}
	*/
	
	public static class ResourceId {
		public int id;
		public int resourceVersion;
	}


	public boolean lastAnswer() {
		return this.lastAnswerCorrect;
	}

	/*
	 * Metodo que se ejecuta al tocar un box (ya sea Entrenamiento o Test)
	 */
	public void boxSelected(Box boxTocada) {
		/*
		if (boxTocada.getClass() == TestBox.class) {
			// Revisamos que onda si se acerto o no. Acertar significa que el elemento tocado y el estimulo compartan al menos una categoria.
			boolean answerCorrect = false;
			for (CategoriasImagenes categoria : boxTocada.contenido.categorias) {
				if (this.estimulo.categorias.contains(categoria, false)) {
					answerCorrect = true;
				}
			}
			boxTocada.select(this);
			this.lastAnswerCorrect = answerCorrect;
			this.alreadyAsked = true;
		}
		
		if (boxTocada.getClass() == TrainingBox.class) {
			for (TrainingBox box : this.trainigBoxes) {
				box.unSelect(this);
			}
			boxTocada.select(this);
		}
		*/
		// Revisamos que onda si se acerto o no. Acertar significa que el elemento tocado y el estimulo compartan al menos una categoria.
		boolean answerCorrect = false;
		for (CategoriasImagenes categoria : boxTocada.contenido.categorias) {
			if (this.estimulo.categorias.contains(categoria, false)) {
				answerCorrect = true;
			}
		}
		boxTocada.select(this);
		this.lastAnswerCorrect = answerCorrect;
		this.alreadyAsked = true;
	}

	public void exit() {
		this.runningSound.stop();
	}
	
	/*
	public void newLog(Session session, Level levelInfo) {
		// Carga la info general del contexto
		this.log.levelInstance = levelInfo.levelLog.levelInstance;
		this.log.sessionId = session.sessionLog.id;
		this.log.timeTrialStart = TimeUtils.millis();  
		this.log.trialId = this.Id;
		this.log.trialTitle = this.jsonTrial.title;
		this.log.userId = session.sessionLog.userID;
		
		// Agrega las categorias de la rta correcta o estimulo
		if (this.rtaCorrecta!=null) {
			for (Categorias categoria: this.rtaCorrecta.categorias) {
				this.log.categoriasRta.add(categoria);
			}
			// Agrega el json de la rta correcta/estimulo
			this.log.jsonMetaDataRta = JsonResourcesMetaData.Load(this.rtaCorrecta.resourceId.id, this.levelId);
		}
		
		// Agrega las categorias de todas las cajas
		for (Box box: this.allBox) {
			for (Categorias categoria: box.contenido.categorias){
				this.log.categoriasElementos.add(categoria);
			}
		}
		this.log.idRtaCorrecta = this.rtaCorrecta.resourceId;
		this.log.indexOfTrialInLevel = levelInfo.activeTrialPosition;
		this.log.trialsInLevel = levelInfo.secuenciaTrailsId.size;
		// Recupera los Id de los recursos en el orden que estan en pantalla
		for (int orden : this.orden) {
			this.log.resourcesIdSort.add(this.elementos.get(orden).resourceId.id); // Recupera los ids de los recursos segun el orden en que esten
		}
		this.log.distribucionEnPantalla = this.jsonTrial.distribucion;
		this.log.tipoDeTrial = this.jsonTrial.modo;
		this.log.jsonTrial = this.jsonTrial;
		this.log.jsonMetaDataRta = JsonResourcesMetaData.Load(this.rtaCorrecta.resourceId.id, this.levelId);
	}
	*/
}
