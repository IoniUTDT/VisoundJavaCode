package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.levelsDesign.Level.LISTAdeRECURSOS;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	// public Array<GenericExp> exps = new Array<GenericExp>();
	public Array<Level> levelList = new Array<Level>();
	// public GenericExp expActivo;
	public Level levelActivo;
	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	public static float volumen = 0.5f;
	public boolean sendingData;
	Array<LISTAdeNIVELES> identificadoresLvl = new Array<LISTAdeNIVELES>();
	
	
	@Override
	public void create () {

		// Hacemos aca el chequeo porque sino se activan envios de internet y es un problema
		Internet.checkInternet();
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		identificadoresLvl.addAll(LISTAdeNIVELES.Tutorial, LISTAdeNIVELES.AngulosTutorial, LISTAdeNIVELES.ParalelismoTutorial);
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			Array<LISTAdeRECURSOS> identificadoresRes = new Array<LISTAdeRECURSOS>();
			identificadoresRes.addAll(LISTAdeRECURSOS.ImagenesEjemplos,LISTAdeRECURSOS.UmbralAngulosTutorial, LISTAdeRECURSOS.UmbralParalelismoTutorial, LISTAdeRECURSOS.UmbralAngulosTransferencia, LISTAdeRECURSOS.UmbralParalelismoTransferencia);
			Builder.buildResources(identificadoresRes);
			Builder.buildLevels(identificadoresLvl);
		}
		
		this.session = new Session();
		setScreen(new MenuScreen(this));
	}	
	
	/*
	public enum TipoDeAplicacion {
		Tutorial, Test, Entrenamiento
	}
	*/
	

}