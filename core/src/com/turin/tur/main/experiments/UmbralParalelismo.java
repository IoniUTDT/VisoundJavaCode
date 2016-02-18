package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Textos;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class UmbralParalelismo implements Experiment {

	static final String TAG = UmbralParalelismo.class.getName();
	
	private Setup setup;
	private String expName = "UmbralParalelismo";
	private ExpSettings expSettings;
	
	private void makeSetup () {
		// Creamos el setup
		Setup setup = new Setup();
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(0d);
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(90d);
		// Generamos las desviaciones
		float desvMin = 0.01f;
		float desvMax = 45f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 50;
		boolean logscale = true;
		// Creamos la serie de desviaciones respecto al paralelismo
		if (logscale) {
			double paso = (desvMaxLog - desvMinLog) / (numeroDeDesviaciones-1); 
			for (int i = 0; i<numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMinLog + paso *i);
			}
			for (int i = 0; i<numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.set(i, Math.exp(setup.desviacionesAngulares.get(i)));
			}
		} else {
			double paso = (desvMax - desvMin) / numeroDeDesviaciones; 
			for (int i = 0; i<numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMin + paso *i);
			}
		}
		// Agregamos una copia negativa
		for (int i = 0; i<numeroDeDesviaciones; i++) {
			setup.desviacionesAngulares.add(-setup.desviacionesAngulares.get(i));
		}
		this.setup=setup;
	}
	
	private static class Setup {
		Array<Double> angulosReferencia = new Array<Double>();
		Array<Double> desviacionesAngulares = new Array<Double>();
		Array<Estimulo> estimulos = new Array<Estimulo>();
	}
	
	private static class Estimulo implements Comparable<Estimulo> {
		int idResource; // Id del archivo con el recurso
		int idTrial; // Id del trial en que se evalua al recurso
		double referencia; // Angulo de inclinacion de las rectas paralelas de referencia
		double desviacion; // Desviacion respecto a la referencia
		int nivelSenal; // Nivel de intensidad de la señal en escala lineal (cada estimulo representa un paso) dentro del nivel
		
		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	private void makeResource (double referencia, double desviacion) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width>Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		
		// Creamos la info conceptual de la imagen
		ImageInfo info = new ImageInfo();
	
		float largo = tamano * 0.8f;
		float separacion = tamano * 0.2f;
		
		float Xcenter = Resources.Display.width/2;
		float Ycenter = Resources.Display.height/2;
		
		
		// Calculamos los centros de manera que esten separados en funcion del angulo
		info.linea1.radial.Xcenter = Xcenter - separacion/2 * MathUtils.sinDeg((float) referencia);
		info.linea2.radial.Xcenter = Xcenter + separacion/2 * MathUtils.sinDeg((float) referencia);
		info.linea1.radial.Ycenter = Ycenter + separacion/2 * MathUtils.cosDeg((float) referencia);
		info.linea2.radial.Ycenter = Ycenter - separacion/2 * MathUtils.cosDeg((float) referencia);
		
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
		if (info.separacion > 0) {
			imagen.categories.add(Categorias.Diverge);
		}
		if (info.separacion < 0) {
			imagen.categories.add(Categorias.Converge);
		}
		if (info.separacion == 0) {
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
	
	private class ImageInfo {
		Linea linea1 = new Linea();
		Linea linea2 = new Linea();
		double referencia;
		double desviacion;
		double separacion;
	}
	
	//  Todo lo que sigue a continuacion son cosas publicas de la interfaz, las anteriores son privadas del funcionamiento interno
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
				makeResource (referencia, desviacion);
			}
		}
		// Guardamos el setup 
		String path = Resources.Paths.currentVersionPath+"/extras/"+this.expName+"Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.setup));
	}

	private ArrayMap<Double, ArrayMap<Double, Estimulo>> indexToMap () {
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
	public void makeLevels() {
		
		// Hacemos tareas de revision y limpieza
		Builder.verifyLevelVersion();
		Builder.verifyResources();
		Builder.cleanAssets();
		
		// Cargamos los datos del setup
		String path = Resources.Paths.currentVersionPath+"/extras/"+this.expName+"Setup.meta";
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
			level.tipoDeLevel = TIPOdeLEVEL.UMBRALPARALELISMO;
			level.levelTitle = "R: " + referencia; 
			
			// Buscamos la inclinacion minima para la referencia que sea visible (porque la sensibilidad auditiva puede ser superior a la visual!). A ojo una desviacion de 2.5 grados se percibe.
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
				if (desviacion > limiteVisible) { // Si la desviacion es visible dejamos que se elija entre las dos imagenes reales
					trial = Builder.crearTrial("Indique a que imagen se parece el sonido", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] {map.get(referencia).get(desviacion).idResource,map.get(referencia).get(-desviacion).idResource}, TIPOdeTRIAL.TEST, 
							map.get(referencia).get(desviacion).idResource, false, true, false);
				} else { // si es muy chica mostramos dos imagenes donde el efecto sea visible (igual como comparten categoria el LevelController detecta la coincidencia
					trial = Builder.crearTrial("Indique a que imagen se parece el sonido", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] {map.get(referencia).get(limiteVisible).idResource,map.get(referencia).get(-limiteVisible).idResource}, TIPOdeTRIAL.TEST, 
							map.get(referencia).get(desviacion).idResource, false, true, false);
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
			// Ordenamos las listas de estimulos segun dificultad decreciente y la numeramos 
			dinamicaPos.listaEstimulos.sort();
			dinamicaNeg.listaEstimulos.sort();
			dinamicaNeg.listaEstimulos.reverse();
			for (int i=0; i<dinamicaPos.listaEstimulos.size; i++) {
				dinamicaPos.listaEstimulos.get(i).nivelSenal=i;
			}
			for (int i=0; i<dinamicaNeg.listaEstimulos.size; i++) {
				dinamicaNeg.listaEstimulos.get(i).nivelSenal=i;
			}
			
			// Retocamos la info dinamica 
			dinamicaPos.anguloDeReferencia = referencia;
			dinamicaPos.anguloDeReferencia = referencia;
			dinamicaPos.convergenciaAlcanzada = false;
			dinamicaPos.convergenciaAlcanzada = false;
			dinamicaPos.nivelEstimulo = dinamicaPos.listaEstimulos.size - 1;
			dinamicaPos.nivelEstimulo = dinamicaPos.listaEstimulos.size - 1;
			dinamicaPos.saltosActivos = dinamicaPos.listaEstimulos.size / 5;
			dinamicaPos.saltosActivos = dinamicaPos.listaEstimulos.size / 5;
			
			// Agrupamos todas las convergencias del nivel en un array y lo mandamos a la variable object del level
			Array<DinamicaExperimento> convergencias = new Array<DinamicaExperimento>();
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

	/**
	 * Esta clase regula la dinamica del experimento y guarda toda la info necesaria para tomar desiciones acerca de que trial seleccionar o si continuar el experimento o terminarlo
	 * @author ionatan
	 *
	 */
	private static class DinamicaExperimento {
		private String identificador; // Algo para indentificar cual convergencia es cual. 
		private int nivelEstimulo; // nivel de señal enviada
		private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		private boolean convergenciaAlcanzada=false;
		private Array<Estimulo> historial = new Array<Estimulo>(); // Se almacena la info de lo que va pasando
		private Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
		private double anguloDeReferencia;
		private float ultimaSD;
		private float ultimoMEAN;		
	}
	
	@Override
	public Trial askTrial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnAnswer(boolean answer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initGame() {
		// Cargamos la info del experimento
		String path = Resources.Paths.finalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		this.expSettings = json.fromJson(Experiments.ExpSettings.class, savedData);
	}

	@Override
	public void initLevel(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopLevel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Array<LevelStatus> levelsStatus() {
		return this.expSettings.levels;
	}

	@Override
	public boolean askCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

}
