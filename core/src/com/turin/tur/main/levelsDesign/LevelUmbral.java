package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Internet.TIPOdeENVIO;


public class LevelUmbral extends Level {

	public static final String TAG = LevelUmbral.class.getName();
	
	static class Dinamica {
		protected Estimulo estimuloActivo;
		protected Array<TrialConfig> pseudorandom = new Array<TrialConfig>();
		Array<Estimulo> estimulosCeros = new Array<Estimulo>();
		Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		LISTAdeNIVELES identificadorNivel; // Algo para indentificar la dinamica
		boolean levelFinalizadoCorrectamente = false;
		int nivelEstimulo; // nivel de proxima señal a enviar
		final int erroresUp = 1;
		final int aciertosDown = 2; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		final int rebotesActivarProporciones = 3; // Es la cantidad de rebotes que tiene que haber para que se active la proporcion de aciertos y errores, hasta este numero un acierto baja 
		int erroresInicialesAcumulados = 0; // Es la cantidad de rebotes acumulados
		double referencia;
		int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		Array<SerieEstimulos> seriesEstimulos = new Array<SerieEstimulos>();
		int trialsPorNivel;
		TrialType trialType; // Distinguimos si se trata de un trial que busca medir de verdad o si es un trial facil para verificar que el usuario esta entendiendo la consigna
		int aciertosAcumulados;
		int erroresAcumulados;
		TrialConfig trialConfig;
		boolean herenciaRecibida; // Determina si se heredo o no el nivel de señal del una experimento anterior.
		static int saltoUnitario = 1;
		static int saltoIntermedio = 3;
		int secuenciasAcumuladas=0;
		boolean ascendiendo=false;
		
		public static String pathNameExt = ".Dinamica";
		
		public static void saveDinamica (LISTAdeNIVELES identificador, Dinamica dinamica) {
			String path = pathLevelInfoBuild(identificador);
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(path, json.toJson(dinamica));
		}
		
		private static String pathLevelInfoLocal (LISTAdeNIVELES identificador) {
			return Level.folderResourcesLevelLocal(identificador) + identificador.toString() + pathNameExt;
		}
		
		private static String pathLevelInfoBuild (LISTAdeNIVELES identificador) {
			return Level.folderResourcesLevelBuild(identificador) + identificador.toString() + pathNameExt;
		}
		
		public static Dinamica loadDinamica (LISTAdeNIVELES identificador) {
			String savedData = FileHelper.readInternalFile(pathLevelInfoLocal(identificador));
			Json json = new Json();
			return json.fromJson(Dinamica.class, savedData);
		}
	}
	
	private static class Respuesta {
		private boolean acertado;
		private float confianceTimeInTrial = -1;
		private float confianza;
		private Estimulo estimulo;
		private int nivelEstimulo;
		private float selectionTimeInTrial = -1;
		private int soundLoops = -1;
		private TrialType trialType;
		
		public Respuesta () {
			
		}
		
