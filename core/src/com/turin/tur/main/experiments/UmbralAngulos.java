package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.PCBuilder;


public abstract class UmbralAngulos extends Umbral {

	static final String TAG = UmbralParalelismo.class.getName();
	// Cosas generales
	
	private static class EstimuloAngulo extends Umbral.Estimulo implements Comparable<Estimulo> {
		private double anguloFormado; // Angulo respecto al lado fijo
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(anguloFormado).compareTo(o.anguloFormado);
		}
		
	}

	
	private static class ImageInfoAngulo extends Umbral.ImageInfo {
		double anguloFormado;
	}
	
	
	@Override
	public void makeResources() {
		// Inicializamos el setup segun parametros
		this.makeSetup();
		// Creamos un recurso para cada imagen necesaria
		for (double ladoFijo : this.setup.angulosReferencia) {
			for (double ladoMovil : this.setup.desviacionesAngulares) {
				if ((ladoMovil - ladoFijo <=180) && (ladoMovil - ladoFijo >=0)) { // Ojo que aca hay un problema con el tema de reiteracion circular! porque importa el orden de la resta a parte del lado fijo en 180
					makeResource(ladoFijo, ladoMovil);
				}
			}
		}
		// Guardamos el setup en la carpeta temporal
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(this.setup));
	}

	private void makeResource(double ladoFijo, double ladoMovil) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width > Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}

		// Creamos la info conceptual de la imagen
		ImageInfoAngulo info = new ImageInfoAngulo();

		float largoLados = tamano * 0.4f;

		float Xcenter = Resources.Display.width / 2;
		float Ycenter = Resources.Display.height / 2;

		// Calculamos los centros de manera que esten separados en funcion del
		// angulo 
		
		
		// Nota: los ejes son cartesianos y hacia abajo, x hacia la derecha
		info.linea1.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) ladoFijo);
		info.linea1.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) ladoFijo);
		info.linea2.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) ladoMovil);
		info.linea2.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) ladoMovil);
		
		
		info.linea1.radial.angulo = ladoFijo;
		info.linea2.radial.angulo = ladoMovil;

		info.linea1.radial.largo = largoLados;
		info.linea2.radial.largo = largoLados;

		info.anguloFormado = ladoMovil - ladoFijo;
		info.referencia = ladoFijo;
		info.desviacion = ladoMovil;
		
		// Pasamos la info a formato cartesiano
		info.linea1.lineaFromRadial();
		info.linea2.lineaFromRadial();

		// Creamos la imagen correspondiente
		Imagenes imagen = new Imagenes();

		// Cargamos la info conceptual (que varia segun el tipo de experimento)
		imagen.infoConceptual = info;

		// Creamos las categorias correspondientes
		if ((info.anguloFormado < 90) &  (info.anguloFormado >= 0)){
			imagen.categories.add(CategoriasImagenes.Agudo);
		}
		if (info.anguloFormado == 90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.anguloFormado <= 180) &  (info.anguloFormado > 90)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
		}
		// Agregamos las dos lineas para que se dibujen
		imagen.lineas.add(info.linea1);
		imagen.lineas.add(info.linea2);

		// Hacemos de la info de la imagen el SVG
		imagen.toSVG();

		// Agregamos al setup el recurso
		EstimuloAngulo estimulo = new EstimuloAngulo();
		estimulo.idResource = imagen.resourceId.id;
		estimulo.anguloFormado = info.anguloFormado;
		estimulo.referencia = info.referencia;
		estimulo.desviacion = info.desviacion;
		this.setup.estimulos.add(estimulo);
	}

	abstract void makeSetup();

	@Override
	public void makeLevels() {
		
		// Cargamos los datos del setup
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.setup = json.fromJson(Umbral.Setup.class, savedData);

		// Creamos el setting del experimento
		this.expSettings = new ExpSettings();
		this.expSettings.tipoDeExperimento = TIPOdeEXPERIMENTO.UmbralAngulos;

		// Categorizamos los recursos una vez.
		ArrayMap<Double, ArrayMap<Double, EstimuloAngulo>> estimulos = new ArrayMap<Double, ArrayMap<Double, EstimuloAngulo>>(); 
		for (Estimulo estimulo : this.setup.estimulos) {
			EstimuloAngulo estimuloA = (EstimuloAngulo) estimulo;
			if (!estimulos.containsKey(estimulo.referencia)) {
				estimulos.put(estimulo.referencia, new ArrayMap<Double, EstimuloAngulo>());
			}
			estimulos.get(estimulo.referencia).put(estimulo.desviacion, estimuloA);
		}
		
		// Hacemos un nivel para cada lado fijo
		for (double ladoFijo : estimulos.keys()) {
			// Creamos el nivel
			JsonLevel level = PCBuilder.crearLevel();
			level.numberOfMaxTrials = this.setup.numeroDeTrailsMaximosxNivel;
			level.levelTitle = "A: " + ladoFijo;

			// Creamos el elemento de la info dinamica que corresponde al nivel
			DinamicaExperimento dinamicaAguda = new DinamicaExperimento();
			dinamicaAguda.identificador = "1er Cuad.";
			// dinamicaAguda.numeroCuadrante = 1;
			DinamicaExperimento dinamicaGave = new DinamicaExperimento();
			dinamicaGave.identificador = "2do Cuad.";
			// dinamicaGave.numeroCuadrante = 2;
			
			// Creamos los trials (uno para cada lado movil)
			for (double ladoMovil : estimulos.get(ladoFijo).keys()) {
				// Seleccionamos el recurso
				EstimuloAngulo recurso = (EstimuloAngulo) estimulos.get(ladoFijo).get(ladoMovil);
				JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el ángulo", "",
							DISTRIBUCIONESenPANTALLA.LINEALx3,
							new int[] {CategoriasImagenes.Agudo.ID, CategoriasImagenes.Recto.ID, CategoriasImagenes.Obtuso.ID},
							TIPOdeTRIAL.TEST, recurso.idResource, false, true, this.setup.feedback);
				recurso.idTrial = trial.Id;
				if ((recurso.anguloFormado <= 90)&(recurso.anguloFormado > 9)) {
					dinamicaAguda.listaEstimulos.add(recurso);
				}
				if ((recurso.anguloFormado < 171)&(recurso.anguloFormado >= 90)) {
					dinamicaGave.listaEstimulos.add(recurso);
				}
				level.jsonTrials.add(trial);
			}
			// Ordenamos las listas de estimulos segun dificultad decreciente y
			// la numeramos
			dinamicaAguda.listaEstimulos.sort();
			dinamicaAguda.listaEstimulos.reverse();
			dinamicaGave.listaEstimulos.sort();  
			for (int i = 0; i < dinamicaAguda.listaEstimulos.size; i++) {
				dinamicaAguda.listaEstimulos.get(i).nivelSenal = i;
			}
			for (int i = 0; i < dinamicaGave.listaEstimulos.size; i++) {
				dinamicaGave.listaEstimulos.get(i).nivelSenal = i;
			}

			// Retocamos la info dinamica
			dinamicaAguda.nivelEstimulo = dinamicaAguda.listaEstimulos.size - 1;
			dinamicaGave.nivelEstimulo = dinamicaGave.listaEstimulos.size - 1;
			dinamicaAguda.saltosActivos = dinamicaAguda.listaEstimulos.size / 10 + 1;
			dinamicaGave.saltosActivos = dinamicaGave.listaEstimulos.size / 10 + 1;
			dinamicaGave.referencia = ladoFijo;
			dinamicaAguda.referencia = ladoFijo;
			
			// Agrupamos todas las convergencias del nivel en un array y lo
			// mandamos a la variable object del level
			Array<DinamicaExperimento> convergencias = new Array<DinamicaExperimento>();
			convergencias.add(dinamicaAguda);
			convergencias.add(dinamicaGave);
			level.infoDinamica = convergencias;
			// Extraemos los niveles y los recursos a la carpeta que corresponda
			PCBuilder.extract(level);
			PCBuilder.buildJsons(level);

			// Agregamos el nivel al setting
			LevelStatus levelStatus = new LevelStatus();
			levelStatus.enabled = true;
			levelStatus.id = level.Id;
			levelStatus.publicName = this.setup.tagButton + "(" + level.Id + ")";
			levelStatus.internalName = this.getName() + level.Id;
			levelStatus.expName = this.getName();
			levelStatus.alreadyPlayed = false;
			levelStatus.priority = this.setup.levelPriority;
			this.expSettings.levels.add(levelStatus);
		}
		// Creamos un archivo con la info del experimento
		String path2 = Resources.Paths.InternalResources + "/" + this.getClass().getSimpleName() + ".settings/";
		Json json2 = new Json();
		json2.setUsePrototypes(false);
		FileHelper.writeLocalFile(path2, json.toJson(this.expSettings));

		
	}

	@Override
	abstract public String getName();

	@Override
	abstract protected String getNameTag();
	
}