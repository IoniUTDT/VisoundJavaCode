package com.turin.tur.main.diseno;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Internet.TIPO_ENVIO;
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
		Internet.addDataToSend(this, TIPO_ENVIO.NEWSESION, "Visound");
	}
	
	public static class User {
		public static final String USERFILE = "logs/user.txt";

		public long id;
		public FASEdeEXPERIMENTO faseDeExperimentoActiva;
		private Array<LevelsJugados> levelsJugados = new Array<LevelsJugados>();
		
		User () {
			this.id = TimeUtils.millis(); 
			this.faseDeExperimentoActiva = FASEdeEXPERIMENTO.Intro;
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
			for (LevelsJugados jugado : levelsJugados) {
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
	}
	
	public enum FASEdeEXPERIMENTO {
		Intro(), Tutorial(), TestInicial(), Entrenamiento1(), TestFinal(), ExperimentoCompleto()
		;
		
		private FASEdeEXPERIMENTO etapaSiguiente;
		public Array<LISTAdeNIVELES> niveles = new Array<LISTAdeNIVELES>();
		
		static {
			Intro.etapaSiguiente = Tutorial;
			Tutorial.etapaSiguiente = TestInicial;
			TestInicial.etapaSiguiente = Entrenamiento1;
			Entrenamiento1.etapaSiguiente = TestFinal;
			TestFinal.etapaSiguiente = ExperimentoCompleto;
			ExperimentoCompleto.etapaSiguiente = ExperimentoCompleto;
			Tutorial.niveles.addAll(LISTAdeNIVELES.Ejemplos, LISTAdeNIVELES.ParalelismoTutorial, LISTAdeNIVELES.AngulosTutorial);
		}
		
		public FASEdeEXPERIMENTO etapaSiguiente() {
			return this.etapaSiguiente;
		}
		
	}
	
	public class LevelsJugados {
		LISTAdeNIVELES identificador;
		FASEdeEXPERIMENTO contexto;
		long Instance = TimeUtils.millis();
	}

}