		public Respuesta (Estimulo estimulo, Boolean rta, float confianza, TrialType trialType, int nivelEstimulo, float selectionTime, float confianceTime, int soundLoops) {
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

	static class SerieEstimulos {
		boolean desdeAgudosOPos;
		String identificador; // Algo para indentificar cual convergencia es cual.
		double ladoFijo;
		Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
	}

	static class SetupLevel {
		public float confianceProbability = 0;
		public boolean feedback;
		public LISTAdeNIVELES identificadorLevel;
		public float saltoGruesoFraccionNivel;
		public int saltoInicialFraccion;
		public float signalProbability = 0.5f; // Esto tiene sentido que sea asi, mitad y mitad para que ande bien el sistema de medicion. No puede ser mas proibable una opcion que la otra (enm principio)
		public float testProbability = 0f; // Representa la inversa del numero de test que se dedica a testear al usuario enviandole trials faciles.
		public int trialsPorNivel; // Numero de trial que conforman un nivel
		boolean allTestsConfianza = true; // Esto esta condicionado a que testProbability sea diferente de cero en la generacion del pseudorandom
		public double referencia; // Angulo de refrencia del nivel
		public boolean restartEstimulo; // Indica si hay que reiniciar el nivel de estimulo o se hereda del nivel anterior
		public int numeroDeEstimulosPorSerie;
		public int soundLoopLimit;
		
		public static String pathNameExt = ".LvlSetup";
		
		public static void saveInfoLevel (LISTAdeNIVELES identificador, SetupLevel setup) {
			String path = pathLevelInfoBuild(identificador);
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(path, json.toJson(setup));
		}
		
		private static String pathLevelInfoLocal (LISTAdeNIVELES identificador) {
			return Level.folderResourcesLevelLocal(identificador) + identificador.toString() + pathNameExt;
		}
		
		private static String pathLevelInfoBuild (LISTAdeNIVELES identificador) {
			return Level.folderResourcesLevelBuild(identificador) + identificador.toString() + pathNameExt;
		}
		
		public static SetupLevel loadInfoLevel (LISTAdeNIVELES identificador) {
			String savedData = FileHelper.readInternalFile(pathLevelInfoLocal(identificador));
			Json json = new Json();
			json.setUsePrototypes(false);
			return json.fromJson(SetupLevel.class, savedData);
		}
	}
	
	private static class TrialConfig {
		boolean confiance;
		TrialType trialType;
	}
	
	private enum TrialType {
		Estimulo,NoEstimulo,Test
	}
	
	static class Estimulo implements Comparable<Estimulo> {
		double anguloFijo; // Angulo de inclinacion de las rectas paralelas de
		// referencia
		double desviacion; // Desviacion respecto a la referencia
		int idResource; // Id del archivo con el recurso
							int idTrial; // Id del trial en que se evalua al recurso (esto es porque 
		int nivelSenal; // Nivel de intensidad de la señal. Cero representa el angulo recto o las rectas paralelas. Y despues representa una escala lineal que mapea las estimulos ordenados segun la intensidad del estimulo a medir (mas facil mayor intencidad)
		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	static class EstimuloAngulo extends Estimulo implements Comparable<Estimulo> {
		double anguloLadoMovil; // Angulo absoluto del lado movil
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(this.desviacion).compareTo(o.desviacion);
		}
		
	}
	
	static class LogConvergencia {
		LevelLog levelLog; 
		Dinamica dinamica; 
	}
	
	SetupLevel setupLevel;
	Dinamica dinamica;
	
	public LevelUmbral() {
	}

	public LevelUmbral(LISTAdeNIVELES identificador) {
		super(identificador);
	}

	@Override
	public Trial getNextTrial() {
		// Obtiene lo que deberia pasar de la lista speudorando
		dinamica.trialConfig = this.dinamica.pseudorandom.get(this.dinamica.historial.size);
		// Decide si manda una señal para medir de verdad o un test para probar al usuario
		if (dinamica.trialConfig.trialType==TrialType.Test) { // Caso en que se mande un test
			this.dinamica.trialType = TrialType.Test;
			int base = this.dinamica.nivelEstimulo *2;
			base = MathUtils.clamp(base, setupLevel.numeroDeEstimulosPorSerie-1 - setupLevel.numeroDeEstimulosPorSerie*2/5, setupLevel.numeroDeEstimulosPorSerie-1);
			int nivel = MathUtils.random(base, setupLevel.numeroDeEstimulosPorSerie-1);
			dinamica.estimuloActivo = dinamica.seriesEstimulos.random().listaEstimulos.get(nivel);
		}
		if (dinamica.trialConfig.trialType==TrialType.Estimulo) {
			dinamica.trialType = TrialType.Estimulo;
			dinamica.estimuloActivo = dinamica.seriesEstimulos.random().listaEstimulos.get(dinamica.nivelEstimulo);
		}
		if (dinamica.trialConfig.trialType==TrialType.NoEstimulo) {
			dinamica.trialType = TrialType.NoEstimulo;
			dinamica.estimuloActivo = dinamica.estimulosCeros.random();
		}
		JsonTrial jsonTrial = Trial.loadJsonTrial(Level.folderResourcesLevelLocal(identificador), dinamica.estimuloActivo.idTrial);
		
		Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
		for (int idElemento : jsonTrial.elementosId) {
			ExperimentalObject elemento = new ExperimentalObject(idElemento, levelAssets, Level.folderResourcesLocal(identificador.listaDeRecursos));
			elementos.add(elemento);
		}
		ExperimentalObject estimulo = new ExperimentalObject(jsonTrial.rtaCorrectaId, levelAssets, Level.folderResourcesLocal(identificador.listaDeRecursos));
		// Con la info del json del trial tenemos que crear un trial
		return new Trial(elementos, jsonTrial, estimulo);
	}
	
	@Override
	public boolean goConfiance() {
		return dinamica.pseudorandom.get(dinamica.historial.size).confiance;
	}
	
	@Override
	public void interrupt() {
		this.sendDataLevel();
	}
	
	@Override
	public void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion, float timeConfiance,
			int loopsCount) {
		// Almacenamos en el historial lo que paso
		dinamica.historial.add(new Respuesta (dinamica.estimuloActivo, answerCorrect, confianzaReportada, dinamica.trialType, dinamica.nivelEstimulo, timeSelecion, timeConfiance, loopsCount));
		
		// Nos fijamos si ya se completo la dinamica o no. (antes de que salga si es un test trial)
		if (this.trialsLeft() == 0) {
			dinamica.levelFinalizadoCorrectamente=true;
			this.levelCompleted = true;
		}
		
		if (dinamica.trialConfig.trialType==TrialType.Test) {
			return;
		}
		
		// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
		boolean incrementarDificultad=false;
		boolean disminuirDificultad=false;
		
		if (answerCorrect) {
			dinamica.aciertosAcumulados++;
		} else {
			dinamica.erroresAcumulados++;
		}
		
		

		if (((dinamica.erroresInicialesAcumulados>=dinamica.rebotesActivarProporciones) || (dinamica.historial.size > (setupLevel.trialsPorNivel  * setupLevel.saltoGruesoFraccionNivel)*3/4 )) || (dinamica.herenciaRecibida)) {
			if (dinamica.aciertosAcumulados == dinamica.aciertosDown) {
				incrementarDificultad = true;
				if (dinamica.ascendiendo) {
					dinamica.secuenciasAcumuladas=1;
					dinamica.ascendiendo = false;
				} else {
					dinamica.secuenciasAcumuladas ++;
				}
				dinamica.erroresAcumulados = 0;
				dinamica.aciertosAcumulados = 0;
			}
			
			if (dinamica.erroresAcumulados == dinamica.erroresUp) {
				disminuirDificultad = true;
				if (dinamica.ascendiendo) {
					dinamica.secuenciasAcumuladas ++;
				} else {
					dinamica.secuenciasAcumuladas=1;
					dinamica.ascendiendo = true;
				}
				dinamica.erroresAcumulados = 0;
				dinamica.aciertosAcumulados = 0;
				dinamica.erroresInicialesAcumulados ++;
			}
		} else {
			if (dinamica.aciertosAcumulados == 1) {
				incrementarDificultad = true;
				dinamica.erroresAcumulados = 0;
				dinamica.aciertosAcumulados = 0;
			}
			if (dinamica.erroresAcumulados == 1) {
				disminuirDificultad = true;
				dinamica.erroresAcumulados = 0;
				dinamica.aciertosAcumulados = 0;
				dinamica.erroresInicialesAcumulados ++;
			}
		}

		// Setea el salto entre nivel y nivel
		if (!dinamica.herenciaRecibida) {
			int saltosGruesos = (int) (setupLevel.trialsPorNivel  * setupLevel.saltoGruesoFraccionNivel);
			float fraccionAvanceSaltoGrueso = (float) dinamica.historial.size / saltosGruesos;
			if (fraccionAvanceSaltoGrueso<1) {
				int saltoMaximo = setupLevel.numeroDeEstimulosPorSerie/setupLevel.saltoInicialFraccion;
				dinamica.saltosActivos = MathUtils.ceil(saltoMaximo*(1-fraccionAvanceSaltoGrueso));
			} else { 
				float fraccionAvanceSaltoIntermedio = (float) (dinamica.historial.size - saltosGruesos) / (setupLevel.trialsPorNivel - saltosGruesos);
				if (fraccionAvanceSaltoIntermedio<0.5) {
					dinamica.saltosActivos = Dinamica.saltoIntermedio * dinamica.secuenciasAcumuladas;
				} else {
					dinamica.saltosActivos = Dinamica.saltoUnitario * dinamica.secuenciasAcumuladas;
				}
			}
		} else {
			dinamica.saltosActivos = Dinamica.saltoUnitario * dinamica.secuenciasAcumuladas;
		}
		
		// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
		if (incrementarDificultad) {
			dinamica.nivelEstimulo=dinamica.nivelEstimulo-dinamica.saltosActivos;
		}
		if (disminuirDificultad) {
			dinamica.nivelEstimulo=dinamica.nivelEstimulo+dinamica.saltosActivos;
		}
			
		dinamica.nivelEstimulo = MathUtils.clamp(dinamica.nivelEstimulo, 1, setupLevel.numeroDeEstimulosPorSerie-1);
	}

