package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

public class TutorialUmbralParalelismo extends UmbralParalelismo{

	private String expName = "UmbralParalelismoTutorial";
	
	@Override
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxNivel = 10;
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(10d);
		// Generamos las desviaciones
		float desvMin = 5f;
		float desvMax = 25f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 3;
		boolean logscale = true;
		// Creamos la serie de desviaciones respecto al paralelismo
		if (logscale) {
			double paso = (desvMaxLog - desvMinLog) / (numeroDeDesviaciones - 1);
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMinLog + paso * i);
			}
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.set(i, Math.exp(setup.desviacionesAngulares.get(i)));
			}
		} else {
			double paso = (desvMax - desvMin) / numeroDeDesviaciones;
			for (int i = 0; i < numeroDeDesviaciones; i++) {
				setup.desviacionesAngulares.add(desvMin + paso * i);
			}
		}
		// Agregamos una copia negativa
		for (int i = 0; i < numeroDeDesviaciones; i++) {
			setup.desviacionesAngulares.add(-setup.desviacionesAngulares.get(i));
		}
		
		setup.levelPriority = 1;
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
		return "TutorialParalelismo";
	}

}
