package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.experiments.Umbral;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.LevelAsset;


public class Level {

	// Constantes
	private static final String TAG = Level.class.getName();
	public int Id;

	// variable del nivel
	public String levelTitle;
	public JsonLevel jsonLevel;
	public LevelAsset levelAssets;

	public Level(int level) {
		Gdx.app.debug(TAG, "Cargando informacion del nivel " + level);
		this.initlevel(level);
	}

	private void initlevel(int levelNumber) {
		this.jsonLevel = loadLevel(levelNumber);
		this.Id = jsonLevel.Id;
		this.levelTitle = jsonLevel.levelTitle;
		this.levelAssets = new LevelAsset(this.Id);
	}

	public void levelDispose() {
		this.levelAssets.dispose();
	}

	/*
	 * Aca empieza info accesoria para el load
	 */

	private JsonLevel loadLevel(int level) {
		String savedData = FileHelper.readInternalFile(Resources.Paths.InternalResources + "level" + level + ".meta");
		if (!savedData.isEmpty()) {
			Json json = new Json();
			json.setUsePrototypes(false);
			return json.fromJson(JsonLevel.class, savedData);
		}
		Gdx.app.error(TAG, "No se a podido encontrar la info del nivel " + level);
		return null;
	}


	public static class JsonLevel {
		public String levelTitle;
		public int levelVersion; 
		public int Id; // Id q identifica al level
		public Array<JsonTrial> jsonTrials = new Array<JsonTrial>(); // Este se usa solamente en el proceso de creacion de niveles (pero por como esta dise�ado el codigo que graba y carga el json completo se guarda   
		public int resourceVersion;
		public Object dinamicaExperimento;
		public Umbral.Setup setup;
	}
	
}