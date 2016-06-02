package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
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
		TRIAL_TYPE trialType; // Distinguimos si se trata de un trial que busca medir de verdad o si es un trial facil para verificar que el usuario esta entendiendo la consigna
		String identificador; // Algo para indentificar la dinamica
		int nivelEstimulo; // nivel de proxima señal a enviar
		int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		boolean levelFinalizadoCorrectamente = false;
		int trialsPorNivel;
		Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		Array<SerieEstimulos> seriesEstimulos = new Array<SerieEstimulos>();
		Array<Estimulo> estimulosCeros = new Array<Estimulo>();
		//float ultimaSD;
		//float ultimoMEAN;
		int proporcionAciertos = 2; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		// private int proporcionTotal = 3; // Es el numero de elementos a revisar en el historial en busca de la cantidad de acierto para definir si se aumenta la dificultad o no
		//int tamanoVentanaAnalisisConvergencia = 6;
		//float sdEsperada = 0.5f;
		double referencia;
		protected Estimulo estimuloActivo;
		
		public enum TRIAL_TYPE {
			TEST_EASY_Trial, REAL_TRIAL_CERO, REAL_TRIAL_ESTIMULO
		}
	}

	public static class SerieEstimulos {
		String identificador; // Algo para indentificar cual convergencia es cual.
		double ladoFijo;
		boolean desdeAgudosOPos;
		Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
	}
	
	static class Estimulo implements Comparable<Estimulo> {
		int idResource; // Id del archivo con el recurso
		int idTrial; // Id del trial en que se evalua al recurso (esto es porque 
		double anguloFijo; // Angulo de inclinacion de las rectas paralelas de
							// referencia
		double desviacion; // Desviacion respecto a la referencia
		int nivelSenal; // Nivel de intensidad de la señal. Cero representa el angulo recto o las rectas paralelas. Y despues representa una escala lineal que mapea las estimulos ordenados segun la intensidad del estimulo a medir (mas facil mayor intencidad)
		
		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	static class Respuesta {
		DinamicaExperimento.TRIAL_TYPE tipoDeTrial;
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
	
	public static class Setup {
		Array<Double> angulosReferencia = new Array<Double>(); // Referencias del experimento
		Array<Float> fluctuacionesLocalesReferenciaSeries = new Array<Float>(); // Fluctuaciones dentro de cada referencia, en terminos relativos
		Array<Double> desviacionesAngulares = new Array<Double>(); // Variaciones del lado movil o del angulo respecto a la referencia
		Array<Double> fluctuacionesLocalesReferenciaEstimuloCero = new Array<Double>(); // angulos en los cuales se muestra a señal recta. 
		Array<Estimulo> estimulos = new Array<Estimulo>(); // Lista de estimulos que se arman en la fse de generacion de recursos.
		public int trialsPorNivel; // Numero de trial que conforman un nivel
		public int levelPriority; // Prioridad que tiene el nivel en la lista de niveles. Sirve para habilitar a que se tenga que completar un nivel antes que otro.
		public String tagButton;
		public boolean feedback;
		public int testFraction = 5; // Representa la inversa del numero de test que se dedica a testear al usuario enviandole trials faciles.
		public int numeroDeEstimulosPorSerie;
		public int saltoInicialFraccion = 4;
		public int saltoColaUNOFraccion = 5;
		double desvMin;
		double desvMax;
		boolean logscale = true;
	}
	
	// Cosas generales
	protected Setup setup;
	
	// Cosas que manejan la dinamica en cada ejecucion
	// protected Array<DinamicaExperimento> dinamicas;
	// protected DinamicaExperimento dinamicaActiva;
	protected DinamicaExperimento dinamicaExperimento;
	// protected boolean waitingAnswer;
	
		
	
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
		// Hacemos un update a los datos guardados en disco
		Json json = new Json();
		FileHelper.writeLocalFile(Resources.Paths.LocalSettingsCopy + this.getClass().getSimpleName() + ".settings", json.toJson(this.expSettings));
	}
	
	public int trialsLeft() {
		/*
		int pendientes = 0;
		for (DinamicaExperimento dinamica : this.dinamicas) {
			pendientes = pendientes + dinamica.trialsInSerie - dinamica.historial.size;
		}
		return pendientes;
		*/
		return this.dinamicaExperimento.trialsPorNivel - this.dinamicaExperimento.historial.size;
	}

	public abstract String getName();

	protected void event_stopLevel () {
		/*
		for (DinamicaExperimento dinamica : this.dinamicas) {
			LogConvergencia log = new LogConvergencia();
			log.expLog = this.expLog;
			log.dinamica = dinamica;
			log.dinamica.listaEstimulos.clear();
			// Creamos el enviable
			Internet.addDataToSend(log, TIPO_ENVIO.CONVERGENCIA, this.getNameTag());
		}
		*/
		
		// Hacemos un send para la data del nivel que acaba de detenerse.
		LogConvergencia log = new LogConvergencia();
		log.expLog = this.expLog;
		log.dinamica = dinamicaExperimento;
		log.dinamica.seriesEstimulos.clear();
		// Creamos el enviable
		Internet.addDataToSend(log, TIPO_ENVIO.CONVERGENCIA, this.getNameTag());
	}
	
	public void interrupt() {
		this.event_stopLevel();
	}

	public void initLevel(Level level) {
		// Cargamos los datos especificos del nivel
		this.level = level;
		this.dinamicaExperimento = (DinamicaExperimento) level.jsonLevel.dinamicaExperimento;
		this.setup = level.jsonLevel.setup;
		this.assets = new LevelAsset(level.Id);
		this.event_initLevel();
		this.createTrial();
	}
	
	public void stopLevel() {
		this.event_stopLevel();
	}
	
	public void createTrial() {
		
		// Decide si manda una señal para medir de verdad o un test para probar al usuario
		if (MathUtils.random(this.setup.testFraction-1)==0) { // Caso en que se mande un test
			this.dinamicaExperimento.trialType = DinamicaExperimento.TRIAL_TYPE.TEST_EASY_Trial;
			int nivel = MathUtils.random(this.dinamicaExperimento.nivelEstimulo, this.setup.numeroDeEstimulosPorSerie-1);
			this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.seriesEstimulos.random().listaEstimulos.get(nivel);
		} else {
			if (MathUtils.randomBoolean()) {
				this.dinamicaExperimento.trialType = DinamicaExperimento.TRIAL_TYPE.REAL_TRIAL_ESTIMULO;
				this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.seriesEstimulos.random().listaEstimulos.get(this.dinamicaExperimento.nivelEstimulo);
			} else {
				this.dinamicaExperimento.trialType = DinamicaExperimento.TRIAL_TYPE.REAL_TRIAL_CERO;
				this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.estimulosCeros.random();
			}
		}
		
		/*  Codigo version vieja!
		
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
		*/
		
		// leemos el json del trial
		String savedData = FileHelper.readInternalFile(Resources.Paths.InternalResources + "level" + level.Id + "/trial" + this.dinamicaExperimento.estimuloActivo.idTrial + ".meta");
		
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
		this.dinamicaExperimento.historial.add(new Respuesta (this.dinamicaExperimento.estimuloActivo, answerIsCorrect, confianza));
		// Marcamos que se recibio una rta
		
		// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
		boolean incrementarDificultad=false;
		boolean disminuirDificultad=false;
		if (this.dinamicaExperimento.historial.peek().acertado) { 
			if (this.dinamicaExperimento.historial.size >= this.dinamicaExperimento.proporcionAciertos) { // Estamos en el caso en que hay que mirar el historial
				// Nos fijamos si hay algun desacierdo en los ultimos datos
				int contadorAciertos=0;
				for (int i=1; i<=(this.dinamicaExperimento.proporcionAciertos); i++){
					if (this.dinamicaExperimento.historial.get(this.dinamicaExperimento.historial.size-i).acertado==true){
						contadorAciertos++;
					}
				}
				if (contadorAciertos>= this.dinamicaExperimento.proporcionAciertos) {
					incrementarDificultad=true;
				}
			} else { // Si no hay historial suficiente
				incrementarDificultad=true;
			}
		} else { // Significa q hubo un desacierto en este caso siempre se disminuye la dificultad
			disminuirDificultad = true;
		}
		
		// Setea el salto entre nivel y nivel
		float avanceHastaUNOs = this.dinamicaExperimento.historial.size / this.setup.trialsPorNivel * (1 - 1/this.setup.saltoColaUNOFraccion);
		if (avanceHastaUNOs<1) {
			int saltoMaximo = this.setup.numeroDeEstimulosPorSerie/this.setup.saltoInicialFraccion;
			this.dinamicaExperimento.saltosActivos = MathUtils.ceil(saltoMaximo*avanceHastaUNOs);
		} else {
			this.dinamicaExperimento.saltosActivos = 1;
		}
		
		// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
		if (incrementarDificultad) {
			this.dinamicaExperimento.nivelEstimulo=this.dinamicaExperimento.nivelEstimulo-this.dinamicaExperimento.saltosActivos;
			if (this.dinamicaExperimento.nivelEstimulo<1) {this.dinamicaExperimento.nivelEstimulo=1;}
		}
		if (disminuirDificultad) {
			this.dinamicaExperimento.nivelEstimulo=this.dinamicaExperimento.nivelEstimulo+this.dinamicaExperimento.saltosActivos;
			if (this.dinamicaExperimento.nivelEstimulo>this.setup.numeroDeEstimulosPorSerie-1) {this.dinamicaExperimento.nivelEstimulo=this.setup.numeroDeEstimulosPorSerie-1;}
		}
		
		// Nos fijamos si ya se completo la dinamica o no.
		if (this.dinamicaExperimento.historial.size == this.setup.trialsPorNivel) {
			this.dinamicaExperimento.levelFinalizadoCorrectamente=true;
		}
		
		
		// Una vez que se actualizo la dinamica anterior pasamos a actualizar el trial si corresponde
		if (this.askNoMoreTrials()) {
			this.levelCompletedAction();
			this.levelCompleted = true;
		} else {
			this.createTrial();
		}
	
	}
	
	protected ArrayMap<Double, ArrayMap<Double, Estimulo>> indexToMap() {
		ArrayMap<Double, ArrayMap<Double, Estimulo>> map = new ArrayMap<Double, ArrayMap<Double, Estimulo>>();
		for (Estimulo estimulo : this.setup.estimulos) {
			if (!map.containsKey(estimulo.anguloFijo)) {
				map.put(estimulo.anguloFijo, new ArrayMap<Double, Estimulo>());
			}
			map.get(estimulo.anguloFijo).put(estimulo.desviacion, estimulo);
		}
		return map;
	}
	
	protected void generarDesviaciones (Setup setup) {
		// Generamos los lados moviles
		double desvMinLog = Math.log(setup.desvMin);
		double desvMaxLog = Math.log(setup.desvMax);
		Array<Double> desviaciones = new Array<Double>();
		// Creamos la serie de desviaciones en abstracto
		if (setup.logscale) {
			double paso = (desvMaxLog - desvMinLog) / (setup.numeroDeEstimulosPorSerie - 1);
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.add(desvMinLog + paso * i);
			}
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.set(i, Math.exp(desviaciones.get(i)));
			}
		} else {
			double paso = (setup.desvMax - setup.desvMin) / setup.numeroDeEstimulosPorSerie;
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.add(setup.desvMin + paso * i);
			}
		}
		// Armamos la serie completa
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			setup.desviacionesAngulares.add(desviacion);
		}
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			setup.desviacionesAngulares.add(-desviacion);
		}
	}
	
	@Override
	public void makeResources() {
		// Inicializamos el setup segun parametros
		this.makeSetup();
		// Creamos un recurso para cada imagen necesaria
		for (double referencia : this.setup.angulosReferencia) { // Asume que en esta variable estan los angulos de referencia
			for (double ladoFijo : this.setup.fluctuacionesLocalesReferenciaSeries) {
				ladoFijo = ladoFijo + referencia;
				for (double desviacion : this.setup.desviacionesAngulares) { // Asume que en esta variable estan los angulos a formar para cada referencia, siempre positivos
					makeResource(ladoFijo, desviacion);
				}
			}
		}
		// Guardamos el setup en la carpeta temporal
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(this.setup));
	}
	
	abstract void makeSetup();
	abstract void makeResource(double ladoFijo, double desviacion);
	
	protected abstract String getNameTag();
	
}