	@Override
	void sendDataLevel() {
		// Hacemos un send para la data del nivel que acaba de detenerse.
		LogConvergencia log = new LogConvergencia();
		log.levelLog = this.log;
		log.dinamica = dinamica;
		log.dinamica.seriesEstimulos.clear();
		log.dinamica.estimulosCeros.clear();
		// Creamos el enviable
		Internet.crearEnvio(log, TIPOdeENVIO.RESULTADOS, identificador.toString());
		//Internet.addDataToSend(log, TIPO_ENVIO.CONVERGENCIA, identificador.toString());
	}

	@Override
	public int trialsLeft() {
		return dinamica.trialsPorNivel - dinamica.historial.size;
	}

	@Override
	void specificLoads(Session sesion) {
		setupLevel = SetupLevel.loadInfoLevel(identificador);
		dinamica = Dinamica.loadDinamica(identificador); 
		if (!setupLevel.restartEstimulo) {
			if (sesion.user.lastNivel(this.identificador)!= -1) {
				dinamica.herenciaRecibida = true;
				dinamica.nivelEstimulo = sesion.user.lastNivel(this.identificador);
			}
		}
		if (!dinamica.herenciaRecibida) {
			dinamica.nivelEstimulo = setupLevel.numeroDeEstimulosPorSerie - 1;
		}
		makeSpeudoRandom();		
	}

