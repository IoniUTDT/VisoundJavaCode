package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

public class UmbralAngulosTutorial extends UmbralAngulos{

	static final String TAG = UmbralAngulosTutorial.class.getName();
	// Cosas generales
	protected String expName = "UmbralAngulosTutorial";
	
	
	@Override
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxDinamica = 5;
		// Definimos los angulos de referencia
		setup.angulosReferencia.add(180d);
		
		// Generamos los lados moviles
		float desvMin = 1f;
		float desvMax = 80f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 5;
		boolean logscale = true;
		// Creamos la serie de desviaciones respecto al paralelismo
		Array<Double> desviaciones = new Array<Double>();
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
		
		setup.levelPriority=1;
		setup.tagButton = "Tutorial";
		setup.feedback = true;
		this.setup = setup;
		
	}

	@Override
	public String getName() {
		return this.expName;
	}

	@Override
	protected String getNameTag() {
		return "TutorialAngulo";
	}
	
	
}
