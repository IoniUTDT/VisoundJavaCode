package com.turin.tur.main.util.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ParametrosSetupParalelismo;
import com.turin.tur.main.experiments.UmbralAngulos;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.FileHelper;

public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private final static String TAG = Builder.class.getName();

	static String pathLevelsBackUp = Resources.Paths.fullLevelsPath.substring(0, Resources.Paths.fullLevelsPath.length()-1)+"olds/"+TimeUtils.millis()+"/";

	static int contadorTrials = 0;

	static int contadorLevels = 0;

	public static final int ResourceVersion = 132;
	public static final int levelVersion = 26;
	public static int levelVersionFinal;
	public static final String AppVersion = "UmbralCompletoAngulos"; 
	public static final boolean categorizar = false;
	
	static final Boolean makeLevels = true;
	static final Boolean makeResources = false;
	

	public Builder (){
		
	}
	
	public void build() {

		if (makeResources) {
			UmbralAngulos experimento = new UmbralAngulos();
			experimento.generalBuilding();
			System.exit(0);
		}	
		if (makeLevels) {
			UmbralAngulos experimento = new UmbralAngulos();
			experimento.makeLevels();
			System.exit(0);
			// LevelsConstructor levelconstructor = new LevelsConstructor();
			
			//LevelMaker.makeLevels();
			//ResourcesExport.createStructure();
			//System.exit(0);
		}

	}

	/**
	 * Funcion que verifica que no haya recursos creados con la misma version que la que se quiere crear
	 */
	public static void verifyResourcesVersion() {
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(Resources.Paths.fullCurrentVersionPath);
		if (file.exists()) {
			System.out.println("Modifique la version de los recursos porque ya existe una carpeta con la version actual");
			System.exit(0);
		}
	}

	/**
	 * This method move the file with the metadata of a resource to specified folder inside the android's assets
	 * @param file
	 * @param folder
	 */
	static void moveMeta(File file, String folder){
		file = new File(Resources.Paths.fullCurrentVersionPath + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".meta");
		Path FROM = Paths.get(file.getAbsolutePath());
		File out = new File(Resources.Paths.finalPath + "/" + folder + "/" + file.getName());
		Path TO = Paths.get(out.getAbsolutePath());
		//overwrite existing file, if exists
		CopyOption[] options = new CopyOption[] {
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES
		};
		try {
			Files.copy(FROM, TO, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Funcion que transforma los archivos svg en png
	 * 
	 * @param file
	 * @param i 
	 */
	static void convertirSVGtoPNG(File file) {
		try {
			//Step -1: We read the input SVG document into Transcoder Input
			//We use Java NIO for this purpose
			String svg_URI_input = Paths.get(file.getAbsolutePath()).toUri().toURL().toString();
			TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
			//Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
			OutputStream png_ostream;
			file = new File(Resources.Paths.ProcessingPath + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
			png_ostream = new FileOutputStream(file);
	
			TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			// Step-3: Create PNGTranscoder and define hints if required
			PNGTranscoder my_converter = new PNGTranscoder();
			// Step-4: Convert and Write output
			my_converter.transcode(input_svg_image, output_png_image);
			// Step 5- close / flush Output Stream
			png_ostream.flush();
			png_ostream.close();
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TranscoderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JsonTrial crearTrial(String title, String caption, DISTRIBUCIONESenPANTALLA distribucion, int[] elementos, TIPOdeTRIAL modo,
			int rtaCorrecta, Boolean randomAnswer, Boolean randomSort, Boolean feedback) {
		// Crea un JsonTrial y aumenta en 1 el contador de trials
		Builder.contadorTrials += 1;
		JsonTrial jsonTrial = new JsonTrial();
		jsonTrial.Id = Builder.contadorTrials;
		jsonTrial.caption = caption;
		jsonTrial.distribucion = distribucion;
		jsonTrial.elementosId = elementos;
		jsonTrial.modo = modo;
		jsonTrial.rtaCorrectaId = rtaCorrecta;
		jsonTrial.rtaRandom = randomAnswer;
		jsonTrial.randomSort = randomSort;
		jsonTrial.title = title;
		jsonTrial.resourceVersion = ResourceVersion;
		jsonTrial.feedback = feedback;
		jsonTrial.parametrosParalelismo = new ParametrosSetupParalelismo();
		return jsonTrial;
	}

	public static void writeLevelJson(JsonLevel jsonLevel, String path) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path + "level" + jsonLevel.Id + ".meta", json.toJson(jsonLevel));
	}

	public static void CreateTrial(JsonTrial jsonTrial, String path) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
	}

	public static void buildJsons (JsonLevel level) {
		// If resources already exported, the the folder was cleaned.
		String path = Resources.Paths.finalPath + "/level" + level.Id + "/";
		for (JsonTrial jsonTrial : level.jsonTrials) {
			level.trials.add(jsonTrial.Id);
			Builder.CreateTrial(jsonTrial, path);
			Builder.CreateTrial(jsonTrial, Builder.pathLevelsBackUp);
		}
		level.jsonTrials.clear();
		Builder.writeLevelJson(level,Resources.Paths.finalPath);
		Builder.writeLevelJson(level,Builder.pathLevelsBackUp);
	}

	public static void extract(JsonLevel level) {
		Gdx.app.debug(TAG,"Procesando nivel "+level.Id);
		Array<Integer> listado = new Array<Integer>(); // Listado de ids de recursos utilizados
		for (JsonTrial jsonTrial : level.jsonTrials) {
			for (int idResource : jsonTrial.elementosId) {
				if (!listado.contains(idResource,false)) {
					listado.add(idResource);
				}
			}
			if (!listado.contains(jsonTrial.rtaCorrectaId,false)) {
				listado.add(jsonTrial.rtaCorrectaId);
			}
		}
		// Con el listado de ids que corresponden al nivel los exportamos
		export(listado,"level"+level.Id);
	}

	public static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.appVersion = AppVersion;
		jsonLevel.Id = contadorLevels;
		jsonLevel.resourceVersion = ResourceVersion;
		jsonLevel.levelVersion = levelVersionFinal;
		return jsonLevel;
	}

	/**
	 * This method transforms SVGs of a list of ids that must be in the same folder into PNG files, join it at a ATLAS file and copy the ATLAS, and the .meta files
	 * to a folder in android's assets 
	 */
	static void export(Array<Integer> ids, String folderName){
		Gdx.app.debug(TAG, "Exportando los recursos correspondientes a " + folderName);
		// We clean the destiny folder
		File folder = new File(Resources.Paths.finalPath+folderName+"/");
		if (folder.exists()) {
			// clean the folder from old stuff
			try {
				FileUtils.cleanDirectory(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			// create folder
			folder.mkdir();
		}
		// clean the temp folder
		File tempDirectory = new File(Resources.Paths.ProcessingPath);
		try {
			FileUtils.cleanDirectory(tempDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// Then we convert all files to PNG into a temp folder and copy the metadata to assets folder
		for (int id : ids){
			File resource = new File (Resources.Paths.fullCurrentVersionPath+id+".svg");
			Builder.convertirSVGtoPNG(resource);
			if (id > Resources.Reservados) { // Means that is not a category with no audio
				SVGtoMp3 converter = new SVGtoMp3(resource, Resources.Paths.finalPath+folderName+"/");
			}
			Builder.moveMeta(resource,folderName);
		}
		
		// Create atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, Resources.Paths.ProcessingPath, Resources.Paths.finalPath, folderName + "img");
		Gdx.app.debug(TAG, "Recursos correctamente exportados: " + folderName+".");
	}

	public static void cleanAssets () {
		File tempDirectory = new File(Resources.Paths.finalPath);
		try {
			FileUtils.cleanDirectory(tempDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static void verifyResources(){
		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(Resources.Paths.fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recursos version:" + ResourceVersion);
			System.exit(0);
		}
	}

	public static void verifyLevelVersion(){
		// Verifica que no haya niveles ya numerados con la version marcada
		File file = new File(Resources.Paths.finalPath + "level" + 1 + ".meta");
		if (file.exists()) {
			String savedData = FileHelper.readLocalFile(Resources.Paths.finalPath + "level" + 1 + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				JsonLevel jsonLevel = json.fromJson(JsonLevel.class, savedData);
				if (jsonLevel.levelVersion >= levelVersion) {
					Gdx.app.error(TAG, "OJO! Deberia actualizar la version del level. Se modificara sola al numero: " + (jsonLevel.levelVersion+1));
					levelVersionFinal=jsonLevel.levelVersion+1;
				} else {
					levelVersionFinal = levelVersion;
				}
			}
		} 
	}
}
