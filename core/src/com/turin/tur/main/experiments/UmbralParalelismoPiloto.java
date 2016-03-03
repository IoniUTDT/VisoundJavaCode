package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

public class UmbralParalelismoPiloto extends UmbralParalelismo{

	private String expName = "UmbralParalelismoPiloto";
	
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxNivel = 40;
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(0d);
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(90d);
		// Generamos las desviaciones
		float desvMin = 0.1f;
		float desvMax = 25f;
		double desvMinLog = Math.log(desvMin);
		double desvMaxLog = Math.log(desvMax);
		int numeroDeDesviaciones = 40;
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
		setup.levelPriority = 2;
		setup.tagButton = "Nivel";
		this.setup = setup;
	}

	@Override
	public String getName() {
		return this.expName;
	}

	@Override
	protected String getNameTag() {
		return "expUmbralParalelismoPiloto";
	}
}
