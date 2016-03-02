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
	private boolean somethingTouched = false; // almacena si ya se respondio el trial o no 
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
		if (this.jsonTrial.modo == TIPOdeTRIAL.ENTRENAMIENTO) {
			/*
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
			*/
			if (this.lastAnswerCorrect) {
				return true;
			} else {
				return false;
			}
		} 
		if (this.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			if (this.somethingTouched) {
				return true;
			} else {
				return false;
			}
		}
		return false;
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
	}

	
	public static class RunningSound {
		
		private static final String TAG = RunningSound.class.getName();
		
		public ExperimentalObject contenido; // Todo el objeto que se esta reproduciendo
		public Sound sound; // Elemento de sonido
		public boolean running = false; // Si se esta reproduciendo o no
		public int id; // El id que identifica el recurso del ultimo sonido
		public long instance; // instancia que identifica cada reproduccion unequivocamente
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
	
		// Revisamos que onda si se acerto o no. Acertar significa que el elemento tocado y el estimulo compartan al menos una categoria.
		boolean answerCorrect = false;
		for (CategoriasImagenes categoria : boxTocada.contenido.categorias) {
			if (this.estimulo.categorias.contains(categoria, false)) {
				answerCorrect = true;
			}
		}
		boxTocada.select(this);
		this.lastAnswerCorrect = answerCorrect;
		this.somethingTouched = true;
	}

	public void exit() {
		this.runningSound.stop();
	}
}
