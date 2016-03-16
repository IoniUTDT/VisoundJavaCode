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
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.PCBuilder;

public abstract class UmbralParalelismo extends Umbral {

	
	private static class ImageInfoParalelismo extends Umbral.ImageInfo {
		double separacion;
	}

	static final String TAG = UmbralParalelismo.class.getName();
			
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
	public void makeLevels() {

		// Cargamos los datos del setup
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
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
			JsonLevel level = PCBuilder.crearLevel();
			level.numberOfMaxTrials = this.setup.numeroDeTrailsMaximosxNivel;
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
					trial = PCBuilder.crearTrial("Indique a que imagen se parece el sonido", "",
							DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] { map.get(referencia).get(desviacion).idResource,
									map.get(referencia).get(-desviacion).idResource },
							TIPOdeTRIAL.TEST, map.get(referencia).get(desviacion).idResource, false, true, this.setup.feedback);
				} else { // si es muy chica mostramos dos imagenes donde el
							// efecto sea visible (igual como comparten
							// categoria el LevelController detecta la
							// coincidencia
					trial = PCBuilder.crearTrial("Indique a que imagen se parece el sonido", "",
							DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] { map.get(referencia).get(limiteVisible).idResource,
									map.get(referencia).get(-limiteVisible).idResource },
							TIPOdeTRIAL.TEST, map.get(referencia).get(desviacion).idResource, false, true, this.setup.feedback);
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
			// dinamicaPos.anguloDeReferencia = referencia;
			// dinamicaNeg.anguloDeReferencia = referencia;
			dinamicaPos.convergenciaAlcanzada = false;
			dinamicaNeg.convergenciaAlcanzada = false;
			dinamicaPos.referencia = referencia;
			dinamicaNeg.referencia = referencia;
			dinamicaPos.nivelEstimulo = dinamicaPos.listaEstimulos.size - 1;
			dinamicaNeg.nivelEstimulo = dinamicaNeg.listaEstimulos.size - 1;
			dinamicaPos.saltosActivos = dinamicaPos.listaEstimulos.size / 10 + 1;
			dinamicaNeg.saltosActivos = dinamicaNeg.listaEstimulos.size / 10 + 1;

			// Agrupamos todas las convergencias del nivel en un array y lo
			// mandamos a la variable object del level
			Array<DinamicaExperimento> convergencias = new Array<DinamicaExperimento>();
			convergencias.add(dinamicaPos);
			convergencias.add(dinamicaNeg);
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
		String path2 = Resources.Paths.finalInternalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		Json json2 = new Json();
		json2.setUsePrototypes(false);
		FileHelper.writeLocalFile(path2, json.toJson(this.expSettings));
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
		ImageInfoParalelismo info = new ImageInfoParalelismo();

		float largo = tamano * 0.8f;
		float separacion = tamano * 0.4f;

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
			imagen.categories.add(CategoriasImagenes.Diverge);
		}
		if (info.desviacion < 0) {
			imagen.categories.add(CategoriasImagenes.Converge);
		}
		if (info.desviacion == 0) {
			imagen.categories.add(CategoriasImagenes.Paralelas);
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
		// Inicializamos el setup segun parametros
		this.makeSetup();
		// Creamos un recurso para cada imagen necesaria
		for (double referencia : this.setup.angulosReferencia) {
			for (double desviacion : this.setup.desviacionesAngulares) {
				makeResource(referencia, desviacion);
			}
		}
		// Guardamos el setup
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(this.setup));
	}

	abstract void makeSetup();


	abstract public String getName();

	@Override
	abstract protected String getNameTag();

}
