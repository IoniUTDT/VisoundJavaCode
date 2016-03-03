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
		setup.desviacionesAngulares.add(0d);
		setup.desviacionesAngulares.add(20d);
		setup.desviacionesAngulares.add(40d);
		setup.desviacionesAngulares.add(60d);
		setup.desviacionesAngulares.add(80d);
		setup.desviacionesAngulares.add(100d);
		setup.desviacionesAngulares.add(120d);
		setup.desviacionesAngulares.add(140d);
		setup.desviacionesAngulares.add(160d);
		setup.desviacionesAngulares.add(180d);
		setup.desviacionesAngulares.add(200d);
		setup.desviacionesAngulares.add(220d);
		setup.desviacionesAngulares.add(240d);
		setup.desviacionesAngulares.add(260d);
		setup.desviacionesAngulares.add(280d);
		setup.desviacionesAngulares.add(300d);
		setup.desviacionesAngulares.add(320d);
		setup.desviacionesAngulares.add(340d);
		setup.desviacionesAngulares.add(360d);
		
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
