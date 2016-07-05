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
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.FileHelper;

public class PCBuilder {

	static final String TAG = PCBuilder.class.getName();
	private static final String fileLevelVersion = "levelVersion.meta";

	/**
	 * Funcion que verifica que no haya recursos creados con la misma version que la que se quiere crear
	 */
	public static void verifyResourcesVersion() {
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(ResourcesCategorias.Paths.ResourcesBuilder);
		Gdx.app.debug(TAG, file.getAbsolutePath());
		if (file.exists()) {
			System.out.println("Modifique la version de los recursos porque ya existe una carpeta con la version actual");
			System.exit(0);
		}
	}

	/**
	 * This method move the file with the metadata of a resource to specified folder inside the android's assets
	 * @param file
	 * @param identificadorNivel
	 */ 
	static void moveMeta(File file, Level.LISTAdeNIVELES identificadorNivel){
		file = new File(ResourcesCategorias.Paths.ResourcesBuilder + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".meta");
		Path FROM = Paths.get(file.getAbsolutePath());
		File out = new File(ResourcesCategorias.Paths.finalInternalPath + "/" + identificadorNivel.toString() + "/" + file.getName());
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

	static void CheckLevels() {
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
			file = new File(ResourcesCategorias.Paths.processingTempFolder + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
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

	public static void writeTrialsJson(Array<JsonTrial> jsonTrials, Level.LISTAdeNIVELES identificador) {
		Json json = new Json();
		json.setUsePrototypes(false);
		for (JsonTrial trial : jsonTrials) {
			FileHelper.writeLocalFile(Level.folderResources(identificador) + trial.Id + ".trial", json.toJson(trial));
		}
	}

	public static void CreateTrial(JsonTrial jsonTrial, String path) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
	}

	public static void buildJsonsTrials (Array<JsonTrial> jsonTrials, Level.LISTAdeNIVELES identificador) {
		// If resources already exported, the the folder was cleaned.
		String path = Level.folderResources(identificador);
		for (JsonTrial jsonTrial : jsonTrials) {
			CreateTrial(jsonTrial, path);
		}
		writeTrialsJson(jsonTrials, identificador);
	}

	public static void extract(Array<JsonTrial> listaDeTrials, Level.LISTAdeNIVELES identificadorNivel) {
		Gdx.app.debug(Builder.TAG,"Procesando nivel "+identificadorNivel.toString());
		Array<Integer> listado = new Array<Integer>(); // Listado de ids de recursos utilizados
		for (JsonTrial jsonTrial : listaDeTrials) {
			for (int idResource : jsonTrial.elementosId) {
				if (!listado.contains(idResource,false)) {
					listado.add(idResource);
				}
			}
			if (!listado.contains(jsonTrial.rtaCorrectaId,false)) {
				listado.add(jsonTrial.rtaCorrectaId);
			}
		}
		// Gdx.app.debug(Builder.TAG, "Lista de recursos a procesar creada");
		// Con el listado de ids que corresponden al nivel los exportamos
		PCBuilder.export(listado,identificadorNivel);
	}

	/*
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
	*/

	/**
	 * This method transforms SVGs of a list of ids that must be in the same folder into PNG files, join it at a ATLAS file and copy the ATLAS, and the .meta files
	 * to a folder in android's assets 
	 */
	static void export(Array<Integer> ids, Level.LISTAdeNIVELES identificadorNivel){
		// Gdx.app.debug(Builder.TAG, "Exportando los recursos correspondientes a " + identificadorNivel.toString());
		// We clean the destiny folder
		File folder = new File(Level.folderResources(identificadorNivel)); 
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
		File tempDirectory = new File(ResourcesCategorias.Paths.processingTempFolder);
		try {
			FileUtils.cleanDirectory(tempDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// Then we convert all files to PNG into a temp folder and copy the metadata to assets folder
		for (int id : ids){
			File resource = new File (ResourcesCategorias.Paths.ResourcesBuilder+id+".svg");
			convertirSVGtoPNG(resource);
			if (id > ResourcesCategorias.NumeroDeRecursosReservados) { // Means that is not a category with no audio
				SVGtoMp3 converter = new SVGtoMp3(resource, ResourcesCategorias.Paths.finalInternalPath+identificadorNivel+"/");
			}
			moveMeta(resource,identificadorNivel);
		}
		
		// Create atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, ResourcesCategorias.Paths.processingTempFolder, Level.folderResources(identificadorNivel), "Imagenes"); 
		Gdx.app.debug(Builder.TAG, "Recursos correctamente exportados: " + identificadorNivel.toString()+".");
	}

	public static void cleanAssets () {
		// borramos la carptea destino interna
		File internalPath = new File(ResourcesCategorias.Paths.finalInternalPath);
		try {
			FileUtils.cleanDirectory(internalPath);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// Borramos la carptea destino local donde se guardan los cambios en los niveles y las configuraciones
		FileHandle to = Gdx.files.local(ResourcesCategorias.Paths.LocalSettingsCopy);
		to.emptyDirectory();
	}

	public static void verifyResources(){
		// Se fija q exista el paquete de recursos de la version actual
		File file = new File(ResourcesCategorias.Paths.ResourcesBuilder);
		if (!new File(ResourcesCategorias.Paths.ResourcesBuilder).exists()) {
			System.out.println("Primero debe crear los recursos version:" + Builder.ResourceVersion);
			System.exit(0);
		}
	}

	public static void verifyLevelVersion(){
		// Verifica que no haya niveles ya numerados con la version marcada
		FileHandle file = Gdx.files.internal(ResourcesCategorias.Paths.finalInternalPathSettings + fileLevelVersion);
		if (file.exists()) {
			String savedData = file.readString();
			try {
				int levelVersionReaded = Integer.valueOf(savedData);
				if (levelVersionReaded >= Builder.levelVersion) {
					Builder.levelVersionFinal=levelVersionReaded+1;
					Gdx.app.error(Builder.TAG, "OJO! Deberia actualizar la version del level. Se modificara sola al numero: " + Builder.levelVersionFinal);
				}
			} catch (NumberFormatException e) {
				Gdx.app.error(Builder.TAG, "Error leyendo la version de los niveles preexistentes("+ e.getMessage() +"). Se llevara la version " + Builder.levelVersionFinal);
			}
		} 
		FileHelper.writeLocalFile(ResourcesCategorias.Paths.finalInternalPathSettings + fileLevelVersion, Builder.levelVersionFinal+"");
	}

}
