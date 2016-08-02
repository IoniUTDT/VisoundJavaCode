package com.turin.tur.main.diseno;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.Visound;
import com.turin.tur.main.levelsDesign.Level.ELECCION;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.InternetNuevo;
import com.turin.tur.main.util.InternetNuevo.TIPOdeENVIO;
import com.turin.tur.main.util.builder.Builder;

public class Session {

	private static final String TAG = Session.class.getName();
	public User user;
	public long sessionInstance = TimeUtils.millis();
	public int codeVersion = Constants.CODEVERSION;
	public int resourcesVersion = Builder.ResourceVersion;
	public ApplicationType plataforma = Gdx.app.getType();
	
	public Session() {
		this.user = User.loadUser(); 
		InternetNuevo.agregarEnvio(this, TIPOdeENVIO.SESION, Long.toString(user.id));
	}
	
	public static class User {
		public static final String USERFILE = Visound.pathLogs + "/user.txt";
		public static final String eleccionFile = Visound.pathLogs + "/eleccion.txt";

		public long id;
		public FASEdeEXPERIMENTO faseDeExperimentoActiva;
		private Array<LevelJugado> levelsJugados = new Array<LevelJugado>();
		public ELECCION eleccion;
		
		User () {
			this.id = TimeUtils.millis(); 
			this.faseDeExperimentoActiva = FASEdeEXPERIMENTO.Intro;
			// Se fija si hay una eleccion guardada
			FileHandle file = Gdx.files.local(User.eleccionFile);
			if (file.exists()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				String savedData = file.readString();
				this.eleccion = json.fromJson(ELECCION.class, savedData);
			} else {
				this.eleccion = ELECCION.values()[(int) (MathUtils.random() * ELECCION.values().length)];
				Json json = new Json();
				json.setUsePrototypes(false);
				file.writeString(json.toJson(this.eleccion), false);
			}
		}

		public void saveUserInfo() {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(USERFILE, json.toJson(this));
		}

		public void pasarFase() {
			this.faseDeExperimentoActiva = this.faseDeExperimentoActiva.etapaSiguiente;
			this.saveUserInfo();
		}
		
		public static User loadUser() {
			FileHandle userFile = Gdx.files.local(User.USERFILE);
			if (userFile.exists()) {
				String savedData = FileHelper.readLocalFile(User.USERFILE);
				if (!savedData.isEmpty()) {
					Json json = new Json();
					json.setUsePrototypes(false);
					User user = json.fromJson(User.class, savedData);
					return user;
				} else { 
					Gdx.app.error(TAG,"No se a podido encontrar la info del usuario");
					return null;
				}
			} else {
				Gdx.app.debug(TAG, "Creando nuevo usuario");
				User user = new User();
				user.saveUserInfo();
				return user;
			}
		}	
		
		public boolean alreadyPlayed (LISTAdeNIVELES identificador) {
			for (LevelJugado jugado : levelsJugados) {
				if (jugado.contexto == faseDeExperimentoActiva) {
					if (jugado.identificador == identificador) {
						return true;
					}
				}
			}
			return false;
		}
		
		public LISTAdeNIVELES nextLevelToPlay () {
			for (LISTAdeNIVELES identificador : faseDeExperimentoActiva.niveles) {
				if (alreadyPlayed(identificador)) {
				} else {
					return identificador;
				}
			}
			
			return null;
		}
		
		public int lastNivel (LISTAdeNIVELES identificador) {
			int lastnivel = -1;
			for (LevelJugado level : levelsJugados) {
				for (ELECCION eleccion : identificador.eleccionesIncluidas) {
					if (level.identificador.eleccionesIncluidas.contains(eleccion, false)) {
						if (level.identificador.listaDeRecursos == identificador.listaDeRecursos) {
							lastnivel = level.nivelAlFinalizar;
						}
					}
				}
			}
			return lastnivel;
		}

