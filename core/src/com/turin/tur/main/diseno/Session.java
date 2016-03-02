package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.builder.Builder;

public class Session {

	private static final String TAG = Session.class.getName();
	public User user;
	public long sessionInstance = TimeUtils.millis();
	public int codeVersion = Constants.CODEVERSION;
	public int levelVersion = Builder.levelVersionFinal;
	public int resourcesVersion = Builder.ResourceVersion;

	public Session() {
		Internet.Check();
		loadUser();
	}
	
	public static class SessionLog {
		public Session session;
		public long levelInstance;
		public String expName;
	}

	private void loadUser() {
		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		this.user = User.Load();
	}

}
