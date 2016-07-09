package com.turin.tur.main.experiments;
/*
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.LevelOLD.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.levelsDesign.LevelUmbral;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.ResourcesCategorias.CategoriasImagenes;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.PCBuilder;


public class UmbralAngulos extends Umbral {

	static final String TAG = UmbralParalelismo.class.getName();
	// Cosas generales
	
	/*
	private static class EstimuloAngulo extends Umbral.Estimulo implements Comparable<Estimulo> {
		private double anguloLadoMovil; // Angulo absoluto del lado movil
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(this.desviacion).compareTo(o.desviacion);
		}
		
	}
	*/
	
	/*
	private static class ImageInfoAngulo extends Umbral.ImageInfo {
		double anguloLadoMovil;
	}
	*/
	
	/*
	protected Estimulo makeResource(double ladoFijo, double anguloAFormar) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (ResourcesCategorias.Display.width > ResourcesCategorias.Display.height) {
			tamano = ResourcesCategorias.Display.height;
		} else {
			tamano = ResourcesCategorias.Display.width;
		}

		// Creamos la info conceptual de la imagen
		ImageInfoAngulo info = new ImageInfoAngulo();

		float largoLados = tamano * 0.4f;

		float Xcenter = ResourcesCategorias.Display.width / 2;
		float Ycenter = ResourcesCategorias.Display.height / 2;

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
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == -270){
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < -90) & (info.desviacion > -270)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == -90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > -90) & (info.desviacion < 90)){
			imagen.categories.add(CategoriasImagenes.Agudo);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == 90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < 270) & (info.desviacion > 90)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == 270) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > 270) & (info.desviacion <= 360)){
			imagen.categories.add(CategoriasImagenes.Agudo);
			imagen.categories.add(CategoriasImagenes.NoRecto);
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
		return estimulo;
	}
	*/
