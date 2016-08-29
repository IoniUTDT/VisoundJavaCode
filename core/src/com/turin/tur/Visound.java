package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
	public Level levelActivo;
	
	private static final String TAG = Visound.class.getName();
	public static final String pathLogs = "logs";
	public static final boolean mododesarrollo = false;
	public static final boolean modoDebug = false;
	public Session session;
	public static float volumen = 0.5f;
	// public InternetNuevo internetViejo = new InternetNuevo();
	public Internet internet = new Internet();
	public InputMultiplexer im = new InputMultiplexer();
	
	@Override
	public void create () {

		// Set Libgdx log level
		if (mododesarrollo) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
		
		Gdx.input.setInputProcessor(im);
		
		// dentificadoresLvl.addAll(LISTAdeNIVELES.Ejemplos, LISTAdeNIVELES.AngulosTutorial, LISTAdeNIVELES.ParalelismoTutorial);
		
		if ((mododesarrollo) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			Array<LISTAdeRECURSOS> identificadoresRes = new Array<LISTAdeRECURSOS>();
			identificadoresRes.addAll(LISTAdeRECURSOS.ImagenesEjemplos,LISTAdeRECURSOS.RecursosAngulosTutorial, LISTAdeRECURSOS.RecursosParalelismoTutorial, LISTAdeRECURSOS.RecursosAngulosTransferencia, LISTAdeRECURSOS.RecursosParalelismoTransferencia);
			Builder.buildResources(identificadoresRes);
			Builder.buildLevels(LISTAdeNIVELES.values());
		}
		 
		internet.inicio();
		//internetViejo.checkConectividad();
		//internetViejo.loadSavedData();
		this.session = Session.newSession();
		setScreen(new MenuScreen(this));
	}	
	
}