package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Session;

/**
 * Este paquete esta pensado para almacenar todo lo que tiene que ver con los diseños experimentales.
 * @author ionatan
 *
 */


public class Experiments {

	static final String TAG = Experiments.class.getName();

	public static class LevelStatus implements Comparable<LevelStatus>{
		public String publicName;
		public String internalName;
		public String expName;
		public int id;
		public boolean enabled;
		public boolean alreadyPlayed;
		public int priority;
		@Override
		public int compareTo(LevelStatus o) {
			return Integer.valueOf(priority).compareTo(o.priority);
		}
	}
	
	public static class ExpSettings {
		public TIPOdeEXPERIMENTO tipoDeExperimento;
		public Array<LevelStatus> levels = new Array<LevelStatus>();
	}
	
	public enum TIPOdeEXPERIMENTO {
		UmbralAngulos,
		UmbralParalelismo,
		TutorialBasico,
		TutorialAvanzadoUmbral
	}
	
	
	public static class ExperimentLog {
		public Session session;
		public long levelInstance;
		public String expName;
	}
	
	
}
