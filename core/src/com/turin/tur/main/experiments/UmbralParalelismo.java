package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.PCBuilder;

public class UmbralParalelismo extends Umbral {

	
	private static class ImageInfoParalelismo extends Umbral.ImageInfo {
		double separacion;
	}

	static final String TAG = UmbralParalelismo.class.getName();
			
	@Override
	public void makeLevels() {

		// Cargamos los datos del setup
		String path = Resources.Paths.ResourcesBuilder + "/extras/" + this.getName() + "Setup.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.setup = json.fromJson(UmbralParalelismo.Setup.class, savedData);

		// Creamos el setting del experimento
		this.expSettings = new LevelsStatus();
		this.expSettings.tipoDeExperimento = TIPOdeEXPERIMENTO.TestParalelismo;

		// Categorizamos los recursos en un mapa
		ArrayMap<Double, ArrayMap<Double, Estimulo>> estimulosByAngulos = this.indexToMap();

		// Hacemos un nivel para cada referencia
		for (double referencia : this.setup.angulosReferencia) {
			// Creamos el nivel
			JsonLevel level = PCBuilder.crearLevel();
			level.levelTitle = "Paralelismo: " + referencia;

			// Creamos el elemento de la info dinamica que corresponde al nivel
			DinamicaExperimento dinamicaExperimento = new DinamicaExperimento();
			dinamicaExperimento.identificador = "Dinamica, "+referencia+" grados";
			dinamicaExperimento.referencia = referencia;
			dinamicaExperimento.trialsPorNivel = this.setup.trialsPorNivel;

			
			
			// Creamos las series
			
			for (double variacion : this.setup.fluctuacionesLocalesReferenciaSeries) {
				double anguloFijo = referencia + variacion;
			
				// Creamos la serie
				SerieEstimulos seriePos = new SerieEstimulos();
				SerieEstimulos serieNeg = new SerieEstimulos();
				// Las configuramos
				seriePos.desdeAgudosOPos = true;
				seriePos.identificador = "Orientacion:"+anguloFijo+"SeriePos";
				seriePos.ladoFijo = anguloFijo;
				serieNeg.desdeAgudosOPos = false;
				serieNeg.identificador = "Orientacion:"+anguloFijo+"SerieNeg";
				serieNeg.ladoFijo = anguloFijo;
				
				// Creamos los trials (uno para cada desviacion)
				for (double desviacion : this.setup.desviacionesAngulares) {
					
					Estimulo recurso = estimulosByAngulos.get(anguloFijo).get(desviacion);
					JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el estímulo", "",
								DISTRIBUCIONESenPANTALLA.LINEALx2,
								new int[] {CategoriasImagenes.Paralelas.ID, CategoriasImagenes.NoParalelas.ID},
								TIPOdeTRIAL.TEST, recurso.idResource, false, true, this.setup.feedback);
					recurso.idTrial = trial.Id;

					// Agregamos a la dinamica que corresponda (aca se ignoran los estimulos de señal CERO
					if (recurso.desviacion > 0) {
						seriePos.listaEstimulos.add(recurso);
					}
					if (recurso.desviacion < 0) {
						serieNeg.listaEstimulos.add(recurso);
					}
					
					// Agregamos el trial al nivel
					level.jsonTrials.add(trial);
				}
				
				// Ordenamos las listas de estimulos segun dificultad decreciente y
				// la numeramos
				seriePos.listaEstimulos.sort();
				seriePos.listaEstimulos.reverse();
				serieNeg.listaEstimulos.sort();
				
				// Numeramos los recursos por dificultad
				for (int i = 1; i <= seriePos.listaEstimulos.size; i++) {
					seriePos.listaEstimulos.get(i-1).nivelSenal = i;
				}
				for (int i = 1; i <= serieNeg.listaEstimulos.size; i++) {
					serieNeg.listaEstimulos.get(i-1).nivelSenal = i;
				}
				
				// Agregamos las dos series a la dinamica
				dinamicaExperimento.seriesEstimulos.add(seriePos);
				dinamicaExperimento.seriesEstimulos.add(serieNeg);
			}

			// Creamos el conjunto de estimulos cero
			for (Double variacion : setup.fluctuacionesLocalesReferenciaEstimuloCero) {
				// Creamos el trial
				// Seleccionamos el recurso
				Estimulo recurso = estimulosByAngulos.get(referencia+variacion).get(0d);
				JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el estímulo", "",
						DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {CategoriasImagenes.Paralelas.ID, CategoriasImagenes.NoParalelas.ID},
						TIPOdeTRIAL.TEST, recurso.idResource, false, true, this.setup.feedback);
				recurso.idTrial = trial.Id;
				dinamicaExperimento.estimulosCeros.add(recurso);
				// Agregamos el trial creado al level
				level.jsonTrials.add(trial);
			}
			
			level.dinamicaExperimento = dinamicaExperimento;
			level.setup = setup;
			level.setup.estimulos.clear();
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

	protected void makeResource(double referencia, double desviacion) {
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
		// Nota: los ejes son cartesianos y hacia abajo, x hacia la derecha
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
			imagen.categories.add(CategoriasImagenes.NoParalelas);
		}
		if (info.desviacion < 0) {
			imagen.categories.add(CategoriasImagenes.Converge);
			imagen.categories.add(CategoriasImagenes.NoParalelas);
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
		estimulo.anguloFijo = info.referencia;
		this.setup.estimulos.add(estimulo);
	}

	void makeSetup();
	public String getName();

	float getDesviacionCero() {
		return 0;
	}
}
