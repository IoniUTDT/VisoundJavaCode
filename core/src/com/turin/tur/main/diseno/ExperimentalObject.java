package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class ExperimentalObject {

	public final Sprite imagen;
	// public final int Id; 
	public String name;
	public String comments = "Aca va opcionalmente una descripcion del objeto";
	public Array<Constants.ResourcesCategorias.CategoriasImagenes> categorias = new Array<Constants.ResourcesCategorias.CategoriasImagenes>();
	public ResourceId resourceId;
	public boolean noSound;
	
	// Constantes
	private static final String TAG = ExperimentalObject.class.getName();
	
	public ExperimentalObject (int Id, LevelAsset asset, String levelPath){ // Esto carga la info desde archivo
		this.imagen = asset.imagen(Id);
		this.loadMetaData(Id, levelPath);
	}

	private void loadMetaData(int Id, String levelPath) {
		JsonResourcesMetaData jsonMetaData = JsonResourcesMetaData.Load(Id, levelPath);
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
		public Array<Constants.ResourcesCategorias.CategoriasImagenes> categories = new Array<Constants.ResourcesCategorias.CategoriasImagenes>();
		public Array<Linea> infoLineas;
		public Object infoConceptual;
		
		
		public static void CreateJsonMetaData (JsonResourcesMetaData jsonMetaData, String path) {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(path + jsonMetaData.resourceId.id + ".meta", json.toJson(jsonMetaData));			
		} 
		
		public void save() {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile("experimentalsource/" + Constants.version() + "/" + resourceId.id + ".meta", json.toJson(this));
		}
		
		public static JsonResourcesMetaData Load(int Id, String levelPath) {
			String savedData = FileHelper.readInternalFile(levelPath + "/" + Id + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				return json.fromJson(JsonResourcesMetaData.class, savedData);
			} else { Gdx.app.error(TAG,"No se a podido encontrar la info del recurso experimental" + Id); }
			return null;
		}
	}
}
