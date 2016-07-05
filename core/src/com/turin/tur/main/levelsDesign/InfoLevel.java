package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Session.FASEdeEXPERIMENTO;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.util.FileHelper;

public class InfoLevel {
	
	public static String pathNameExt = ".LvlInfo";
	public LISTAdeNIVELES indentificadorLevel;
	public Array<FASEdeEXPERIMENTO> fases = new Array<FASEdeEXPERIMENTO>();
	public int prioridad;
	
	public static void saveInfoLevel (Level.LISTAdeNIVELES identificador, InfoLevel infoLevel) {
		String path = pathLevelInfo(identificador);
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(infoLevel));
	}
	
	private static String pathLevelInfo (Level.LISTAdeNIVELES identificador) {
		return Level.folderResources(identificador) + identificador.toString() + pathNameExt;
	}
	
	public static InfoLevel loadInfoLevel (Level.LISTAdeNIVELES identificador) {
		// Se fija si existe el archivo en la copia externa (donde se puede modificar) y sino hace una copia desde los archivos internos
		/*
		if (!Gdx.files.local(ResourcesCategorias.Paths.LocalSettingsCopy + identificador.toString() + pathNameExt).exists()) { // hacemos una copia de la info guardada en internal
			FileHandle from = Gdx.files.internal(pathLevelInfo(identificador));
			FileHandle to = Gdx.files.local(ResourcesCategorias.Paths.LocalSettingsCopy + identificador.toString() + pathNameExt);
			from.copyTo(to);
		}
		*/
		// String savedData = FileHelper.readLocalFile(ResourcesCategorias.Paths.LocalSettingsCopy + identificador.toString() + pathNameExt);
		String savedData = FileHelper.readLocalFile(pathLevelInfo(identificador));
		Json json = new Json();
		return json.fromJson(InfoLevel.class, savedData);
	}
}
