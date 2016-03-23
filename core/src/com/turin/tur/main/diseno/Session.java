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
	public long userId;
	public long sessionInstance = TimeUtils.millis();
	public int codeVersion = Constants.CODEVERSION;
	// public int levelVersion = Builder.levelVersionFinal;
	public int resourcesVersion = Builder.ResourceVersion;
	public ApplicationType plataforma = Gdx.app.getType();

	public Session() {
		Internet.Check();
		this.userId = userId();
		Internet.sendData(this, TIPO_ENVIO.NEWSESION, "Visound");
	}
	
	public void saveUserId(Long id) {
		//JsonUser jsonUser = new JsonUser();
		//jsonUser.Id = this.id;
		//jsonUser.save();
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(Constants.USERFILE, id.toString());
	}

	public Long userId() {
		FileHandle userFile = Gdx.files.local(Constants.USERFILE);
		if (userFile.exists()) {
			String savedData = FileHelper.readLocalFile(Constants.USERFILE);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				Long data = json.fromJson(Long.class, savedData);
				return data;
			} else { 
				Gdx.app.error(TAG,"No se a podido encontrar la info del usuario");
				return null;
			}
		} else {
			Gdx.app.debug(TAG, "Creando nuevo usuario");
			Long newId = TimeUtils.millis(); 
			FileHelper.writeLocalFile(Constants.USERFILE, newId.toString());
			return newId;
		}
	}	

}
