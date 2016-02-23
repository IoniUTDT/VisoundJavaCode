package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Experiments.TIPOdeEXPERIMENTO;
import com.turin.tur.main.experiments.UmbralParalelismo;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	public Experiment exp;
	public TIPOdeEXPERIMENTO tipo = TIPOdeEXPERIMENTO.UmbralParalelismo;
	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	
	@Override
	public void create () {

		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Create the experiment class
		if (tipo == TIPOdeEXPERIMENTO.UmbralParalelismo) {exp = new UmbralParalelismo();}
		//if (tipo == TIPOdeEXPERIMENTO.UmbralAngulos) {exp = new UmbralAngulos();}
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			
			Builder builder = new Builder();
			builder.build(exp);
		}

		// Inicializa la session y el juego
		this.session = new Session();
		this.exp.initGame(this.session);
		setScreen(new MenuScreen(this, this.session, exp));
	}
	
	
}