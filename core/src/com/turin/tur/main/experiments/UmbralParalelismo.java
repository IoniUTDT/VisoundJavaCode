package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.builder.Imagenes;

public class UmbralParalelismo implements Experiment {

	static final String TAG = UmbralParalelismo.class.getName();
	
	private Setup setup;
	
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
	
	private class Setup {
		Array<Double> angulosReferencia = new Array<Double>();
		Array<Double> desviacionesAngulares = new Array<Double>();
	}
	
	private void makeResource (double referencia, double desviacion) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width>Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		// Definimos algunos parametros
		float largo = tamano * 0.8f;
		float separacion = tamano * 0.2f;
		
		float Xcenter = Resources.Display.width/2;
		float Ycenter = Resources.Display.height/2;
		
		ImageInfo info = new ImageInfo();
		
		// Creamos la imagen correspondiente
		Imagenes imagen = new Imagenes();
		imagen.infoConceptual = info;
		
	}
	
	private class ImageInfo {
		
	}
	
	//  Todo lo que sigue a continuacion son cosas publicas de la interfaz, las anteriores son privadas del funcionamiento interno
	@Override
	public void makeResources() {
		// Inicializamos el setup segun parametros
		this.makeSetup();
		// Creamos un recurso para cada imagen necesaria
		for (double referencia : this.setup.angulosReferencia) {
			for (double desviacion : this.setup.desviacionesAngulares) {
				makeResource (referencia, desviacion);
			}
		}
		// TODO Auto-generated method stub	
	}

	@Override
	public void makeLevels() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportLevels() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
