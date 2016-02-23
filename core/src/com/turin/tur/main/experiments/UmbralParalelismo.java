package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Internet.Enviable;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Textos;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class UmbralParalelismo implements Experiment {

	private static class SessionLog {
		private long userId;
		private long sessionInstance;
		private String expName;
		private int levelVersion;
		private int resourcesVersion;
		private int codeVersion;
	}
	
	/**
	 * Esta clase regula la dinamica del experimento y guarda toda la info
	 * necesaria para tomar desiciones acerca de que trial seleccionar o si
	 * continuar el experimento o terminarlo
	 * 
	 * @author ionatan
	 *
	 */
	private static class DinamicaExperimento {
		private String identificador; // Algo para indentificar cual convergencia es cual.
		private int nivelEstimulo; // nivel de señal enviada
		private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		private boolean convergenciaAlcanzada = false;
		private Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		private Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
		private double anguloDeReferencia;
		private float ultimaSD;
		private float ultimoMEAN;
		private int proporcionAciertos = 2; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		private int proporcionTotal = 3; // Es el numero de elementos a revisar en el historial en busca de la cantidad de acierto para definir si se aumenta la dificultad o no
		private int tamanoVentanaAnalisisConvergencia = 6;
		private float sdEsperada = 1;
	}

	private static class Estimulo implements Comparable<Estimulo> {
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
	
	private static class Respuesta {
		private Estimulo estimulo;
		private boolean acertado;
		Respuesta (Estimulo estimulo, Boolean rta) {
			this.estimulo = estimulo;
			this.acertado = rta;
		}
	}
	
	private static class ImageInfo {
		Linea linea1 = new Linea();
		Linea linea2 = new Linea();
		double referencia;
		double desviacion;
		double separacion;
	}
	
	private static class Setup {
		Array<Double> angulosReferencia = new Array<Double>();
		Array<Double> desviacionesAngulares = new Array<Double>();
		Array<Estimulo> estimulos = new Array<Estimulo>();
	}

	static final String TAG = UmbralParalelismo.class.getName();
	// Cosas generales
	private Setup setup;
	private String expName = "UmbralParalelismo";
	private ExpSettings expSettings;
	// Cosas que manejan la dinamica en cada ejecucion
	private Level level;
	private Array<DinamicaExperimento> dinamicas;
	private DinamicaExperimento dinamicaActiva;
	private Trial trial;
	private boolean waitingAnswer;
	private LevelAsset assets;
	private Estimulo estimuloActivo;
	private ArrayMap <String, WindowedMean> ventanasNivel = new ArrayMap <String, WindowedMean>(); // Esto esta aca porque la clase WindowedMean no es facil guardarla en un json, entonces se guarada en la clase principal un conjunto de windows asociados al nombre de cada convergencia

	// Logs
	SessionLog sessionLog;

	@Override
	public boolean askCompleted() {
		for (DinamicaExperimento dinamica : this.dinamicas) {
			if (!dinamica.convergenciaAlcanzada) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void askNext() {
		if (!waitingAnswer) {
			// Seleccionamos una de las convergencias al azar.
			Array<DinamicaExperimento> forSelect = new Array<DinamicaExperimento>();
			for (DinamicaExperimento dinamica : this.dinamicas) {
				if (!dinamica.convergenciaAlcanzada) {
					forSelect.add(dinamica);
				}
			}
			this.dinamicaActiva = forSelect.random();
			// Buscamos el trial que corresponde al nivel actual de la
			// convergencia (los cambios se actualizan cuando se recibe el
			// answer)
			this.estimuloActivo = this.dinamicaActiva.listaEstimulos.get(this.dinamicaActiva.nivelEstimulo);
			// leemos el json del trial
			String path = Resources.Paths.finalPath + "/level" + level.Id + "/trial" + this.estimuloActivo.idTrial + ".meta";
			String savedData = FileHelper.readLocalFile(path);
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
			this.waitingAnswer = true;
		}
	}

	public Trial getTrial() {
		return this.trial;
	}

	private ArrayMap<Double, ArrayMap<Double, Estimulo>> indexToMap() {
		ArrayMap<Double, ArrayMap<Double, Estimulo>> map = new ArrayMap<Double, ArrayMap<Double, Estimulo>>();
		for (Estimulo estimulo : this.setup.estimulos) {
			if (!map.containsKey(estimulo.referencia)) {
				map.put(estimulo.referencia, new ArrayMap<Double, Estimulo>());
			}
			map.get(estimulo.referencia).put(estimulo.desviacion, estimulo);
		}
		return map;
	}

	@Override
	public void initGame() {
		// Cargamos la info del experimento
		String path = Resources.Paths.finalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		this.expSettings = json.fromJson(Experiments.ExpSettings.class, savedData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initLevel(Level level) {
		// Cargamos los datos especificos del nivel
		this.level = level;
		this.dinamicas = (Array<DinamicaExperimento>) level.jsonLevel.infoDinamica;
		this.assets = new LevelAsset(level.Id);
		this.ventanasNivel.clear();
	}

	@Override
	public void interrupt() {
		this.waitingAnswer = false;
	}

	@Override
	public Array<LevelStatus> levelsStatus() {
		return this.expSettings.levels;
	}

	@Override
	public void makeLevels() {

		// Hacemos tareas de revision y limpieza
		Builder.verifyLevelVersion();
		Builder.verifyResources();
		Builder.cleanAssets();

		// Cargamos los datos del setup
		String path = Resources.Paths.currentVersionPath + "/extras/" + this.expName + "Setup.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.setup = json.fromJson(UmbralParalelismo.Setup.class, savedData);

		// Creamos el setting del experimento
		this.expSettings = new ExpSettings();
		this.expSettings.tipoDeExperimento = TIPOdeEXPERIMENTO.UmbralParalelismo;

		// Categorizamos los recursos en un mapa
		ArrayMap<Double, ArrayMap<Double, Estimulo>> map = this.indexToMap();

		// Hacemos un nivel para cada referencia
		for (double referencia : this.setup.angulosReferencia) {
			// Creamos el nivel
			JsonLevel level = Builder.crearLevel();
			// level.tipoDeLevel = TIPOdeLEVEL.UMBRALPARALELISMO;
			level.levelTitle = "R: " + referencia;

			// Buscamos la inclinacion minima para la referencia que sea visible
			// (porque la sensibilidad auditiva puede ser superior a la
			// visual!). A ojo una desviacion de 2.5 grados se percibe.
			// Es probable que esto se pueda hacer mas eficiente
			Array<Double> temp = new Array<Double>();
			for (double desviacion : this.setup.desviacionesAngulares) {
				if (desviacion > 2.5d) {
					temp.add(desviacion);
				}
			}
			temp.sort();
			double limiteVisible = temp.first();

			// Creamos el elemento de la info dinamica que corresponde al nivel
			DinamicaExperimento dinamicaPos = new DinamicaExperimento();
			DinamicaExperimento dinamicaNeg = new DinamicaExperimento();
			dinamicaPos.identificador = "Acercamiento Positivo";
			dinamicaNeg.identificador = "Acercamiento Negativo";

			// Creamos los trials (uno para cada desviacion)
			for (double desviacion : this.setup.desviacionesAngulares) {
				JsonTrial trial;
				if ((desviacion > limiteVisible) || (desviacion < -limiteVisible)) { // Si la desviacion es visible
													// dejamos que se elija
													// entre las dos imagenes
													// reales
					trial = Builder.crearTrial("Indique a que imagen se parece el sonido", "",
							DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] { map.get(referencia).get(desviacion).idResource,
									map.get(referencia).get(-desviacion).idResource },
							TIPOdeTRIAL.TEST, map.get(referencia).get(desviacion).idResource, false, true, false);
				} else { // si es muy chica mostramos dos imagenes donde el
							// efecto sea visible (igual como comparten
							// categoria el LevelController detecta la
							// coincidencia
					trial = Builder.crearTrial("Indique a que imagen se parece el sonido", "",
							DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] { map.get(referencia).get(limiteVisible).idResource,
									map.get(referencia).get(-limiteVisible).idResource },
							TIPOdeTRIAL.TEST, map.get(referencia).get(desviacion).idResource, false, true, false);
				}
				// agregamos el trial al index
				map.get(referencia).get(desviacion).idTrial = trial.Id;
				if (desviacion > 0) {
					dinamicaPos.listaEstimulos.add(map.get(referencia).get(desviacion));
				}
				if (desviacion < 0) {
					dinamicaNeg.listaEstimulos.add(map.get(referencia).get(desviacion));
				}
				level.jsonTrials.add(trial);
			}
			// Ordenamos las listas de estimulos segun dificultad decreciente y
			// la numeramos
			dinamicaPos.listaEstimulos.sort();
			dinamicaNeg.listaEstimulos.sort(); //Por alguna extraña razon el sort usa el modulo y no el valor con signo. 
			// dinamicaNeg.listaEstimulos.reverse();
			for (int i = 0; i < dinamicaPos.listaEstimulos.size; i++) {
				dinamicaPos.listaEstimulos.get(i).nivelSenal = i;
			}
			for (int i = 0; i < dinamicaNeg.listaEstimulos.size; i++) {
				dinamicaNeg.listaEstimulos.get(i).nivelSenal = i;
			}

			// Retocamos la info dinamica
			dinamicaPos.anguloDeReferencia = referencia;
			dinamicaNeg.anguloDeReferencia = referencia;
			dinamicaPos.convergenciaAlcanzada = false;
			dinamicaNeg.convergenciaAlcanzada = false;
			dinamicaPos.nivelEstimulo = dinamicaPos.listaEstimulos.size - 1;
			dinamicaNeg.nivelEstimulo = dinamicaNeg.listaEstimulos.size - 1;
			dinamicaPos.saltosActivos = dinamicaPos.listaEstimulos.size / 10;
			dinamicaNeg.saltosActivos = dinamicaNeg.listaEstimulos.size / 10;

			// Agrupamos todas las convergencias del nivel en un array y lo
			// mandamos a la variable object del level
			Array<DinamicaExperimento> convergencias = new Array<DinamicaExperimento>();
			convergencias.add(dinamicaPos);
			convergencias.add(dinamicaNeg);
			level.infoDinamica = convergencias;
			// Extraemos los niveles y los recursos a la carpeta que corresponda
			Builder.extract(level);
			Builder.buildJsons(level);

			// Agregamos el nivel al setting
			LevelStatus levelStatus = new LevelStatus();
			levelStatus.enabled = true;
			levelStatus.id = level.Id;
			levelStatus.name = level.levelTitle;
			levelStatus.alreadyPlayed = false;
			this.expSettings.levels.add(levelStatus);
		}
		// Creamos un archivo con la info del experimento
		String path2 = Resources.Paths.finalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		Json json2 = new Json();
		json2.setUsePrototypes(false);
		FileHelper.writeFile(path2, json.toJson(this.expSettings));
	}

	private void makeResource(double referencia, double desviacion) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width > Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}

		// Creamos la info conceptual de la imagen
		ImageInfo info = new ImageInfo();

		float largo = tamano * 0.8f;
		float separacion = tamano * 0.2f;

		float Xcenter = Resources.Display.width / 2;
		float Ycenter = Resources.Display.height / 2;

		// Calculamos los centros de manera que esten separados en funcion del
		// angulo
		info.linea1.radial.Xcenter = Xcenter - separacion / 2 * MathUtils.sinDeg((float) referencia);
		info.linea2.radial.Xcenter = Xcenter + separacion / 2 * MathUtils.sinDeg((float) referencia);
		info.linea1.radial.Ycenter = Ycenter + separacion / 2 * MathUtils.cosDeg((float) referencia);
		info.linea2.radial.Ycenter = Ycenter - separacion / 2 * MathUtils.cosDeg((float) referencia);

		info.linea1.radial.angulo = referencia + desviacion;
		info.linea2.radial.angulo = referencia - desviacion;

		info.linea1.radial.largo = largo;
		info.linea2.radial.largo = largo;

		info.desviacion = desviacion;
		info.referencia = referencia;
		info.separacion = separacion;

		// Pasamos la info a formato cartesiano
		info.linea1.lineaFromRadial();
		info.linea2.lineaFromRadial();

		// Creamos la imagen correspondiente
		Imagenes imagen = new Imagenes();

		// Cargamos la info conceptual (que varia segun el tipo de experimento)
		imagen.infoConceptual = info;

		// Creamos las categorias correspondientes
		if (info.desviacion > 0) {
			imagen.categories.add(Categorias.Diverge);
		}
		if (info.desviacion < 0) {
			imagen.categories.add(Categorias.Converge);
		}
		if (info.desviacion == 0) {
			imagen.categories.add(Categorias.Paralelas);
		}
		// Agregamos las dos lineas para que se dibujen
		imagen.lineas.add(info.linea1);
		imagen.lineas.add(info.linea2);

		// Hacemos de la info de la imagen el SVG
		imagen.toSVG();

		// Agregamos al setup el recurso
		Estimulo estimulo = new Estimulo();
		estimulo.idResource = imagen.resourceId.id;
		estimulo.desviacion = info.desviacion;
		estimulo.referencia = info.referencia;
		this.setup.estimulos.add(estimulo);
	}

	// Todo lo que sigue a continuacion son cosas publicas de la interfaz, las
	// anteriores son privadas del funcionamiento interno
	@Override
	public void makeResources() {
		// Verificamos la version
		Builder.verifyResourcesVersion();
		// Inicializamos el setup segun parametros
		this.makeSetup();
		// Creamos los textos
		Textos.crearTextos();
		// Creamos un recurso para cada imagen necesaria
		for (double referencia : this.setup.angulosReferencia) {
			for (double desviacion : this.setup.desviacionesAngulares) {
				makeResource(referencia, desviacion);
			}
		}
		// Guardamos el setup
		String path = Resources.Paths.currentVersionPath + "/extras/" + this.expName + "Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.setup));
	}

	private void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(0d);
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(90d);
		// Generamos las desviaciones
		float desvMin = 0.01f;
		float desvMax = 30f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 40;
		boolean logscale = true;
		// Creamos la serie de desviaciones respecto al paralelismo
		if (logscale) {
			double paso = (desvMaxLog - desvMinLog) / (numeroDeDesviaciones - 1);
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMinLog + paso * i);
			}
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.set(i, Math.exp(setup.desviacionesAngulares.get(i)));
			}
		} else {
			double paso = (desvMax - desvMin) / numeroDeDesviaciones;
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMin + paso * i);
			}
		}
		// Agregamos una copia negativa
		for (int i = 0; i < numeroDeDesviaciones; i++) {
			setup.desviacionesAngulares.add(-setup.desviacionesAngulares.get(i));
		}
		this.setup = setup;
	}

	@Override
	public void returnAnswer(boolean answer) {
		// Almacenamos en el historial lo que paso
		this.dinamicaActiva.historial.add(new Respuesta (this.estimuloActivo, answer));
		// Marcamos que se recibio una rta
		this.waitingAnswer = false;
		
		// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
		boolean incrementarDificultad=false;
		boolean disminuirDificultad=false;
		if (this.dinamicaActiva.historial.peek().acertado) { // Si se acerto y no hay suficiente historial se debe disminuir la dificultad, sino hay que revisar si la proporcion de aciertos requeridos esta cumpleida
			if (this.dinamicaActiva.historial.size >= this.dinamicaActiva.proporcionTotal) { // Estamos en el caso en que hay que mirar el historial
				// Nos fijamos si hay suficientes aciertos en el ultimo tramo como para aumentar la dificultad
				int contadorAciertos=0;
				for (int i=1; i<=(this.dinamicaActiva.proporcionTotal); i++){
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
		
		// Se fija si hay que disminuir el salto entre nivel y nivel. Para simplicar solo se considera que disminuye cuando hay un rebote "hacia arriba"
		if (this.dinamicaActiva.historial.size >1) { // Verifica q haya al menos dos datos
			if (!this.dinamicaActiva.historial.peek().acertado) { // Se se erro el ultimo 
				if (this.dinamicaActiva.historial.get(this.dinamicaActiva.historial.size-2).acertado) { // Si se acerto el anterior (hay rebote) 
					this.dinamicaActiva.saltosActivos = this.dinamicaActiva.saltosActivos - 1;
					// Verificamos que no llegue a cero el salto
					if (this.dinamicaActiva.saltosActivos==0) {
						this.dinamicaActiva.saltosActivos = 1;
					}
				}
			}
		}
		
		// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
		if (incrementarDificultad) {
			this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.nivelEstimulo-this.dinamicaActiva.saltosActivos;
			if (this.dinamicaActiva.nivelEstimulo<0) {this.dinamicaActiva.nivelEstimulo=0;}
		}
		if (disminuirDificultad) {
			this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.nivelEstimulo+this.dinamicaActiva.saltosActivos;
			if (this.dinamicaActiva.nivelEstimulo>this.dinamicaActiva.listaEstimulos.size-1) {this.dinamicaActiva.nivelEstimulo=this.dinamicaActiva.listaEstimulos.size-1;}
		}
		
		// Nos fijamos si se alcanzo la convergencia
		if (!this.ventanasNivel.containsKey(this.dinamicaActiva.identificador)) { // Primero nos fijamos si existe una ventana para el cuadrante activo
			this.ventanasNivel.put(this.dinamicaActiva.identificador, new WindowedMean(this.dinamicaActiva.tamanoVentanaAnalisisConvergencia));
		}	
		this.ventanasNivel.get(this.dinamicaActiva.identificador).addValue(this.estimuloActivo.nivelSenal);
		if (this.ventanasNivel.get(this.dinamicaActiva.identificador).hasEnoughData()) {
			this.dinamicaActiva.ultimaSD = this.ventanasNivel.get(this.dinamicaActiva.identificador).standardDeviation();
			this.dinamicaActiva.ultimoMEAN = this.ventanasNivel.get(this.dinamicaActiva.identificador).getMean();
			if (this.dinamicaActiva.ultimaSD < this.dinamicaActiva.sdEsperada) {
				this.dinamicaActiva.convergenciaAlcanzada = true;
				Gdx.app.debug(TAG, this.dinamicaActiva.identificador + " ha alcanzado la convergencia con valor " + this.dinamicaActiva.ultimoMEAN);
			}
		}
		Gdx.app.debug(TAG, this.dinamicaActiva.identificador + " SD: " + this.dinamicaActiva.ultimaSD);
		Gdx.app.debug(TAG, this.dinamicaActiva.identificador + " Nivel: " + this.dinamicaActiva.nivelEstimulo);
		Gdx.app.debug(TAG, this.dinamicaActiva.identificador + " Rta correcta: " + answer);
	}

	@Override
	public void stopLevel() {
		this.waitingAnswer = false;
	}


	@Override
	public void event_newAnswer(ExperimentalObject estimulo, boolean rtaCorrecta) {
		// TODO 
	}

	@Override
	public void event_sendReport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void event_initGame(Session session) {
		// Creamos el log
		this.sessionLog = new SessionLog();
		this.sessionLog.expName = this.expName;
		this.sessionLog.levelVersion = session.levelVersion;
		this.sessionLog.resourcesVersion = session.resourcesVersion;
		this.sessionLog.sessionInstance = session.sessionInstance;
		this.sessionLog.userId = session.userID;
		this.sessionLog.codeVersion = session.codeVersion;
		// Creamos el enviable
		Json json = new Json();
		json.setUsePrototypes(false);
		String string = json.toJson(this.sessionLog);
		Enviable envio = new Enviable (string, TIPO_ENVIO.SESION);
		Internet.addData(envio);
		Internet.tryToSend();
	}

	@Override
	public void event_initLevel(Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void event_initTrial(Trial trial) {
		// TODO Auto-generated method stub
		
	}

}
