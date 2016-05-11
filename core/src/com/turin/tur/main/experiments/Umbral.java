package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.experiments.Experiments.ExperimentLog;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public abstract class Umbral extends GenericExp {

	static final String TAG = Umbral.class.getName();
	
	static class LogConvergencia {
		ExperimentLog expLog; 
		DinamicaExperimento dinamica; 
	}

	/**
	 * Esta clase regula la dinamica del experimento y guarda toda la info
	 * necesaria para tomar desiciones acerca de que trial seleccionar o si
	 * continuar el experimento o terminarlo
	 * 
	 * @author ionatan
	 *
	 */
	public static class DinamicaExperimento {
		String identificador; // Algo para indentificar cual convergencia es cual.
		int nivelEstimulo; // nivel de proxima señal a enviar
		int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		//boolean convergenciaAlcanzada = false;
		boolean convergenciaFinalizada = false;
		int trialsInSerie;
		Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
		//float ultimaSD;
		//float ultimoMEAN;
		int proporcionAciertos = 2; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		// private int proporcionTotal = 3; // Es el numero de elementos a revisar en el historial en busca de la cantidad de acierto para definir si se aumenta la dificultad o no
		//int tamanoVentanaAnalisisConvergencia = 6;
		//float sdEsperada = 0.5f;
		double referencia;
	}

	static class Estimulo implements Comparable<Estimulo> {
		int idResource; // Id del archivo con el recurso
		int idTrial; // Id del trial en que se evalua al recurso
		double referencia; // Angulo de inclinacion de las rectas paralelas de
							// referencia
		double desviacion; // Desviacion respecto a la referencia
		int nivelSenal; // Nivel de intensidad de la señal en escala lineal
						// (cada estimulo representa un paso) dentro del nivel

		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	static class Respuesta {
		private Estimulo estimulo;
		boolean acertado;
		float confianza;
		Respuesta (Estimulo estimulo, Boolean rta, float confianza) {
			this.estimulo = estimulo;
			this.acertado = rta;
			this.confianza = confianza;
		}
	}
	
	static class ImageInfo {
		Linea linea1 = new Linea();
		Linea linea2 = new Linea();
		double referencia;
		double desviacion;
	}
	
	static class Setup {
		Array<Double> angulosReferencia = new Array<Double>();
		Array<Double> desviacionesAngulares = new Array<Double>();
		Array<Estimulo> estimulos = new Array<Estimulo>();
		public int numeroDeTrailsMaximosxDinamica;
		public int levelPriority;
		public String tagButton;
		public boolean feedback;
	}
	
	// Cosas generales
	protected Setup setup;
	
	// Cosas que manejan la dinamica en cada ejecucion
	protected Array<DinamicaExperimento> dinamicas;
	protected DinamicaExperimento dinamicaActiva;
	// protected boolean waitingAnswer;
	protected Estimulo estimuloActivo;
		
	
	// Funciones comunes a todos los experimentos de umbral
	public boolean askNoMoreTrials() {
		if (this.trialsLeft() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private void levelCompletedAction() {
		for (LevelStatus levelStatus : this.expSettings.levels) {
			if (levelStatus.id == this.level.Id) {
				levelStatus.alreadyPlayed = true;
			}
		}
		Json json = new Json();
		FileHelper.writeLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings", json.toJson(this.expSettings));
	}
	
	public int trialsLeft() {
		int pendientes = 0;
		for (DinamicaExperimento dinamica : this.dinamicas) {
			pendientes = pendientes + dinamica.trialsInSerie - dinamica.historial.size;
		}
		return pendientes;
	}

	public abstract String getName();

	protected void event_stopLevel () {
		for (DinamicaExperimento dinamica : this.dinamicas) {
			LogConvergencia log = new LogConvergencia();
			log.expLog = this.expLog;
			log.dinamica = dinamica;
			log.dinamica.listaEstimulos.clear();
			// Creamos el enviable
			Internet.addDataToSend(log, TIPO_ENVIO.CONVERGENCIA, this.getNameTag());
		}
	}
	
	public void interrupt() {
		this.event_stopLevel();
	}

	public void initLevel(Level level) {
		// Cargamos los datos especificos del nivel
		this.level = level;
		this.dinamicas = (Array<DinamicaExperimento>) level.jsonLevel.infoDinamica;
		this.assets = new LevelAsset(level.Id);
		this.event_initLevel();
		this.createTrial();
	}
	
	public void stopLevel() {
		this.event_stopLevel();
	}
	
	public void createTrial() {
		// Seleccionamos una de las convergencias al azar.
		Array<DinamicaExperimento> forSelect = new Array<DinamicaExperimento>();
		for (DinamicaExperimento dinamica : this.dinamicas) {
			if (!dinamica.convergenciaFinalizada) {
				forSelect.add(dinamica);
			}
		}
		this.dinamicaActiva = forSelect.random();
		// Buscamos el trial que corresponde al nivel actual de la
		// convergencia (los cambios se actualizan cuando se recibe el
		// answer)
		this.estimuloActivo = this.dinamicaActiva.listaEstimulos.get(this.dinamicaActiva.nivelEstimulo);
		// leemos el json del trial
		String savedData = FileHelper.readInternalFile(Resources.Paths.InternalResources + "level" + level.Id + "/trial" + this.estimuloActivo.idTrial + ".meta");
		
		Json json = new Json();
		JsonTrial jsonTrial = json.fromJson(JsonTrial.class, savedData);
		// Cargamos la lista de objetos experimentales
		Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
		for (int idElemento : jsonTrial.elementosId) {
			ExperimentalObject elemento = new ExperimentalObject(idElemento, this.assets, level.Id);
			elementos.add(elemento);
		}
		ExperimentalObject estimulo = new ExperimentalObject(jsonTrial.rtaCorrectaId, this.assets, level.Id);
		// Con la info del json del trial tenemos que crear un trial y
		// cargarlo
		if (this.trial != null) {
			this.trial.exit();
		}
		this.trial = new Trial(elementos, jsonTrial, this.assets, estimulo);
	}
	
	public void returnAnswer(boolean answerIsCorrect, float confianza) {
		// Almacenamos en el historial lo que paso
		this.dinamicaActiva.historial.add(new Respuesta (this.estimuloActivo, answerIsCorrect, confianza));
		// Marcamos que se recibio una rta
		
		// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
		boolean incrementarDificultad=false;
		boolean disminuirDificultad=false;
		if (this.dinamicaActiva.historial.peek().acertado) { // Si se acerto y no hay suficiente historial se debe disminuir la dificultad, sino hay que revisar si la proporcion de aciertos requeridos esta cumpleida
			if (this.dinamicaActiva.historial.size >= this.dinamicaActiva.proporcionAciertos) { // Estamos en el caso en que hay que mirar el historial
				// Nos fijamos si hay algun desacierdo en los ultimos datos
				int contadorAciertos=0;
				for (int i=1; i<=(this.dinamicaActiva.proporcionAciertos); i++){
					if (this.dinamicaActiva.historial.get(this.dinamicaActiva.historial.size-i).acertado==true){
						contadorAciertos++;
					}
				}
				if (contadorAciertos>= this.dinamicaActiva.proporcionAciertos) {
					incrementarDificultad=true;
				}
			} else { // Si no hay historial suficiente
				incrementarDificultad=true;
			}
		} else { // Significa q hubo un desacierto en este caso siempre se disminuye la dificultad
			disminuirDificultad = true;
		}
		
		// Setea el salto entre nivel y nivel 
		this.dinamicaActiva.saltosActivos = (this.dinamicaActiva.trialsInSerie - 10) / 4;
		if (this.dinamicaActiva.saltosActivos < 1) { this.dinamicaActiva.saltosActivos = 1; }
		
		// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
		if (incrementarDificultad) {
			this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.nivelEstimulo-this.dinamicaActiva.saltosActivos;
			if (this.dinamicaActiva.nivelEstimulo<0) {this.dinamicaActiva.nivelEstimulo=0;}
		}
		if (disminuirDificultad) {
			this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.nivelEstimulo+this.dinamicaActiva.saltosActivos;
			if (this.dinamicaActiva.nivelEstimulo>this.dinamicaActiva.listaEstimulos.size-1) {this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.listaEstimulos.size-1;}
		}
		
		// Nos fijamos si ya se completo la dinamica o no.
		if (this.dinamicaActiva.historial.size == this.dinamicaActiva.trialsInSerie) {
			this.dinamicaActiva.convergenciaFinalizada=true;
		}
		
		
		// Una vez que se actualizo la dinamica anterior pasamos a actualizar el trial si corresponde
		if (this.askNoMoreTrials()) {
			this.levelCompletedAction();
			this.levelCompleted = true;
		} else {
			this.createTrial();
		}
	
	}
	
	protected abstract String getNameTag();
}
