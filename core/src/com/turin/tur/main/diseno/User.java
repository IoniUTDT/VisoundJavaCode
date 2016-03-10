package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;


public class User {

	private static final String TAG = User.class.getName();
	
	public long id;
	
	public void save() {
		JsonUser jsonUser = new JsonUser();
		jsonUser.Id = this.id;
		jsonUser.save();
	}

	static public void CreateUser() {
		User user = LoadNewUser();
		user.save();
	}

	private static User LoadNewUser() {
		User user = new User();
		user.id = GenerateId();
		return user;
	}

	public static User Load() {
		User user = new User();
		JsonUser jsonUser = new JsonUser();
		jsonUser = JsonUser.load();
		// Transpasa los datos
		user.id = jsonUser.Id;
		return user;
	}
	
	private static long GenerateId() {
		long Id = TimeUtils.millis();
		return Id;
	}

	public static class JsonUser {
		public long Id;
		
		public void save(){
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeLocalFile(Constants.USERFILE, json.toJson(this));
		}
		
		public static JsonUser load(){
			String savedData = FileHelper.readLocalFile(Constants.USERFILE);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				return json.fromJson(JsonUser.class, savedData);
			} else { Gdx.app.error(TAG,"No se a podido encontrar la info del usuario"); }
			return null;
		}
	}
}
