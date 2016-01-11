package com.turin.tur.main.util.builder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Level.Significancia;
import com.turin.tur.main.diseno.Level.TIPOdeSIGNIFICANCIA;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ParametrosSetupParalelismo;
import com.turin.tur.main.experiments.Experiments.SetupUmbralAngulos;
import com.turin.tur.main.experiments.Experiments.SetupUmbralParalelismo;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Stadistics;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;

public class LevelsConstructor {


	private static final String TAG = LevelsConstructor.class.getName();
	private Array<Array<Integer>> listadosIdbyCategory = new Array<Array<Integer>>();
	
	//static Array<JsonResourcesMetaData> listadoRecursos = ResourcesSelectors.listadoRecursos;
	//static Array<Array<Integer>> listadosId = ResourcesSelectors.listadosId;
	//static Array<Agrupamientos> listadosGrupos = ResourcesSelectors.listadosGrupos;

	static int contadorLevels = 0;
	static int contadorTrials = 0;
	
	public LevelsConstructor() {
		this.initCategoryList();
		this.verifyLevelVersion();
		this.verifyResources();
		if (Builder.categorizar) {
			//categorizeResources();// Categoriza los recursos para que despues se pueda seleccionar recursos conceptualmente
		}
		this.moveOldLevelsToArchive();
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
					System.out.println("Cambie la version de los niveles a crear para que sean mayor a la version actual: " + Builder.levelVersion);
					System.exit(0);
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

	private void moveOldLevelsToArchive() {
		// Manda los levels que ya estaban creados a una carpeta nueva para archivarlos
		File oldDir = new File(Resources.Paths.finalPath);
		String str = Resources.Paths.fullLevelsPath.substring(0, Resources.Paths.fullLevelsPath.length()-1)+"olds/"+TimeUtils.millis()+"/";
		File newDir = new File(str);
		newDir.mkdirs();
		oldDir.renameTo(newDir);
		new File(Resources.Paths.finalPath).mkdirs();
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
					// Array<Integer> recursos = ResourcesSelectors.findAngles(anguloRef); // Linea vieja, es muuuy lento
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
				extract(level);
				buildJsons(level);
			}
		}

		private void extract(JsonLevel level) {
			Gdx.app.debug(TAG,"Procesando nivel "+level.Id);
			Array<Integer> listado = new Array<Integer>(); // Listado de ids de recursos utilizados
			for (JsonTrial jsonTrial : level.jsonTrials) {
				for (int idResource : jsonTrial.elementosId) {
					listado.add(idResource);
				}
				listado.add(jsonTrial.rtaCorrectaId);
			}
			// Limpiamos la carpeta temporal
			
			for (int id : listado) {
				File fileSVG = new File(Resources.Paths.fullCurrentVersionPath + id + ".svg");
				convertirSVGtoPNG(fileSVG);
			}
		}
		
		private void buildJsons (JsonLevel level) {
			String path = Resources.Paths.finalPath + '/' + level.Id + '/';
			for (JsonTrial jsonTrial : level.jsonTrials) {
				level.trials.add(jsonTrial.Id);
				CreateTrial(jsonTrial, path);
			}
			level.jsonTrials.clear();
			CreateLevel(level,Resources.Paths.finalPath);
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
			jsonLevel.levelVersion = Builder.levelVersion;
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
			file = new File(Resources.Paths.fullUsedResources + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
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

}
