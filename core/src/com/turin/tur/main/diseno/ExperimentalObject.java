package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class ExperimentalObject {

	public final Sprite imagen;
	// public final int Id; 
	public String name;
	public String comments = "Aca va opcionalmente una descripcion del objeto";
	public Array<Constants.Resources.Categorias> categorias = new Array<Constants.Resources.Categorias>();
	public ResourceId resourceId;
	public boolean noSound;
	
	// Constantes
	private static final String TAG = ExperimentalObject.class.getName();
	
	public ExperimentalObject (int Id, LevelAsset asset, int levelId){ // Esto carga la info desde archivo
		
		// Carga ma metadata
		this.loadMetaData(Id, levelId);
		// Crea los recursos graficos y sonoros
		this.imagen = asset.imagen(Id); 
	}

	private void loadMetaData(int Id, int levelId) {
		JsonResourcesMetaData jsonMetaData = JsonResourcesMetaData.Load(Id, levelId);
		this.comments = jsonMetaData.comments;
		this.name = jsonMetaData.name;
		this.categorias = jsonMetaData.categories;
		this.noSound =jsonMetaData.noSound ;
		this.resourceId = jsonMetaData.resourceId;
	}


	public static class JsonResourcesMetaData {
		public boolean noSound;
		public String name;
		public String comments;
		public ResourceId resourceId;
		public Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
		public Array<Linea> infoLineas;
		public Object infoConceptual;
		
		
		public static void CreateJsonMetaData (JsonResourcesMetaData jsonMetaData, String path) {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeFile(path + jsonMetaData.resourceId.id + ".meta", json.toJson(jsonMetaData));			
		} 
		
		public void save() {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeFile("experimentalsource/" + Constants.version() + "/" + resourceId.id + ".meta", json.toJson(this));
		}
		
		public static JsonResourcesMetaData Load(int Id, int levelId) {
			String savedData = FileHelper.readFile(Resources.Paths.resources + "level"+ levelId + "/" + Id + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				return json.fromJson(JsonResourcesMetaData.class, savedData);
			} else { Gdx.app.error(TAG,"No se a podido encontrar la info del recurso experimental" + Id); }
			return null;
		}
	}
}
