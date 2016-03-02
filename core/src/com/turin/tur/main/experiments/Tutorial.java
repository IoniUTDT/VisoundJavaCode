package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.experiments.Experiments.ExpSettings;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class Tutorial extends GenericExp implements Experiment {

	String expName = "Tutorial";
	private Setup setup;
	TIPOdeEXPERIMENTO tipoDeExperimento = TIPOdeEXPERIMENTO.TutorialBasico;
	private DinamicaTutorial dinamicaActiva;
	
	
	private static class Setup {
		Array<Recurso> listaRecursos = new Array<Recurso>();
	}
	
	private static class Recurso {
		int idRecurso;
		String tag;
	}
	
	private static class DinamicaTutorial {
		Array<Integer> listaDeTrials = new Array<Integer>();
		int trialActivo;
	}
	
	private static class Dibujo {
		String tag;
		Array<Linea> lineas = new Array<Linea>();
	}
	
	@Override
	public String getName() {
		return this.expName;
	}

	@Override
	public void makeResources() {
		
		// Creamos una lista de las lineas a dibujar
		Array<Dibujo> dibujos = this.createDibujos ();
		
		// Inicializamos el setup
		this.setup = new Setup();
		
		for (Dibujo dibujo : dibujos) {
			// Creamos la imagen correspondiente
			Imagenes imagen = new Imagenes();
			imagen.categories.add(CategoriasImagenes.Tutorial);
			imagen.infoConceptual = dibujo;
			imagen.lineas.addAll(dibujo.lineas);
			// Hacemos de la info de la imagen el SVG
			imagen.toSVG();
			Recurso recurso = new Recurso();
			recurso.idRecurso = imagen.resourceId.id;
			recurso.tag = dibujo.tag;
			
			this.setup.listaRecursos.add(recurso);
		}
		
		// Guardamos el setup
		String path = Resources.Paths.currentVersionPath + "/extras/" + this.expName + "Setup.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.setup));
		

	}

	private Array<Dibujo> createDibujos() {
		Array<Dibujo> dibujos = new Array<Dibujo>();
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width > Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		double largo = tamano * 0.8;
		
		Dibujo dibujo = new Dibujo();
		dibujo.tag = "HorizontalArriba";
		Linea linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/10;
		linea.radial.angulo = 0;
		linea.radial.largo = largo;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Linea horizontal medio
		dibujo = new Dibujo();
		dibujo.tag = "HorizontalMedio";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 0;
		linea.radial.largo = largo;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Linea horizontal bajo
		dibujo = new Dibujo();
		dibujo.tag = "HorizontalBajo";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/10*9;
		linea.radial.angulo = 0;
		linea.radial.largo = largo;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea Vertical alta
		dibujo = new Dibujo();
		dibujo.tag = "VerticalArriba";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/4;
		linea.radial.angulo = 90;
		linea.radial.largo = tamano/2.5;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea Vertical completa
		dibujo = new Dibujo();
		dibujo.tag = "VerticalCompleta";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 90;
		linea.radial.largo = largo;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea Vertical abajo
		dibujo = new Dibujo();
		dibujo.tag = "VerticalAbajo";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/4*3;
		linea.radial.angulo = 90;
		linea.radial.largo = tamano/2.5;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Para la segunda pantalla
		
		// Linea que crece suavemente
		dibujo = new Dibujo();
		dibujo.tag = "DiagSuave";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 20;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea que crece no tan suavemente
		dibujo = new Dibujo();
		dibujo.tag = "DiagMedio";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 45;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea que crece rapido
		dibujo = new Dibujo();
		dibujo.tag = "DiagRapida";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 80;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea que decrece muy lento
		dibujo = new Dibujo();
		dibujo.tag = "DiagNegMuyLenta";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = -5;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Linea que decrece bastante rapido
		dibujo = new Dibujo();
		dibujo.tag = "DiagNegMedioRapida";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = -75;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Linea que decrece muy rapido
		dibujo = new Dibujo();
		dibujo.tag = "DiagNegMuyRapido";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = -88;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		
		
		
		return dibujos;
		
	}

	@Override
	public void makeLevels() {
		// Cargamos los datos del setup
		String path = Resources.Paths.currentVersionPath + "/extras/" + this.expName + "Setup.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.setup = json.fromJson(Tutorial.Setup.class, savedData);

		// Inicializamos el expSettings;
		this.expSettings = new ExpSettings();
		this.expSettings.tipoDeExperimento = this.tipoDeExperimento;

		// Creamos un map con todos los recursos
		ArrayMap<String, Recurso> recursosTag = new ArrayMap<String, Recurso>();
		for (Recurso recurso : this.setup.listaRecursos) {
			recursosTag.put(recurso.tag, recurso);
		}
		
		// Hacemos el Nivel 1 que contiene cosas basica (las cosas especificas estan en el tutorial parte dos)
		JsonLevel tutorial = Builder.crearLevel();
		tutorial.levelTitle = "Tutorial basico";
		
		DinamicaTutorial dinamica = new DinamicaTutorial();
		
		// Creamos el trial uno, con segmentos rectos, horizontales y verticuales.
		JsonTrial trial = Builder.crearTrial("Selecciones la imagen que desea escuchar", "",
				DISTRIBUCIONESenPANTALLA.BILINEALx7,
				new int[] {recursosTag.get("HorizontalArriba").idRecurso, recursosTag.get("HorizontalMedio").idRecurso, recursosTag.get("HorizontalBajo").idRecurso,
						recursosTag.get("VerticalArriba").idRecurso, recursosTag.get("VerticalCompleta").idRecurso, recursosTag.get("VerticalAbajo").idRecurso,
						CategoriasImagenes.Siguiente.ID},
				TIPOdeTRIAL.ENTRENAMIENTO, CategoriasImagenes.Siguiente.ID, false, false, false);
		dinamica.listaDeTrials.add(trial.Id);
		dinamica.trialActivo = trial.Id;
		tutorial.jsonTrials.add(trial);
		
		// Creamos el trial dos, con segmentos rectos, pero en diagonal.
		JsonTrial trial2 = Builder.crearTrial("Selecciones la imagen que desea escuchar", "",
				DISTRIBUCIONESenPANTALLA.BILINEALx7,
				new int[] {recursosTag.get("DiagSuave").idRecurso, recursosTag.get("DiagMedio").idRecurso, recursosTag.get("DiagRapida").idRecurso,
						recursosTag.get("DiagNegMuyLenta").idRecurso, recursosTag.get("DiagNegMedioRapida").idRecurso, recursosTag.get("DiagNegMuyRapido").idRecurso,
						CategoriasImagenes.Siguiente.ID},
				TIPOdeTRIAL.ENTRENAMIENTO, CategoriasImagenes.Siguiente.ID, false, false, false);
		dinamica.listaDeTrials.add(trial2.Id);
		tutorial.jsonTrials.add(trial2);
		
		// Preparamos el level y lo exportamos
		tutorial.infoDinamica = dinamica;
		// Extraemos los niveles y los recursos a la carpeta que corresponda
		Builder.extract(tutorial);
		Builder.buildJsons(tutorial);
		// Agregamos el nivel al setting
		LevelStatus levelStatus = new LevelStatus();
		levelStatus.enabled = true;
		levelStatus.id = tutorial.Id;
		levelStatus.publicName = tutorial.levelTitle;
		levelStatus.internalName = this.expName + tutorial.Id;
		levelStatus.expName = this.expName;
		levelStatus.alreadyPlayed = false;
		this.expSettings.levels.add(levelStatus);

		// Creamos un archivo con la info del experimento
		String path2 = Resources.Paths.finalPath + "/" + this.getClass().getSimpleName() + ".settings/";
		Json json2 = new Json();
		json2.setUsePrototypes(false);
		FileHelper.writeFile(path2, json.toJson(this.expSettings));

	}

	@Override
	public void createTrial() {
		// Creamos el trial correspondiente
		String savedData = FileHelper.readFile(Resources.Paths.resources + "level" + level.Id + "/trial" + this.dinamicaActiva.trialActivo + ".meta");
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

	@Override
	public void returnAnswer(boolean answer) {
		// Cambiamos el trial al siguiente de la lista
		if (this.askNoMoreTrials()) { 
			this.levelCompleted = true;
			// Aca iria alguna accion como reportar algo
		} else {
			int thisTrialIndex = this.dinamicaActiva.listaDeTrials.indexOf(this.dinamicaActiva.trialActivo, false);
			this.dinamicaActiva.trialActivo = this.dinamicaActiva.listaDeTrials.get(thisTrialIndex +1);
			this.createTrial();
		} 	
	}

	@Override
	public void initLevel(Level level) {
		// Cargamos los datos especificos del nivel
		this.level = level;
		this.dinamicaActiva = (DinamicaTutorial) level.jsonLevel.infoDinamica;
		this.assets = new LevelAsset(level.Id);
		this.event_initLevel();
		this.createTrial();
	}

	@Override
	public void stopLevel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean askNoMoreTrials() {
		if (this.trialsLeft() == 0) {
			this.levelCompleted();
			return true;
		} else {
			return false;
		}
	}

	private void levelCompleted() {
		// TODO Ver que onda si hace falta hacer algo cuando se completa el nivel, por ahora no.
	}

	@Override
	public void interrupt() {
		// TODO Ver que onda si hace falta hacer algo cuando se sale del nivel, por ahora no.
	}

	@Override
	public int trialsLeft() {
		int thisTrialIndex = this.dinamicaActiva.listaDeTrials.indexOf(this.dinamicaActiva.trialActivo, false);
		return this.dinamicaActiva.listaDeTrials.size - (thisTrialIndex + 1);
	}
	
	String getNameTag() {
		return "Tutorial";
	}

}
