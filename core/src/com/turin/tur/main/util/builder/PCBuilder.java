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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.FileHelper;

public class PCBuilder {

	static final String TAG = PCBuilder.class.getName();

	/**
	 * Funcion que verifica que no haya recursos creados con la misma version que la que se quiere crear
	 */
	public static void verifyResourcesVersion() {
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(Resources.Paths.ResourcesBuilder);
		Gdx.app.debug(TAG, file.getAbsolutePath());
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
		file = new File(Resources.Paths.ResourcesBuilder + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".meta");
		Path FROM = Paths.get(file.getAbsolutePath());
		File out = new File(Resources.Paths.finalInternalPath + "/" + folder + "/" + file.getName());
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

	static void makeLevels() {
		// Hacemos tareas de revision y limpieza
		PCBuilder.verifyLevelVersion();
		PCBuilder.verifyResources();
		PCBuilder.cleanAssets();
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
			file = new File(Resources.Paths.processingTempFolder + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
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
		jsonTrial.resourceVersion = Builder.ResourceVersion;
		jsonTrial.feedback = feedback;
		// jsonTrial.parametrosParalelismo = new ParametrosSetupParalelismo();
		return jsonTrial;
	}

	public static void writeLevelJson(JsonLevel jsonLevel, String path) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path + "level" + jsonLevel.Id + ".meta", json.toJson(jsonLevel));
	}

	public static void CreateTrial(JsonTrial jsonTrial, String path) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
	}

	public static void buildJsons (JsonLevel level) {
		// If resources already exported, the the folder was cleaned.
		String path = Resources.Paths.finalInternalPath + "/level" + level.Id + "/";
		for (JsonTrial jsonTrial : level.jsonTrials) {
			// level.trials.add(jsonTrial.Id);
			CreateTrial(jsonTrial, path);
			CreateTrial(jsonTrial, Builder.pathLevelsBackUp);
		}
		level.jsonTrials.clear();
		writeLevelJson(level,Resources.Paths.finalInternalPath);
		writeLevelJson(level,Builder.pathLevelsBackUp);
	}

	public static void extract(JsonLevel level) {
		Gdx.app.debug(Builder.TAG,"Procesando nivel "+level.Id);
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
		Gdx.app.debug(Builder.TAG, "Lista de recursos a procesar creada");
		// Con el listado de ids que corresponden al nivel los exportamos
		PCBuilder.export(listado,"level"+level.Id);
	}

	public static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		Builder.contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		// jsonLevel.appVersion = AppVersion;
		jsonLevel.Id = Builder.contadorLevels;
		jsonLevel.resourceVersion = Builder.ResourceVersion;
		jsonLevel.levelVersion = Builder.levelVersionFinal;
		return jsonLevel;
	}

	/**
	 * This method transforms SVGs of a list of ids that must be in the same folder into PNG files, join it at a ATLAS file and copy the ATLAS, and the .meta files
	 * to a folder in android's assets 
	 */
	static void export(Array<Integer> ids, String folderName){
		Gdx.app.debug(Builder.TAG, "Exportando los recursos correspondientes a " + folderName);
		// We clean the destiny folder
		File folder = new File(Resources.Paths.finalInternalPath+folderName+"/");
		if (folder.exists()) {
			// clean the folder from old stuff
			try {
				FileUtils.cleanDirectory(folder);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else {
			// create folder
			folder.mkdir();
		}
		// clean the temp folder
		File tempDirectory = new File(Resources.Paths.processingTempFolder);
		try {
			FileUtils.cleanDirectory(tempDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// Then we convert all files to PNG into a temp folder and copy the metadata to assets folder
		for (int id : ids){
			File resource = new File (Resources.Paths.ResourcesBuilder+id+".svg");
			convertirSVGtoPNG(resource);
			if (id > Resources.Reservados) { // Means that is not a category with no audio
				SVGtoMp3 converter = new SVGtoMp3(resource, Resources.Paths.finalInternalPath+folderName+"/");
			}
			moveMeta(resource,folderName);
		}
		
		// Create atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, Resources.Paths.processingTempFolder, Resources.Paths.finalInternalPath, folderName + "img");
		Gdx.app.debug(Builder.TAG, "Recursos correctamente exportados: " + folderName+".");
	}

	public static void cleanAssets () {
		// borramos la carptea destino interna
		File internalPath = new File(Resources.Paths.finalInternalPath);
		try {
			FileUtils.cleanDirectory(internalPath);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// Borramos la carptea destino local donde se guardan los cambios en los niveles y las configuraciones
		FileHandle to = Gdx.files.local(Resources.Paths.LocalSettingsCopy);
		to.emptyDirectory();
	}

	public static void verifyResources(){
		// Se fija q exista el paquete de recursos de la version actual
		File file = new File(Resources.Paths.ResourcesBuilder);
		
		if (!new File(Resources.Paths.ResourcesBuilder).exists()) {
			System.out.println("Primero debe crear los recursos version:" + Builder.ResourceVersion);
			System.exit(0);
		}
	}

	public static void verifyLevelVersion(){
		// Verifica que no haya niveles ya numerados con la version marcada
		FileHandle file = Gdx.files.internal(Resources.Paths.finalInternalPath + "level" + 1 + ".meta");
		if (file.exists()) {
			String savedData = file.readString();
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				JsonLevel jsonLevel = json.fromJson(JsonLevel.class, savedData);
				if (jsonLevel.levelVersion >= Builder.levelVersion) {
					Gdx.app.error(Builder.TAG, "OJO! Deberia actualizar la version del level. Se modificara sola al numero: " + (jsonLevel.levelVersion+1));
					Builder.levelVersionFinal=jsonLevel.levelVersion+1;
				} else {
					Builder.levelVersionFinal = Builder.levelVersion;
				}
			}
		} 
	}

}
