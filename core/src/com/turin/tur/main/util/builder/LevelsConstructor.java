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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ParametrosSetupParalelismo;
import com.turin.tur.main.experiments.Experiments.SetupUmbralAngulos;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.SVGtoMp3;;

public class LevelsConstructor {


	private static final String TAG = LevelsConstructor.class.getName();
	private Array<Array<Integer>> listadosIdbyCategory = new Array<Array<Integer>>();
	private String pathOldLevels = Resources.Paths.fullLevelsPath.substring(0, Resources.Paths.fullLevelsPath.length()-1)+"olds/"+TimeUtils.millis()+"/";
	
	//static Array<JsonResourcesMetaData> listadoRecursos = ResourcesSelectors.listadoRecursos;
	//static Array<Array<Integer>> listadosId = ResourcesSelectors.listadosId;
	//static Array<Agrupamientos> listadosGrupos = ResourcesSelectors.listadosGrupos;

	static int contadorLevels = 0;
	static int contadorTrials = 0;
	
	public LevelsConstructor() {
		this.initCategoryList();
		this.verifyLevelVersion();
		this.verifyResources();
		this.cleanAssets();
		// this.exportCategories();
		if (Builder.categorizar) {
			//categorizeResources();// Categoriza los recursos para que despues se pueda seleccionar recursos conceptualmente
		}
		// Crea los niveles
		if (Builder.AppVersion == "UmbralCompleto") {
			//this.MakeLevelParalelismoUmbral();
		}
		
		if (Builder.AppVersion == "UmbralCompletoAngulos") {
			Levels levelbuilder = new Levels();
			levelbuilder.MakeLevelsAngulosUmbral();
		}

	}

	private void initCategoryList(){
		for (int i=0;i<Categorias.values().length+1;i++) {
			listadosIdbyCategory.add(new Array<Integer>());
		}
	}
	
	private void verifyLevelVersion(){
		// Verifica que no haya niveles ya numerados con la version marcada
		File file = new File(Resources.Paths.finalPath + "level" + 1 + ".meta");
		if (file.exists()) {
			String savedData = FileHelper.readLocalFile(Resources.Paths.finalPath + "level" + 1 + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				JsonLevel jsonLevel = json.fromJson(JsonLevel.class, savedData);
				if (jsonLevel.levelVersion >= Builder.levelVersion) {
					Gdx.app.error(TAG, "OJO! Deberia actualizar la version del level. Se modificara sola al numero: " + (jsonLevel.levelVersion+1));
					Builder.levelVersionFinal=jsonLevel.levelVersion+1;
				} else {
					Builder.levelVersionFinal = Builder.levelVersion;
				}
			}
		} 
	}

	private void verifyResources(){
		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(Resources.Paths.fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recursos version:" + Builder.ResourceVersion);
			System.exit(0);
		}
	}
	
