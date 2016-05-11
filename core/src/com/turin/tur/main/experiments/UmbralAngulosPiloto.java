package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;


public class UmbralAngulosPiloto extends UmbralAngulos {

	static final String TAG = UmbralAngulosPiloto.class.getName();
	// Cosas generales
	private String expName = "UmbralAngulosPiloto";
	
	
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxDinamica = 20;
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(60d);
		setup.angulosReferencia.add(120d);
		setup.angulosReferencia.add(150d);
		
		// Generamos los lados moviles
		float desvMin = 1f;
		float desvMax = 80f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 50;
		boolean logscale = true;
		Array<Double> desviaciones = new Array<Double>();
		// Creamos la serie de desviaciones en abstracto
		if (logscale) {
			double paso = (desvMaxLog - desvMinLog) / (numeroDeDesviaciones - 1);
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				desviaciones.add(desvMinLog + paso * i);
			}
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				desviaciones.set(i, Math.exp(desviaciones.get(i)));
			}
		} else {
			double paso = (desvMax - desvMin) / numeroDeDesviaciones;
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				desviaciones.add(desvMin + paso * i);
			}
		}
		// Armamos la serie completa
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			
			setup.desviacionesAngulares.add(90 + desviacion);
		}
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			setup.desviacionesAngulares.add(90 - desviacion);
		}
		
		setup.levelPriority=2;
		setup.tagButton = "Nivel";
		setup.feedback = false;
		this.setup = setup;
		
	}

	@Override
	public String getName() {
		return this.expName;
	}

	@Override
	protected String getNameTag() {
		return "expAngulosPiloto";
	}
	
}