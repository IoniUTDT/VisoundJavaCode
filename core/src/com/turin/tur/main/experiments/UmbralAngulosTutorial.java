package com.turin.tur.main.experiments;

public class UmbralAngulosTutorial extends UmbralAngulos{

	static final String TAG = UmbralAngulosTutorial.class.getName();
	// Cosas generales
	protected String expName = "UmbralAngulosTutorial";
	
	
	@Override
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		
		// Definimos los angulos de referencia
		setup.angulosReferencia.add(180d);
		
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
		setup.desvMax = 80;
		setup.desvMin = 1;
		setup.confianceProbability = 0.3f;
		setup.testProbability = 0.2f;
		this.setup = setup;
		this.generarDesviaciones(setup);

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
