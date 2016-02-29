package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class Tutorial implements Experiment {

	String expName = "Tutorial";
	Setup setup;
	TIPOdeEXPERIMENTO tipoDeExperimento = TIPOdeEXPERIMENTO.TutorialBasico;
	
	private class Setup {
		Array<Recurso> listaRecursos = new Array<Recurso>();
	}
	
	private class Recurso {
		int idRecurso;
		String tag;
	}
	
	private class DinamicaTutorial {
		Array<Integer> listaDeTrials = new Array<Integer>();
		int proximoTrial;
	}
	
	private class Dibujo {
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
		dibujo.tag = "Vertical arriba";
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
		dibujo.tag = "Vertical completa";
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
		dibujo.tag = "Vertical abajo";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/4*3;
		linea.radial.angulo = 90;
		linea.radial.largo = tamano/2.5;
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

		// Hacemos el Nivel 1 que contiene cosas basica (las cosas especificas estan en el tutorial parte dos)
		JsonLevel tutorial = Builder.crearLevel();
		tutorial.levelTitle = "Tutorial basico";
		
		
	}

	@Override
	public Trial getTrial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void askNext() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnAnswer(boolean answer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initGame(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initLevel(Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopLevel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Array<LevelStatus> levelsStatus() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public int trialsLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

}
