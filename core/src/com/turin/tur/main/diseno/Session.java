package com.turin.tur.main.diseno;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.Visound.TipoDeAplicacion;
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
	public TipoDeAplicacion tipoDeAplicacion;

	public Session() {
		this.user = loadUser();
		this.tipoDeAplicacion = this.user.selectAppType(); 
		Internet.addDataToSend(this, TIPO_ENVIO.NEWSESION, "Visound");
	}
	
	public void saveUserId(Long id) {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(Constants.USERFILE, id.toString());
	}

	public User loadUser() {
		FileHandle userFile = Gdx.files.local(Constants.USERFILE);
		if (userFile.exists()) {
			String savedData = FileHelper.readLocalFile(Constants.USERFILE);
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
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(Constants.USERFILE, json.toJson(user));
			return user;
		}
	}	
	
	public class User {
		long id;
		int numberOfLevelsPlayed;
		User () {
			this.id = TimeUtils.millis(); 
			this.numberOfLevelsPlayed = 0;
		}
		
		public TipoDeAplicacion selectAppType () {
			switch (this.numberOfLevelsPlayed) {
			case 1:
				return TipoDeAplicacion.Tutorial;
			case 2:
				return TipoDeAplicacion.Test;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				return TipoDeAplicacion.Entrenamiento;
			case 9:
				return TipoDeAplicacion.Test;
			default:
				return null;
			}
		}
	}

}
