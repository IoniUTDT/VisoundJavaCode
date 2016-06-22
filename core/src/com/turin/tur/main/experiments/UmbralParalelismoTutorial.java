package com.turin.tur.main.experiments;


public class UmbralParalelismoTutorial extends UmbralParalelismo{

	private String expName = "UmbralParalelismoTutorial";
	
	@Override
	void makeSetup() {
		
		// Creamos el setup
		Setup setup = new Setup();
		
		// Definimos los angulos de referencia
		setup.angulosReferencia.add(0d);
		
		// Definimos las fluctuaciones locales
		setup.fluctuacionesLocalesReferenciaSeries.add(0f);
		
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
		
		// Definimos la cantidad de estimulos por serie
		setup.numeroDeEstimulosPorSerie = 10;
		
		// Definimos el numero de trials por level
		setup.trialsPorNivel = 10;
		
		setup.levelPriority=1;
		setup.tagButton = "Tutorial";
		setup.feedback = true;
		setup.desvMin = 0.1;
		setup.desvMax = 50;
		setup.confianceProbability = 0.3f;
		setup.testProbability = 0.2f;
		setup.allTestsConfianza = true;
		
		this.setup = setup;
		this.generarDesviaciones(setup);
		
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