	private void cleanAssets () {
		File tempDirectory = new File(Resources.Paths.finalPath);
		try {
			FileUtils.cleanDirectory(tempDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Select to export the resources of categories
	 */
	private void exportCategories () {
		Array<Integer> listaIds= new Array<Integer>();
		for (Categorias categoria : Constants.Resources.Categorias.values()){
			listaIds.add(categoria.ID);
		}
		this.export(listaIds, "categories");
	}
	
	/**
	 * This method transforms SVGs of a list of ids that must be in the same folder into PNG files, join it at a ATLAS file and copy the ATLAS, and the .meta files
	 * to a folder in android's assets 
	 */
	private void export(Array<Integer> ids, String folderName){
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
			convertirSVGtoPNG(resource);
			if (id > Resources.Reservados) { // Means that is not a category with no audio
				SVGtoMp3 converter = new SVGtoMp3(resource, Resources.Paths.finalPath+folderName+"/");
			}
			moveMeta(resource,folderName);
		}
		
		// Create atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, Resources.Paths.ProcessingPath, Resources.Paths.finalPath, folderName+"_");
		Gdx.app.debug(TAG, "Recursos correctamente exportados: " + folderName+".");
	}
	
	
	private class Levels {
		
		public void MakeLevelsAngulosUmbral() {
			// Cargamos los datos del setup
			String path = Resources.Paths.currentVersionPath+"extras/jsonSetupUmbralAngulos.meta";
			String savedData = FileHelper.readLocalFile(path);
			Json json = new Json();
			json.setUsePrototypes(false);
			SetupUmbralAngulos setup = json.fromJson(SetupUmbralAngulos.class, savedData);
			
			Array<Integer> angulosdeReferencia = new Array<Integer>(); 
			
			for (int i = 0; i<=(90/setup.saltoGrande); i++) { // Hacemos solo para el primer cuadrante
				angulosdeReferencia.add(setup.saltoGrande*i);
			}

			while (angulosdeReferencia.size>0) { // Seleccionamos angulos en forma random segun parametros del setup
				Array<Integer> angulosElegidos = new Array<Integer>();
				// Los quita de la lista general y lo pasa a la de los que se van a incluir en el proximo nivel
				if (angulosdeReferencia.size>=setup.numeroDeRefrenciasConjuntas) {
					for (int i=0; i<setup.numeroDeRefrenciasConjuntas; i++) {
						angulosElegidos.add(angulosdeReferencia.removeIndex(MathUtils.random(angulosdeReferencia.size-1)));
					}	
					if (angulosdeReferencia.size==1) { // Agregamos el ultimo que queda al grupo anterior si queda uno solo...
						angulosElegidos.addAll(angulosdeReferencia);
						angulosdeReferencia.clear();
					}
				} else {
					angulosElegidos.addAll(angulosdeReferencia);
					angulosdeReferencia.clear();
				}
				// Ahora creamos el nivel
				JsonLevel level = crearLevel();
				level.tipoDeLevel = TIPOdeLEVEL.UMBRALANGULO;
				level.angulosReferencia = angulosElegidos;
				level.levelTitle = "";
				for (int i=0; i<angulosElegidos.size; i++) {
					level.levelTitle = level.levelTitle + " R: "+angulosElegidos.get(i);
				}
				level.randomTrialSort=false;
				level.show = true;

				// agregamos un trial por recurso. 
				for (int anguloRef:angulosElegidos) {
					for (int recurso:setup.idsResourcesByAngle.get(setup.angulos.indexOf(anguloRef, false))) {
						JsonTrial trial = crearTrial("Selecciones a que categoria pertenece el angulo", "", DISTRIBUCIONESenPANTALLA.LINEALx3,
								new int[] {Constants.Resources.Categorias.Grave.ID,Constants.Resources.Categorias.Recto.ID,Constants.Resources.Categorias.Agudo.ID}, TIPOdeTRIAL.TEST, recurso, false, true, false);
						savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + recurso + ".meta");
						json = new Json();
						json.setUsePrototypes(false);
						trial.jsonEstimulo =  json.fromJson(JsonResourcesMetaData.class, savedData);
						level.jsonTrials.add(trial); 
					}
				}
				level.setup = setup;
				// TODO
				extract(level);
				buildJsons(level);
			}
		}

		private void extract(JsonLevel level) {
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
		
		private void buildJsons (JsonLevel level) {
			// If resources already exported, the the folder was cleaned.
			String path = Resources.Paths.finalPath + "/level" + level.Id + "/";
			for (JsonTrial jsonTrial : level.jsonTrials) {
				level.trials.add(jsonTrial.Id);
				CreateTrial(jsonTrial, path);
				CreateTrial(jsonTrial, pathOldLevels);
			}
			level.jsonTrials.clear();
			CreateLevel(level,Resources.Paths.finalPath);
			CreateLevel(level,pathOldLevels);
		}
		
		public void CreateTrial(JsonTrial jsonTrial, String path) {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
		}
		
		public void CreateLevel(JsonLevel jsonLevel, String path) {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeFile(path + "level" + jsonLevel.Id + ".meta", json.toJson(jsonLevel));
		}

		private JsonTrial crearTrial(String title, String caption, DISTRIBUCIONESenPANTALLA distribucion, int[] elementos, TIPOdeTRIAL modo,
				int rtaCorrecta, Boolean randomAnswer, Boolean randomSort, Boolean feedback) {
			// Crea un JsonTrial y aumenta en 1 el contador de trials
			contadorTrials += 1;
			JsonTrial jsonTrial = new JsonTrial();
			jsonTrial.Id = contadorTrials;
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
			jsonTrial.parametrosParalelismo = new ParametrosSetupParalelismo();
			return jsonTrial;
		}

		private JsonLevel crearLevel() {
			// Crea un JsonLevel y aumenta en 1 el contador de niveles
			contadorLevels += 1;
			JsonLevel jsonLevel = new JsonLevel();
			jsonLevel.appVersion = Builder.AppVersion;
			jsonLevel.Id = contadorLevels;
			jsonLevel.resourceVersion = Builder.ResourceVersion;
			jsonLevel.levelVersion = Builder.levelVersionFinal;
			return jsonLevel;
		}
		
	}
	
	/**
	 * 
	 * Funcion que transforma los archivos svg en png
	 * 
	 * @param file
	 * @param i 
	 */
	private void convertirSVGtoPNG(File file) {
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
	
	/**
	 * This method move the file with the metadata of a resource to specified folder inside the android's assets
	 * @param file
	 * @param folder
	 */
	private void moveMeta(File file, String folder){
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
}
