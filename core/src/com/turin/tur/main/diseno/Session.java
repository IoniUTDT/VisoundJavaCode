package com.turin.tur.main.diseno;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
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
		this.user = loadUser(); 
		Internet.addDataToSend(this, TIPO_ENVIO.NEWSESION, "Visound");
	}
	
	public User loadUser() {
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
	
	public static class User {
		public static final String USERFILE = "logs/user.txt";

		public long id;
		public FASEdeEXPERIMENTO faseDeExperimentoActiva;
		
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
		
	}
	
	public enum FASEdeEXPERIMENTO {
		Intro, Tutorial, TestInicial, Entrenamiento1, TestFinal, ExperimentoCompleto;
		
		private FASEdeEXPERIMENTO etapaSiguiente;
		
		static {
			Intro.etapaSiguiente = Tutorial;
			Tutorial.etapaSiguiente = TestInicial;
			TestInicial.etapaSiguiente = Entrenamiento1;
			Entrenamiento1.etapaSiguiente = TestFinal;
			TestFinal.etapaSiguiente = ExperimentoCompleto;
			ExperimentoCompleto.etapaSiguiente = ExperimentoCompleto;
		}
		
		public FASEdeEXPERIMENTO etapaSiguiente() {
			return this.etapaSiguiente;
		}
	}
	

}
