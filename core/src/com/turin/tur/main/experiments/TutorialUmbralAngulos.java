package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

public class TutorialUmbralAngulos extends UmbralAngulos{

	static final String TAG = TutorialUmbralAngulos.class.getName();
	// Cosas generales
	protected String expName = "UmbralAngulosTutorial";
	
	
	@Override
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxNivel = 10;
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(180d);
		
		// Generamos los lados moviles
		setup.desviacionesAngulares.add(10d);
		setup.desviacionesAngulares.add(45d);
		setup.desviacionesAngulares.add(90d);
		setup.desviacionesAngulares.add(135d);
		setup.desviacionesAngulares.add(170d);
		//setup.desviacionesAngulares.add(180d);
		setup.desviacionesAngulares.add(190d);
		setup.desviacionesAngulares.add(225d);
		setup.desviacionesAngulares.add(270d);
		setup.desviacionesAngulares.add(315d);
		setup.desviacionesAngulares.add(350d);
		
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