	private void makeSpeudoRandom() {
		int numberOfEstimulo;
		int numberOfNoEstimulo;
		int numberOfTest;
		
		numberOfTest = (int) (setupLevel.trialsPorNivel*setupLevel.testProbability);
		if ((setupLevel.trialsPorNivel - numberOfTest) % 2 != 0) {
			Gdx.app.debug(TAG, "WARNING: El numero de trials a asignar en señal o no señal no es par y no quedara bien balanceado. Se agrega un trial test para equilibrar");
			numberOfTest ++;
		}
		numberOfEstimulo = ((setupLevel.trialsPorNivel - numberOfTest) / 2);
		numberOfNoEstimulo = ((setupLevel.trialsPorNivel - numberOfTest) / 2);
		
		
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
		confianceCount = (int) (setupLevel.confianceProbability * trialsTest.size);
		if (setupLevel.allTestsConfianza) {
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
		confianceCount = (int) (setupLevel.confianceProbability * trialsEstimulo.size);
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
		confianceCount = (int) (setupLevel.confianceProbability * trialsNoEstimulo.size);
		for (TrialConfig test : trialsNoEstimulo) {
			i++;
			if (i<=confianceCount) {
				test.confiance = true;
			} else {
				test.confiance = false;
			}
			test.trialType = TrialType.NoEstimulo;
		}
		
		dinamica.pseudorandom.addAll(trialsTest);
		dinamica.pseudorandom.addAll(trialsEstimulo);
		dinamica.pseudorandom.addAll(trialsNoEstimulo);
		dinamica.pseudorandom.shuffle();
		
	}

	@Override
	public double getDesviacionActual() {
		if (dinamica.historial.size != 0) {
			return dinamica.historial.peek().estimulo.desviacion; 
		} else {
			return -1;
		}
	}

	@Override
	public int getNivelSenalActual() {
		if (dinamica.historial.size != 0) {
			return dinamica.historial.peek().nivelEstimulo; 
		} else {
			return -1;
		}
	}

	@Override
	public int getrepeatnumber() {
		if (this.setupLevel.soundLoopLimit == -1) {
			return 100;
		} else {
			return this.setupLevel.soundLoopLimit;
		}
	}
}
