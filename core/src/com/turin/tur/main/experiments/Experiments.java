package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

/**
 * Este paquete esta pensado para almacenar todo lo que tiene que ver con los diseños experimentales.
 * @author ionatan
 *
 */


public class Experiments {

	static final String TAG = Experiments.class.getName();

	public static class LevelStatus {
		public String publicName;
		public String internalName;
		public String expName;
		public int id;
		public boolean enabled;
		public boolean alreadyPlayed;
	}
	
	public static class ExpSettings {
		public TIPOdeEXPERIMENTO tipoDeExperimento;
		public Array<LevelStatus> levels = new Array<LevelStatus>();
	}
	
	public enum TIPOdeEXPERIMENTO {
		UmbralAngulos,
		UmbralParalelismo,
		UmbralAngulosV2
	}
	
}
