package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;


public class UmbralAngulosPiloto extends UmbralAngulos {

	static final String TAG = UmbralAngulosPiloto.class.getName();
	// Cosas generales
	private String expName = "UmbralAngulosPiloto";
	
	
	void makeSetup() {
		// Creamos el setup
		Setup setup = new Setup();
		setup.numeroDeTrailsMaximosxNivel = 40;
		// Definimos los angulos de referencia
		setup.angulosReferencia = new Array<Double>();
		setup.angulosReferencia.add(0d);
		setup.angulosReferencia.add(30d);
		setup.angulosReferencia.add(90d);
		
		// Generamos los lados moviles
		setup.desviacionesAngulares.add(0d);
		setup.desviacionesAngulares.add(1d);
		setup.desviacionesAngulares.add(2d);
		setup.desviacionesAngulares.add(4d);
		setup.desviacionesAngulares.add(6d);
		setup.desviacionesAngulares.add(8d);
		setup.desviacionesAngulares.add(10d);
		setup.desviacionesAngulares.add(20d);
		setup.desviacionesAngulares.add(30d);
		setup.desviacionesAngulares.add(40d);
		setup.desviacionesAngulares.add(50d);
		setup.desviacionesAngulares.add(60d);
		setup.desviacionesAngulares.add(70d);
		setup.desviacionesAngulares.add(80d);
		setup.desviacionesAngulares.add(82d);
		setup.desviacionesAngulares.add(84d);
		setup.desviacionesAngulares.add(86d);
		setup.desviacionesAngulares.add(88d);
		setup.desviacionesAngulares.add(89d);
		setup.desviacionesAngulares.add(90d);
		setup.desviacionesAngulares.add(91d);
		setup.desviacionesAngulares.add(92d);
		setup.desviacionesAngulares.add(94d);
		setup.desviacionesAngulares.add(96d);
		setup.desviacionesAngulares.add(98d);
		setup.desviacionesAngulares.add(100d);
		setup.desviacionesAngulares.add(110d);
		setup.desviacionesAngulares.add(120d);
		setup.desviacionesAngulares.add(130d);
		setup.desviacionesAngulares.add(140d);
		setup.desviacionesAngulares.add(150d);
		setup.desviacionesAngulares.add(160d);
		setup.desviacionesAngulares.add(170d);
		setup.desviacionesAngulares.add(172d);
		setup.desviacionesAngulares.add(174d);
		setup.desviacionesAngulares.add(176d);
		setup.desviacionesAngulares.add(178d);
		setup.desviacionesAngulares.add(179d);
		setup.desviacionesAngulares.add(180d);
		setup.desviacionesAngulares.add(181d);
		setup.desviacionesAngulares.add(182d);
		setup.desviacionesAngulares.add(184d);
		setup.desviacionesAngulares.add(186d);
		setup.desviacionesAngulares.add(188d);
		setup.desviacionesAngulares.add(190d);
		setup.desviacionesAngulares.add(200d);
		setup.desviacionesAngulares.add(210d);
		setup.desviacionesAngulares.add(220d);
		setup.desviacionesAngulares.add(230d);
		setup.desviacionesAngulares.add(240d);
		setup.desviacionesAngulares.add(250d);
		setup.desviacionesAngulares.add(260d);
		setup.desviacionesAngulares.add(262d);
		setup.desviacionesAngulares.add(264d);
		setup.desviacionesAngulares.add(266d);
		setup.desviacionesAngulares.add(268d);
		setup.desviacionesAngulares.add(269d);
		setup.desviacionesAngulares.add(270d);
		setup.desviacionesAngulares.add(271d);
		setup.desviacionesAngulares.add(272d);
		setup.desviacionesAngulares.add(274d);
		setup.desviacionesAngulares.add(276d);
		setup.desviacionesAngulares.add(278d);
		setup.desviacionesAngulares.add(280d);
		setup.desviacionesAngulares.add(290d);
		setup.desviacionesAngulares.add(300d);
		setup.desviacionesAngulares.add(310d);
		setup.desviacionesAngulares.add(320d);
		setup.desviacionesAngulares.add(330d);
		setup.desviacionesAngulares.add(340d);
		setup.desviacionesAngulares.add(350d);
		setup.desviacionesAngulares.add(352d);
		setup.desviacionesAngulares.add(354d);
		setup.desviacionesAngulares.add(356d);
		setup.desviacionesAngulares.add(358d);
		setup.desviacionesAngulares.add(359d);
		
		setup.levelPriority=2;
		setup.tagButton = "Nivel";
		
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