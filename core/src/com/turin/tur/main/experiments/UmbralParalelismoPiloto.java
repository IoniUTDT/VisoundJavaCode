package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.experiments.Umbral.Setup;

public class UmbralParalelismoPiloto extends UmbralParalelismo{

	private String expName = "UmbralParalelismoPiloto";
	
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeEstimulosPorSerie = 50;
		setup.trialsPorNivel = 100; //TODO cuando haga las pruebas preliminares tengo que fijarme en cuanto tipicamente converge y reducirlo en funcion de eso
		
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(60d);
		setup.angulosReferencia.add(120d);
		setup.angulosReferencia.add(150d);
		
		// Definimos las fluctuaciones locales
		setup.fluctuacionesLocalesReferenciaSeries.add(0f);
		setup.fluctuacionesLocalesReferenciaSeries.add(2.5f);
		setup.fluctuacionesLocalesReferenciaSeries.add(5f);
		setup.fluctuacionesLocalesReferenciaSeries.add(-2.5f);
		setup.fluctuacionesLocalesReferenciaSeries.add(-5f);
				
		// Definimos las fluctuaciones para el cero
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(0d);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(2.5);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(-2.5);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(5d);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(-5d);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(7.5);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(-7.5);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(10d);
		setup.fluctuacionesLocalesReferenciaEstimuloCero.add(-10d);
		

		setup.levelPriority=2;
		setup.tagButton = "Nivel";
		setup.feedback = false;
		setup.desvMax = 50;
		setup.desvMin = 0.1;
		this.setup = setup;
		this.generarDesviaciones(setup);
		
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
