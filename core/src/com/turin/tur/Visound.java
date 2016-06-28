package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.experiments.Tutorial;
import com.turin.tur.main.experiments.UmbralAngulos;
import com.turin.tur.main.experiments.UmbralParalelismo;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	public Array<GenericExp> exps = new Array<GenericExp>();
	public GenericExp expActivo;
	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	public static float volumen = 0.5f;
	public boolean sendingData;
	
	@Override
	public void create () {

		// Hacemos aca el chequeo porque sino se activan envios de internet y es un problema
		Internet.checkInternet();
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Create the experiment class
		exps.add(new Tutorial());
		exps.add(new UmbralAngulos());
		// exps.add(new UmbralParalelismo());
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			
			Builder builder = new Builder();
			builder.build(exps);
		}

		// Inicializa la session
		this.session = new Session();
		for (Experiment exp : this.exps) {
			exp.initGame(this.session);
		}
		setScreen(new MenuScreen(this));
	}	
	
	public enum TipoDeAplicacion {
		Tutorial, Test, Entrenamiento
	}
}