package com.turin.tur.main.levelsDesign;

import com.turin.tur.main.diseno.Listas.LISTAdeNIVELES;
import com.turin.tur.main.diseno.Listas.LISTAdeRECURSOS;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.Constants.ResourcesCategorias.CategoriasImagenes;
import com.turin.tur.main.util.Constants.ResourcesCategorias.Paths;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Imagenes.Linea;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial;

public class TutorialLevel extends Level{
	
	public TutorialLevel(LISTAdeNIVELES identificador) {
		this.identificadorNivel = identificador;
		this.loadInfoLevel();
	}

	@Override
	public Trial getNextTrial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion, float timeConfiance,
			int loopsCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean islevelCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void levelCompleted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean goConfiance() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void buildResources(LISTAdeRECURSOS identificador) {
		// Creamos una lista de las lineas a dibujar
		Array<Dibujo> dibujos = createDibujos();
		
		// Inicializamos el setup
		Setup setup = new Setup();
		
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
			
			setup.listaRecursos.add(recurso);
		}
		
		saveSetup(setup, identificador);
	}
	 
	private static class Recurso {
		int idRecurso;
		String tag;
	}
	
	private static class Setup {
		private Array<Recurso> listaRecursos = new Array<Recurso>();
	}
	
	private static void saveSetup(Setup setup, LISTAdeRECURSOS identificador) {
		// Guardamos el setup en la carpeta temporal
		String path = Paths.ResourcesBuilder + Paths.ExtraFldr + identificador.toString() + Paths.ResourcesSetupExt;
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(setup));
	}
	
	private static Array<Dibujo> createDibujos() {
		Array<Dibujo> dibujos = new Array<Dibujo>();
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (ResourcesCategorias.Display.width > ResourcesCategorias.Display.height) {
			tamano = ResourcesCategorias.Display.height;
		} else {
			tamano = ResourcesCategorias.Display.width;
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

		// Para el tercer trial, el de mas de una linea
		
		// Lineas paralelas 1 parte 1
		dibujo = new Dibujo();
		dibujo.tag = "Paralelas1";
		linea = new Linea();
		linea.radial.Xcenter = tamano/3;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 60;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		linea.radial.Xcenter = tamano/3*2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 60;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Lineas paralelas 2 P1
		dibujo = new Dibujo();
		dibujo.tag = "Paralelas2P1";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/3;
		linea.radial.angulo = 10;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Lineas paralelas 2 P2
		dibujo = new Dibujo();
		dibujo.tag = "Paralelas2P2";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/3*2;
		linea.radial.angulo = 10;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Lineas paralelas 2 
		dibujo = new Dibujo();
		dibujo.tag = "Paralelas2";
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/3;
		linea.radial.angulo = 10;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/3*2;
		linea.radial.angulo = 10;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Lineas NO paralelas 1
		dibujo = new Dibujo();
		dibujo.tag = "NoParalelas1";
		linea = new Linea();
		linea.radial.Xcenter = tamano/3;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 70;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		linea.radial.Xcenter = tamano/2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 30;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Lineas No paralelas 2
		dibujo = new Dibujo();
		dibujo.tag = "NoParalelas2";
		linea = new Linea();
		linea.radial.Xcenter = tamano/5*2;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = -20;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		linea.radial.Xcenter = tamano/5*3;
		linea.radial.Ycenter = tamano/2;
		linea.radial.angulo = 20;
		linea.radial.largo = tamano*0.8;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Figuras para el tutorial de angulos
		
		// Lineas Agudo facil
		dibujo = new Dibujo();
		dibujo.tag = "AgudoFacil";
		double angulo = 30;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = 60;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);

		// Lineas Agudo dificil
		dibujo = new Dibujo();
		dibujo.tag = "AgudoDificil";
		angulo = 15;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = -15;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Lineas recto facil
		dibujo = new Dibujo();
		dibujo.tag = "RectoFacil";
		angulo = 0;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = 90;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Lineas recto dificil
		dibujo = new Dibujo();
		dibujo.tag = "RectoDificil";
		angulo = 45;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = 135;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);		
		
		// Lineas obtuso facil
		dibujo = new Dibujo();
		dibujo.tag = "ObtusoFacil";
		angulo = -10;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = 100;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		// Lineas obtuso dificil
		dibujo = new Dibujo();
		dibujo.tag = "ObtusoDificil";
		angulo = 30;
		linea = new Linea();
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		linea = new Linea();
		angulo = 140;
		linea.radial.Xcenter = tamano/2 + largo / 4 * MathUtils.cosDeg((float) angulo);
		linea.radial.Ycenter = tamano/2 + largo / 4 * MathUtils.sinDeg((float) angulo);
		linea.radial.angulo = angulo;
		linea.radial.largo = largo/2;
		linea.lineaFromRadial();
		dibujo.lineas.add(linea);
		dibujos.add(dibujo);
		
		return dibujos;
	}

	private static class Dibujo {
		String tag;
		Array<Linea> lineas = new Array<Linea>();
	}
}
