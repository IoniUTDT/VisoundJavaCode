package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.experiments.Experiments.ExperimentLog;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
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
		TrialType trialType; // Distinguimos si se trata de un trial que busca medir de verdad o si es un trial facil para verificar que el usuario esta entendiendo la consigna
		String identificador; // Algo para indentificar la dinamica
		int nivelEstimulo; // nivel de proxima señal a enviar
		int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		boolean levelFinalizadoCorrectamente = false;
		int trialsPorNivel;
		Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		Array<SerieEstimulos> seriesEstimulos = new Array<SerieEstimulos>();
		Array<Estimulo> estimulosCeros = new Array<Estimulo>();
		int proporcionAciertos = 3; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		double referencia;
		protected Estimulo estimuloActivo;
		protected Array<TrialConfig> pseudorandom = new Array<TrialConfig>();

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
		private Estimulo estimulo;
		private boolean acertado;
		private float confianza;
		private TrialType trialType;
		private int nivelEstimulo;
		private float selectionTimeInTrial = -1;
		private float confianceTimeInTrial = -1;
		private int soundLoops = -1;
		Respuesta (Estimulo estimulo, Boolean rta, float confianza, TrialType trialType, int nivelEstimulo, float selectionTime, float confianceTime, int soundLoops) {
			this.estimulo = estimulo;
			this.acertado = rta;
			this.confianza = confianza;
			this.trialType = trialType;
			this.nivelEstimulo = nivelEstimulo;
			this.selectionTimeInTrial = selectionTime;
			this.confianceTimeInTrial = confianceTime;
			this.soundLoops = soundLoops;
		}
	}
	
	public static class ImageInfo {
		Linea linea1 = new Linea();
		Linea linea2 = new Linea();
		double referencia;
		double desviacion;
	}
	
	public static class SetupResources {
		public String SetupResourcesName; // Identifica el setup en cuestion
		Array<Double> angulosReferencia = new Array<Double>(); // Referencias del experimento
		Array<Float> fluctuacionesLocalesReferenciaSeries = new Array<Float>(); // Fluctuaciones dentro de cada referencia, en terminos relativos
		Array<Double> desviacionesAngulares = new Array<Double>(); // Variaciones del lado movil o del angulo respecto a la referencia
		Array<Double> fluctuacionesLocalesReferenciaEstimuloCero = new Array<Double>(); // angulos en los cuales se muestra a señal recta.
		Array<Estimulo> estimulos = new Array<Estimulo>(); // Lista de estimulos que se arman en la fase de generacion de recursos.
		public int numeroDeEstimulosPorSerie;
		double desvMin;
		double desvMax;
		boolean logscale = true;
	}
	
	public static class SetupLevel {
		public SetupResources setupResources;
		public double referencia;
		public String SetupLevelName; // Identifica el setup en cuestion
		public int trialsPorNivel; // Numero de trial que conforman un nivel
		public int levelPriority; // Prioridad que tiene el nivel en la lista de niveles. Sirve para habilitar a que se tenga que completar un nivel antes que otro.
		public String tagButton;
		public boolean feedback;
		public float testProbability = 0f; // Representa la inversa del numero de test que se dedica a testear al usuario enviandole trials faciles.
		public float signalProbability = 0.5f; // Esto tiene sentido que sea asi, mitad y mitad para que ande bien el sistema de medicion. No puede ser mas proibable una opcion que la otra (enm principio)
		public int saltoInicialFraccion = 4;
		public int saltoColaUNOFraccion = 2;
		boolean allTestsConfianza = true; // Esto esta condicionado a que testProbability sea diferente de cero en la generacion del pseudorandom
		public float confianceProbability = 0;
	}
	
	// Cosas generales
	protected Array<SetupLevel> setupsLevels = new Array<SetupLevel>();
	protected Array<SetupResources> setupsResources = new Array<SetupResources>();
	protected SetupLevel setupActivo;
	protected DinamicaExperimento dinamicaExperimento;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int trialsLeft() {
		return this.dinamicaExperimento.trialsPorNivel - this.dinamicaExperimento.historial.size;
	}

	protected void sendDataLevel () {	
		// Hacemos un send para la data del nivel que acaba de detenerse.
		LogConvergencia log = new LogConvergencia();
		log.expLog = this.expLog;
		log.dinamica = dinamicaExperimento;
		log.dinamica.seriesEstimulos.clear();
		// Creamos el enviable
		Internet.addDataToSend(log, TIPO_ENVIO.CONVERGENCIA, this.getLevelName());
	}
	
	public void interrupt() {
		this.sendDataLevel();
	}

	@Override
	protected void specificInitLevel() {
		// Cargamos los datos especificos del nivel
		this.dinamicaExperimento = (DinamicaExperimento) level.jsonLevel.dinamicaExperimento;
		this.setupActivo = level.jsonLevel.setupLevel;
		this.dinamicaExperimento.nivelEstimulo = this.setupActivo.setupResources.numeroDeEstimulosPorSerie - 1;
		this.makeSpeudoRandom();
	}
	
	public Trial getNextTrial() {
		// Obtiene lo que deberia pasar de la lista speudorando
		TrialConfig trialConfig = this.dinamicaExperimento.pseudorandom.get(this.dinamicaExperimento.historial.size);
		// Decide si manda una señal para medir de verdad o un test para probar al usuario
		if (trialConfig.trialType==TrialType.Test) { // Caso en que se mande un test
			this.dinamicaExperimento.trialType = TrialType.Test;
			int base = this.dinamicaExperimento.nivelEstimulo *2;
			if (base>this.setupActivo.setupResources.numeroDeEstimulosPorSerie-1 - this.setupActivo.setupResources.numeroDeEstimulosPorSerie/5) {
				base = this.setupActivo.setupResources.numeroDeEstimulosPorSerie-1 - this.setupActivo.setupResources.numeroDeEstimulosPorSerie/5;
			}
			int nivel = MathUtils.random(base, this.setupActivo.setupResources.numeroDeEstimulosPorSerie-1);
			this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.seriesEstimulos.random().listaEstimulos.get(nivel);
		}
		if (trialConfig.trialType==TrialType.Estimulo) {
			this.dinamicaExperimento.trialType = TrialType.Estimulo;
			this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.seriesEstimulos.random().listaEstimulos.get(this.dinamicaExperimento.nivelEstimulo);
		}
		if (trialConfig.trialType==TrialType.NoEstimulo) {
			this.dinamicaExperimento.trialType = TrialType.NoEstimulo;
			this.dinamicaExperimento.estimuloActivo = this.dinamicaExperimento.estimulosCeros.random();
		}
		
		// leemos el json del trial
		String savedData = FileHelper.readInternalFile(ResourcesCategorias.Paths.InternalResources + "level" + level.Id + "/trial" + this.dinamicaExperimento.estimuloActivo.idTrial + ".meta");
		
		Json json = new Json();
		JsonTrial jsonTrial = json.fromJson(JsonTrial.class, savedData);
		// Cargamos la lista de objetos experimentales
		Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
		for (int idElemento : jsonTrial.elementosId) {
			ExperimentalObject elemento = new ExperimentalObject(idElemento, this.level.levelAssets, level.Id);
			elementos.add(elemento);
		}
		ExperimentalObject estimulo = new ExperimentalObject(jsonTrial.rtaCorrectaId, this.level.levelAssets, level.Id);
		
		return new Trial(elementos, jsonTrial, estimulo);
	}
	
	public void returnAnswer(boolean answerIsCorrect, float confianza, float selectionTime, float confianceTime, int soundLoops) {
		// Almacenamos en el historial lo que paso
		this.dinamicaExperimento.historial.add(new Respuesta (this.dinamicaExperimento.estimuloActivo, answerIsCorrect, confianza, this.dinamicaExperimento.trialType, this.dinamicaExperimento.nivelEstimulo, selectionTime, confianceTime, soundLoops));
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
		float avanceHastaUNOs = (float) this.dinamicaExperimento.historial.size / (this.setupActivo.trialsPorNivel * (1 - 1f/this.setupActivo.saltoColaUNOFraccion));
		if (avanceHastaUNOs<1) {
			int saltoMaximo = this.setupActivo.setupResources.numeroDeEstimulosPorSerie/this.setupActivo.saltoInicialFraccion;
			this.dinamicaExperimento.saltosActivos = MathUtils.ceil(saltoMaximo*(1-avanceHastaUNOs));
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
			if (this.dinamicaExperimento.nivelEstimulo>this.setupActivo.setupResources.numeroDeEstimulosPorSerie-1) {this.dinamicaExperimento.nivelEstimulo=this.setupActivo.setupResources.numeroDeEstimulosPorSerie-1;}
		}
		
		// Nos fijamos si ya se completo la dinamica o no.
		if (this.trialsLeft() == 0) {
			this.dinamicaExperimento.levelFinalizadoCorrectamente=true;
			this.levelCompleted = true;
		}
	}
	
	
	
	protected class TrialConfig {
		boolean confiance;
		TrialType trialType;
	}
	
	protected enum TrialType {
		Test,Estimulo,NoEstimulo
	}
	
	protected void makeSpeudoRandom () {
		int numberOfEstimulo;
		int numberOfNoEstimulo;
		int numberOfTest;
		
		numberOfTest = (int) (this.setupActivo.trialsPorNivel*this.setupActivo.testProbability);
		if ((this.setupActivo.trialsPorNivel - numberOfTest) % 2 != 0) {
			Gdx.app.debug(TAG, "WARNING: El numero de trials a asignar en señal o no señal no es par y no quedara bien balanceado. Se agrega un trial test para equilibrar");
			numberOfTest ++;
		}
		numberOfEstimulo = ((this.setupActivo.trialsPorNivel - numberOfTest) / 2);
		numberOfNoEstimulo = ((this.setupActivo.trialsPorNivel - numberOfTest) / 2);
		
		
		Array <TrialConfig> trialsTest= new Array<TrialConfig> ();
		Array <TrialConfig> trialsEstimulo= new Array<TrialConfig> ();
		Array <TrialConfig> trialsNoEstimulo= new Array<TrialConfig> ();
		for (int i=0 ; i < numberOfTest; i++) {
			trialsTest.add(new TrialConfig());
		}
		for (int i=0 ; i < numberOfEstimulo; i++) {
			trialsEstimulo.add(new TrialConfig());
		}
		for (int i=0 ; i < numberOfNoEstimulo; i++) {
			trialsNoEstimulo.add(new TrialConfig());
		}
		
		int i;
		int confianceCount;
		
		i = 0;
		confianceCount = (int) (this.setupActivo.confianceProbability * trialsTest.size);
		if (this.setupActivo.allTestsConfianza) {
			for (TrialConfig test : trialsTest) {
				test.confiance = true;
				test.trialType = TrialType.Test;
			}
		} else {
			for (TrialConfig test : trialsTest) {
				i++;
				if (i<=confianceCount) {
					test.confiance = true;
				} else {
					test.confiance = false;
				}
				test.trialType = TrialType.Test;
			}
		}
		
		i = 0;
		confianceCount = (int) (this.setupActivo.confianceProbability * trialsEstimulo.size);
		for (TrialConfig test : trialsEstimulo) {
			i++;
			if (i<=confianceCount) {
				test.confiance = true;
			} else {
				test.confiance = false;
			}
			test.trialType = TrialType.Estimulo;
		}
		
		i = 0;
		confianceCount = (int) (this.setupActivo.confianceProbability * trialsNoEstimulo.size);
		for (TrialConfig test : trialsNoEstimulo) {
			i++;
			if (i<=confianceCount) {
				test.confiance = true;
			} else {
				test.confiance = false;
			}
			test.trialType = TrialType.NoEstimulo;
		}
		
		this.dinamicaExperimento.pseudorandom.addAll(trialsTest);
		this.dinamicaExperimento.pseudorandom.addAll(trialsEstimulo);
		this.dinamicaExperimento.pseudorandom.addAll(trialsNoEstimulo);
		this.dinamicaExperimento.pseudorandom.shuffle();
	}
	
	public boolean goConfiance() {
		return this.dinamicaExperimento.pseudorandom.get(this.dinamicaExperimento.historial.size).confiance;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Cosas relacionadas al makelevel
	protected ArrayMap<Double, ArrayMap<Double, Estimulo>> indexToMap(SetupResources setup) {
		ArrayMap<Double, ArrayMap<Double, Estimulo>> map = new ArrayMap<Double, ArrayMap<Double, Estimulo>>();
		for (Estimulo estimulo : setup.estimulos) {
			if (!map.containsKey(estimulo.anguloFijo)) {
				map.put(estimulo.anguloFijo, new ArrayMap<Double, Estimulo>());
			}
			map.get(estimulo.anguloFijo).put(estimulo.desviacion, estimulo);
		}
		return map;
	}
	
	protected void generarDesviaciones (SetupResources setup) {
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
		for (SetupResources setupResource : this.setupsResources) {
			// Creamos un recurso para cada imagen necesaria en las series de estimulo variable
			for (double referencia : setupResource.angulosReferencia) { // Asume que en esta variable estan los angulos de referencia
				for (double ladoFijo : setupResource.fluctuacionesLocalesReferenciaSeries) {
					ladoFijo = ladoFijo + referencia;
					for (double desviacion : setupResource.desviacionesAngulares) { // Asume que en esta variable estan los angulos a formar para cada referencia, siempre positivos
						setupResource.estimulos.add(makeResource(ladoFijo, desviacion));
					}
				}
			}
			// Creamos los recursos correspondientes a cada estimulo de nivel cero
			for (double referencia : setupResource.angulosReferencia) { // Asume que en esta variable estan los angulos de referencia
				for (double ladoFijo : setupResource.fluctuacionesLocalesReferenciaEstimuloCero) {
					ladoFijo = ladoFijo + referencia;
					setupResource.estimulos.add(makeResource(ladoFijo, this.getDesviacionCero()));
				}
			}
		}
		// Guardamos el setup en la carpeta temporal
		String path = ResourcesCategorias.Paths.ResourcesBuilder + ResourcesCategorias.Paths.ExtraFldr + this.getExpName() + ResourcesCategorias.Paths.ResourcesSetupExt;
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(this.setupsResources));
	}
	
	abstract String getExpName();
	abstract float getDesviacionCero();
	abstract void makeSetup();
	abstract Estimulo makeResource(double ladoFijo, double desviacion);
	
}
