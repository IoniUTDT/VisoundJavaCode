package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.UmbralParalelismo;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	
	@Override
	public void create () {

		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			Experiment exp = new UmbralParalelismo();
			exp.makeResources();
			Builder builder = new Builder();
			builder.build();
		}

		// Inicializa la session y el juego
		this.session = new Session();
		setScreen(new MenuScreen(this, this.session));
	}
	
	
}