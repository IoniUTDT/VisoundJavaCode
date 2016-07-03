package com.turin.tur.main.levelsDesign;

import com.turin.tur.main.diseno.Listas.LISTAdeNIVELES;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.LevelAsset;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.Constants.ResourcesCategorias.Paths;

public abstract class Level {
	
	public static final String dinamicaPathName = "dinamica.meta";
	// Info basica que todos los niveles tienen que tener y que se carga cuando se inicia el programa
	public LevelInfo levelInfo;
	public LevelAsset levelAssets;
	public LISTAdeNIVELES identificadorNivel;
	
	public abstract Trial getNextTrial();
	public abstract void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion, float timeConfiance,
			int loopsCount);
	public abstract boolean islevelCompleted();
	public abstract void levelCompleted();
	public abstract void interrupt();
	public abstract boolean goConfiance();
	public abstract void loadDinamica();
	
	
	Level() {
	}
	
	Level (LISTAdeNIVELES identificador) {
		this.identificadorNivel = identificador;
		this.loadInfoLevel();
		this.loadDinamica();
	}
	
	protected void loadInfoLevel() {
		// Se fija si existe el archivo en la copia externa (donde se puede modificar) y sino hace una copia desde los archivos internos 
		if (!Gdx.files.local(ResourcesCategorias.Paths.LocalSettingsCopy + this.identificadorNivel.toString() + Paths.LevelInfo).exists()) { // hacemos una copia de la info guardada en internal
			FileHandle from = Gdx.files.internal(ResourcesCategorias.Paths.InternalResources + this.identificadorNivel.toString() + Paths.LevelInfo);
			FileHandle to = Gdx.files.local(ResourcesCategorias.Paths.LocalSettingsCopy + this.identificadorNivel.toString() + Paths.LevelInfo);
			from.copyTo(to);
		}
		String savedData = FileHelper.readLocalFile(ResourcesCategorias.Paths.LocalSettingsCopy + this.identificadorNivel.toString() + Paths.LevelInfo);
		Json json = new Json();
		this.levelInfo = json.fromJson(this.levelInfo.getClass(), savedData);
	}
	
	protected void saveInfoLevel() {
		
	}
	
	public static String folderResources(LISTAdeNIVELES identificador) {
		return ResourcesCategorias.Paths.finalInternalPath+identificador.toString()+"/";
	}
	
}