/*
	public void buildLevels() {
		
		for (SetupLevel setupLevel : this.setupsLevels) {
			// Categorizamos los recursos una vez.
			ArrayMap<Double, ArrayMap<Double, Estimulo>> estimulosByAngulos = LevelUmbral.indexToMap(LevelU setupLevel.setupResources); 

			// Creamos el nivel
			JsonLevel level = PCBuilder.crearLevel();
			level.levelTitle = "Angulos: " + setupLevel.referencia;

			// Creamos el elemento de la info dinamica que corresponde al nivel
			DinamicaExperimento dinamicaExperimento = new DinamicaExperimento();
			dinamicaExperimento.identificador = "Dinamica, "+setupLevel.referencia+" grados";
			dinamicaExperimento.referencia = setupLevel.referencia;
			dinamicaExperimento.trialsPorNivel = setupLevel.trialsPorNivel;

			// Creamos las series 
			for (double variacion : setupLevel.setupResources.fluctuacionesLocalesReferenciaSeries) {
				double anguloFijo = setupLevel.referencia + variacion;

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
				for (double anguloMovil : estimulosByAngulos.get(anguloFijo).keys()) { // Esto incluye a los estimulos con nivel de estimulo cero que tienen el lado fijo en la referencia, pero despues se los ignora.
					// Seleccionamos el recurso
					EstimuloAngulo recurso = (EstimuloAngulo) estimulosByAngulos.get(anguloFijo).get(anguloMovil);
					JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el ángulo", "",
							DISTRIBUCIONESenPANTALLA.LINEALx2,
							new int[] {CategoriasImagenes.Recto.ID, CategoriasImagenes.NoRecto.ID},
							TIPOdeTRIAL.TEST, recurso.idResource, false, true, setupLevel.feedback);
					recurso.idTrial = trial.Id;

					// Agregamos a la dinamica que correspondam (nota, aca se elimina de la lista de recursos a los que tienen estinulo 0, osea desviacion 90)
					if ((recurso.desviacion < -270) & (recurso.desviacion > -360)){
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
					if ((recurso.desviacion > 270) & (recurso.desviacion < 360)){
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
				
			// Creamos el conjunto de estimulos cero
			for (Double variacion : setupLevel.setupResources.fluctuacionesLocalesReferenciaEstimuloCero) {
				// Creamos el trial
				// Seleccionamos el recurso
				EstimuloAngulo recurso;
				if ((setupLevel.referencia+variacion) > 90) {  
					recurso = (EstimuloAngulo) estimulosByAngulos.get(setupLevel.referencia+variacion).get(90d);
				} else {
					recurso = (EstimuloAngulo) estimulosByAngulos.get(setupLevel.referencia+variacion).get(-90d);
				}
				JsonTrial trial = PCBuilder.crearTrial("Indique a que categoría pertenece el ángulo", "",
						DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {CategoriasImagenes.Recto.ID, CategoriasImagenes.NoRecto.ID},
						TIPOdeTRIAL.TEST, recurso.idResource, false, true, setupLevel.feedback);
				recurso.idTrial = trial.Id;
				dinamicaExperimento.estimulosCeros.add(recurso);
				// Agregamos el trial creado al level
				level.jsonTrials.add(trial);
			}

			level.dinamicaExperimento = dinamicaExperimento;
			level.setupLevel = setupLevel;
			level.setupLevel.setupResources.estimulos.clear();
			// Extraemos los niveles y los recursos a la carpeta que corresponda
			PCBuilder.extract(level);
			PCBuilder.buildJsons(level);

			// Agregamos el nivel al setting
			LevelStatus levelStatus = new LevelStatus();
			levelStatus.enabled = true;
			levelStatus.id = level.Id;
			levelStatus.publicName = setupLevel.tagButton + "(" + level.Id + ")";
			levelStatus.internalName = setupLevel.SetupLevelName + level.Id;
			levelStatus.expName = this.getExpName();
			levelStatus.alreadyPlayed = false;
			levelStatus.priority = setupLevel.levelPriority;
			this.levelsStatus.add(levelStatus);
		}

		// Creamos un archivo con la info del experimento
		String path = ResourcesCategorias.Paths.finalInternalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(this.levelsStatus));
	}

	
	protected void generarDesviaciones (SetupResources setup) {
		super.generarDesviaciones(setup);
		for (int i=0 ; i < setup.desviacionesAngulares.size; i++) {
			setup.desviacionesAngulares.set(i, setup.desviacionesAngulares.get(i)+90);
		}
	}
	
	float getDesviacionCero() {
		return 90;
	}
	
	
	private SetupResources makeResourceSetupTutorial() {
		
		// Hacemos el setup para el tutorial
		SetupResources setupResources = new SetupResources();
				
		// Definimos los angulos de referencia
		setupResources.angulosReferencia.add(180d);
		
		// Definimos las fluctuaciones locales
		setupResources.fluctuacionesLocalesReferenciaSeries.add(0f);
		
		// Definimos las fluctuaciones para el cero
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(0d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(2.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-2.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(5d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-5d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(7.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-7.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(10d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-10d);
		
		// Definimos la cantidad de estimulos por serie
		setupResources.numeroDeEstimulosPorSerie = 10;
		
		// Definimos cosas varias
		setupResources.desvMax = 80;
		setupResources.desvMin = 1;
		setupResources.SetupResourcesName = "Tutorial";
		return setupResources; 
	}

	private SetupResources makeResourceTransferencia() {

		// Hacemos el setup para el tutorial
		SetupResources setupResources = new SetupResources();
				
		// Definimos los angulos de referencia
		setupResources.angulosReferencia.add(30d);
		setupResources.angulosReferencia.add(60d);
		setupResources.angulosReferencia.add(120d);
		setupResources.angulosReferencia.add(150d);

		// Definimos las fluctuaciones locales
		setupResources.fluctuacionesLocalesReferenciaSeries.add(0f);
		setupResources.fluctuacionesLocalesReferenciaSeries.add(5f);
		setupResources.fluctuacionesLocalesReferenciaSeries.add(-5f);
		
		// Definimos las fluctuaciones para el cero
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(0d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(2.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-2.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(5d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-5d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(7.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-7.5);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(10d);
		setupResources.fluctuacionesLocalesReferenciaEstimuloCero.add(-10d);
		
		// Definimos cosas varias
		setupResources.numeroDeEstimulosPorSerie = 50;
		setupResources.desvMin = 1;
		setupResources.desvMax = 80;
		setupResources.SetupResourcesName = "ExperimentoTransferencia";
		return setupResources; 
	}
	
	
	@Override
	public String getResourcesName() {
		return this.setupActivo.setupResources.SetupResourcesName;
	}

	@Override
	public String getLevelName() {
		return this.setupActivo.SetupLevelName;
	}

	@Override
	void makeSetup() {
		this.setupsResources.add(this.makeResourceSetupTutorial());
		this.setupsResources.add(this.makeResourceTransferencia());
		for (SetupResources setup : this.setupsResources) {
			this.generarDesviaciones(setup);
		}
	}

	@Override
	String getExpName() {
		return "UmbralAngulo";
	}

	@Override
	public void makeLevels() {
		// cargamos los archivos con el setup guardados durante la creacion de recursos
		String path = ResourcesCategorias.Paths.ResourcesBuilder + "/extras/" + this.getExpName() + "SetupResources.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.setupsResources = json.fromJson(this.setupsResources.getClass(), savedData);
		this.setupsLevels.add(this.makeLevelTutorial());
		this.buildLevels();
	}

	private SetupLevel makeLevelTutorial() {
		// Creamos el setup desde la super clase
		SetupLevel setup = new SetupLevel();
		setup.setupResources = this.searchByName("Tutorial");
		setup.allTestsConfianza = false;
		setup.confianceProbability = 0.3f;
		setup.feedback = true;
		setup.levelPriority = 1;
		setup.referencia = 180d;
		setup.SetupLevelName = "NivelTutorial";
		setup.testProbability = 0.2f;
		setup.tagButton = "Tutorial Angulos";
		setup.trialsPorNivel = 10;
		return setup;
	}
	
	private SetupResources searchByName (String name) {
		for (SetupResources setup : this.setupsResources) {
			if (setup.SetupResourcesName.equals(name)) {
				return setup;
			}
		}
		Gdx.app.debug(TAG, "Se esta buscando un setupResource llamado "+ name + " que no existe");
		return null;
	}

}*/