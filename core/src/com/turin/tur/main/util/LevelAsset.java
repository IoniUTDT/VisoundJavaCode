package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.turin.tur.main.util.Constants.Resources;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class LevelAsset implements Disposable, AssetErrorListener {

	public final String TAG = LevelAsset.class.getName();
	private AssetManager assetManager;
	TextureAtlas atlas;
	int level;
	
	public LevelAsset (int levelId) {
		this.level = levelId;
		this.assetManager = new AssetManager();
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		String atlasFilepath = Resources.Paths.resources + "level" + levelId + "img.atlas";
		assetManager.load(atlasFilepath, TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}
		atlas = assetManager.get(atlasFilepath, TextureAtlas.class);
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset,
			Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName,
				throwable);
	}

	public Sprite imagen(int Id) {
		return new Sprite(this.atlas.findRegion("" + Id));
	}
	
	public Sound sound(int Id) {
		String soundpath = Resources.Paths.resources + "level" + this.level + "/" + Id + ".mp3";
		assetManager.load(soundpath, Sound.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		return assetManager.get(soundpath, Sound.class);
	}
}