		public void levelFinished(LISTAdeNIVELES identificador, int nivelSenal, double desviacion) {
			LevelJugado levelFinalizado = new LevelJugado();
			levelFinalizado.identificador = identificador;
			levelFinalizado.contexto = this.faseDeExperimentoActiva;
			levelFinalizado.nivelAlFinalizar = nivelSenal;
			levelFinalizado.desviacionAlFinalizar = desviacion;
			levelsJugados.add(levelFinalizado);
			this.saveUserInfo();
			if (this.nextLevelToPlay() == null) {
				this.pasarFase();
			}
		}
	}
	
	public enum FASEdeEXPERIMENTO {
		Intro(), Tutorial(), TestInicial(), 
		Entrenamiento1(), Entrenamiento2(), Entrenamiento3(), Entrenamiento4(), 
		TestFinal(), ExperimentoCompleto();
		
		private FASEdeEXPERIMENTO etapaSiguiente;
		public Array<LISTAdeNIVELES> niveles = new Array<LISTAdeNIVELES>();
		
		static {
			Intro.etapaSiguiente = Tutorial;
			Tutorial.etapaSiguiente = TestInicial;
			TestInicial.etapaSiguiente = Entrenamiento1;
			Entrenamiento1.etapaSiguiente = Entrenamiento2;
			Entrenamiento2.etapaSiguiente = Entrenamiento3;
			Entrenamiento3.etapaSiguiente = Entrenamiento4;
			Entrenamiento4.etapaSiguiente = TestFinal;
			TestFinal.etapaSiguiente = ExperimentoCompleto;
			ExperimentoCompleto.etapaSiguiente = ExperimentoCompleto;
			
			Tutorial.niveles.addAll(LISTAdeNIVELES.Ejemplos, LISTAdeNIVELES.ParalelismoTutorial, LISTAdeNIVELES.AngulosTutorial);
			TestInicial.niveles.addAll(LISTAdeNIVELES.TESTP30, LISTAdeNIVELES.TESTP60, LISTAdeNIVELES.TESTP120, LISTAdeNIVELES.TESTP150,
					LISTAdeNIVELES.TESTA30, LISTAdeNIVELES.TESTA60, LISTAdeNIVELES.TESTA120, LISTAdeNIVELES.TESTA150);
			Entrenamiento1.niveles.addAll(
					LISTAdeNIVELES.ENTRENAMIENTOA30INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOA30MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOA30FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOP30INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOP30MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOP30FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOA60INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOA60MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOA60FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOP60INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOP60MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOP60FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOA120INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOA120MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOA120FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOP120INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOP120MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOP120FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOA150INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOA150MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOA150FINAL,
					LISTAdeNIVELES.ENTRENAMIENTOP150INICIAL,
					LISTAdeNIVELES.ENTRENAMIENTOP150MEDIO,
					LISTAdeNIVELES.ENTRENAMIENTOP150FINAL
					);
			Entrenamiento2.niveles.addAll(Entrenamiento1.niveles);
			Entrenamiento3.niveles.addAll(Entrenamiento1.niveles);
			Entrenamiento4.niveles.addAll(Entrenamiento1.niveles);
			TestFinal.niveles.addAll(LISTAdeNIVELES.TESTP30, LISTAdeNIVELES.TESTP60, LISTAdeNIVELES.TESTP120, LISTAdeNIVELES.TESTP150,
					LISTAdeNIVELES.TESTA30, LISTAdeNIVELES.TESTA60, LISTAdeNIVELES.TESTA120, LISTAdeNIVELES.TESTA150);
		}
		
		public Array<LISTAdeNIVELES> listaDeNivelesFiltrados (ELECCION eleccion) {
			Array<LISTAdeNIVELES> listaNiveles = new Array<LISTAdeNIVELES>();
			for (LISTAdeNIVELES nivel : niveles) {
				if (nivel.eleccionesIncluidas.contains(eleccion, false) || (nivel.eleccionesIncluidas.contains(ELECCION.TODAS,false))) {
					listaNiveles.add(nivel);
				}
			}
			return listaNiveles;
		}
		
	}
	
	public static class LevelJugado {
		LISTAdeNIVELES identificador;
		FASEdeEXPERIMENTO contexto;
		long Instance = TimeUtils.millis();
		double desviacionAlFinalizar;
		int nivelAlFinalizar;
	}

}