package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Tutorial;
import com.turin.tur.main.experiments.TutorialUmbralAngulos;
import com.turin.tur.main.experiments.TutorialUmbralParalelismo;
import com.turin.tur.main.experiments.UmbralAngulosPiloto;
import com.turin.tur.main.experiments.UmbralParalelismoPiloto;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	public Array<Experiment> exps = new Array<Experiment>();
	public Experiment expActivo;
	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	
	@Override
	public void create () {

		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Create the experiment class
		exps.add(new Tutorial());
		exps.add(new TutorialUmbralAngulos());
		exps.add(new TutorialUmbralParalelismo());
		exps.add(new UmbralParalelismoPiloto());
		exps.add(new UmbralAngulosPiloto());
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			
			Builder builder = new Builder();
			builder.build(exps);
		}

		// Inicializa la session y el juego
		this.session = new Session();
		for (Experiment exp : this.exps) {
			exp.initGame(this.session);
		}
		setScreen(new MenuScreen(this));
	}	
}