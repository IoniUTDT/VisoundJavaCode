package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
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
		private double anguloLadoMovil; // Angulo absoluto del lado movil
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(this.desviacion).compareTo(o.desviacion);
		}
		
	}

	
	private static class ImageInfoAngulo extends Umbral.ImageInfo {
		double anguloLadoMovil;
	}
	
	
	protected void makeResource(double ladoFijo, double anguloAFormar) {
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

		// A partir del angulo a formar, lo orientamos segun haga falta. En este experimento el angulo se forma hacia la izq si es mayor que noventa el lado fijo y hacia derecha si es menor.
		if (ladoFijo < 90) {anguloAFormar = -anguloAFormar;}
		double anguloLadoMovil = (ladoFijo + anguloAFormar);
		
		// Nota: los ejes son cartesianos y hacia abajo, x hacia la derecha
		info.linea1.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) ladoFijo);
		info.linea1.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) ladoFijo);
		info.linea2.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) anguloLadoMovil);
		info.linea2.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) anguloLadoMovil);
		
		
		info.linea1.radial.angulo = ladoFijo;
		info.linea2.radial.angulo = anguloLadoMovil;

		info.linea1.radial.largo = largoLados;
		info.linea2.radial.largo = largoLados;

		info.anguloLadoMovil = anguloLadoMovil;
		info.referencia = ladoFijo;
		info.desviacion = anguloAFormar%360; //El angulo tiene signo, y esta entre -360 y 360 
		
		// Pasamos la info a formato cartesiano
		info.linea1.lineaFromRadial();
		info.linea2.lineaFromRadial();

		// Creamos la imagen correspondiente
		Imagenes imagen = new Imagenes();

		// Cargamos la info conceptual (que varia segun el tipo de experimento)
		imagen.infoConceptual = info;

		
		// Creamos las categorias correspondientes (para angulos de -360 a 360)
		if ((info.desviacion < -270) & (info.desviacion >= -360)){
			imagen.categories.add(CategoriasImagenes.Agudo);
		}
		if (info.desviacion == -270){
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < -90) & (info.desviacion > -270)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
		}
		if (info.desviacion == -90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > -90) & (info.desviacion < 90)){
			imagen.categories.add(CategoriasImagenes.Agudo);
		}
		if (info.desviacion == 90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < 270) & (info.desviacion > 90)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
		}
		if (info.desviacion == 270) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > 270) & (info.desviacion <= 360)){
			imagen.categories.add(CategoriasImagenes.Agudo);
		}
		// Agregamos las dos lineas para que se dibujen
		imagen.lineas.add(info.linea1);
		imagen.lineas.add(info.linea2);

		// Hacemos de la info de la imagen el SVG
		imagen.toSVG();

		// Agregamos al setup el recurso
		EstimuloAngulo estimulo = new EstimuloAngulo();
		estimulo.idResource = imagen.resourceId.id;
		estimulo.anguloLadoMovil = info.anguloLadoMovil;
		estimulo.anguloFijo = info.referencia;
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
		this.expSettings.tipoDeExperimento = TIPOdeEXPERIMENTO.TestAngulos;

		
		// Categorizamos los recursos una vez.
		ArrayMap<Double, ArrayMap<Double, Estimulo>> estimulosByAngulos = this.indexToMap(); 
		
		// Hacemos un nivel para cada referencia
		for (double referencia : this.setup.angulosReferencia) {
			// Creamos el nivel
			JsonLevel level = PCBuilder.crearLevel();
			level.levelTitle = "Angulos: " + referencia;

			// Creamos el elemento de la info dinamica que corresponde al nivel
			DinamicaExperimento dinamicaExperimento = new DinamicaExperimento();
			dinamicaExperimento.identificador = "Dinamica, "+referencia+" grados";
			dinamicaExperimento.referencia = referencia;
			dinamicaExperimento.trialsPorNivel = this.setup.trialsPorNivel;
			
			// TODO : crear u agregar los estimulos cero
			
			// Creamos las series 
			for (double variacion : this.setup.fluctuacionesLocalesReferenciaSeries) {
				double anguloFijo = referencia + variacion;
				
				// Creamos la serie
				SerieEstimulos serieAgudos = new SerieEstimulos();
				SerieEstimulos serieObtusos = new SerieEstimulos();
				// Las configuramos
				serieAgudos.desdeAgudosOPos = true;
				serieAgudos.identificador = "Orientacion:"+anguloFijo+"Ag";
				serieAgudos.ladoFijo = anguloFijo;
				serieObtusos.desdeAgudosOPos = false;
				serieObtusos.identificador = "Orientacion:"+anguloFijo+"ob";
				serieObtusos.ladoFijo = anguloFijo;
				
				// Creamos los trials (uno para cada lado movil)
				for (double anguloMovil : estimulosByAngulos.get(anguloFijo).keys()) {
					// Seleccionamos el recurso
					EstimuloAngulo recurso = (EstimuloAngulo) estimulosByAngulos.get(anguloFijo).get(anguloMovil);
					JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el ángulo", "",
								DISTRIBUCIONESenPANTALLA.LINEALx2,
								new int[] {CategoriasImagenes.Recto.ID, CategoriasImagenes.NoRecto.ID},
								TIPOdeTRIAL.TEST, recurso.idResource, false, true, this.setup.feedback);
					recurso.idTrial = trial.Id;
					
					// Agregamos a la dinamica que corresponda
					if ((recurso.desviacion < -270) & (recurso.desviacion >= -360)){
						serieAgudos.listaEstimulos.add(recurso);
					}
					if ((recurso.desviacion < -90) & (recurso.desviacion > -270)){
						serieObtusos.listaEstimulos.add(recurso);
					}
					if ((recurso.desviacion > -90) & (recurso.desviacion < 90)){
						serieAgudos.listaEstimulos.add(recurso);
					}
					if ((recurso.desviacion < 270) & (recurso.desviacion > 90)){
						serieObtusos.listaEstimulos.add(recurso);
					}
					if ((recurso.desviacion > 270) & (recurso.desviacion <= 360)){
						serieAgudos.listaEstimulos.add(recurso);
					}
					
					// Agregamos el trial creado al level
					level.jsonTrials.add(trial);
				}
				
				// Ordenamos las listas de estimulos segun dificultad decreciente y
				// la numeramos
				serieAgudos.listaEstimulos.sort();
				serieObtusos.listaEstimulos.sort();
				serieObtusos.listaEstimulos.reverse();
				
				// Numeramos los recursos por dificultad
				for (int i = 1; i <= serieAgudos.listaEstimulos.size; i++) {
					serieAgudos.listaEstimulos.get(i-1).nivelSenal = i;
				}
				for (int i = 1; i <= serieObtusos.listaEstimulos.size; i++) {
					serieObtusos.listaEstimulos.get(i-1).nivelSenal = i;
				}

				// Agregamos las dos series a la dinamica
				dinamicaExperimento.seriesEstimulos.add(serieAgudos);
				dinamicaExperimento.seriesEstimulos.add(serieObtusos);
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

	protected void generarDesviaciones (Setup setup) {
		super.generarDesviaciones(setup);
		for (int i=0 ; i < setup.desviacionesAngulares.size; i++) {
			setup.desviacionesAngulares.set(i, setup.desviacionesAngulares.get(i)+90);
		}
	}
		
	@Override
	abstract public String getName();

	@Override
	abstract protected String getNameTag();
	
}