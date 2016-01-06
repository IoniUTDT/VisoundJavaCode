package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;



public class LevelInterfaz {
	
	// private static final String TAG = LevelInterfaz.class.getName();
	private Level levelInfo;   
	// private int trialNumber; // Numero de trial que esta activo
	private Trial trial;
	
	public LevelInterfaz (Level levelInfo, int trialNumber, Trial trial){
		this.trial = trial;
		this.levelInfo = levelInfo;		
	}
	
	public void renderFps(SpriteBatch batch, OrthographicCamera cameraGUI) {
		float x = cameraGUI.viewportWidth - cameraGUI.viewportWidth*1/10;
		float y = cameraGUI.viewportHeight - cameraGUI.viewportHeight*1/20;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultFont;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.getData().setScale(Constants.factorEscala());
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
		
	}
	
	public void renderTitle (SpriteBatch batch, OrthographicCamera cameraGUI) {
		BitmapFont fpsFont = Assets.instance.fonts.defaultFont;
		fpsFont.getData().setScale(Constants.factorEscala());
		fpsFont.draw(batch, levelInfo.levelTitle, cameraGUI.viewportWidth*1/20 , cameraGUI.viewportHeight*1/10); 
		fpsFont.draw(batch, trial.jsonTrial.title , cameraGUI.viewportWidth*2/5 , cameraGUI.viewportHeight*1/10);
		Assets.instance.fonts.defaultFont.draw(batch, trial.jsonTrial.caption , cameraGUI.viewportWidth*1/5 , cameraGUI.viewportHeight*9/10);
		//Assets.instance.fonts.defaultFont.draw(batch, "Convergencia cuandrante 1: "+ this.levelInfo.levelLog.analisis.convergencia(0), cameraGUI.viewportWidth*3/5, cameraGUI.viewportHeight*16/20);
		//Assets.instance.fonts.defaultFont.draw(batch, "Convergencia cuandrante 2: "+ this.levelInfo.levelLog.analisis.convergencia(1), cameraGUI.viewportWidth*3/5, cameraGUI.viewportHeight*17/20);
		//Assets.instance.fonts.defaultFont.draw(batch, "Convergencia cuandrante 3: "+ this.levelInfo.levelLog.analisis.convergencia(2), cameraGUI.viewportWidth*3/5, cameraGUI.viewportHeight*18/20);
		//Assets.instance.fonts.defaultFont.draw(batch, "Convergencia cuandrante 4: "+ this.levelInfo.levelLog.analisis.convergencia(3), cameraGUI.viewportWidth*3/5, cameraGUI.viewportHeight*19/20);
		Assets.instance.fonts.defaultFont.draw(batch, "Numero de trials maximos restantes: " + this.levelInfo.levelLog.analisis.trialsRestantes(), cameraGUI.viewportWidth*2/5, cameraGUI.viewportHeight*19/20);
		// Assets.instance.fonts.defaultFont.draw(batch, String.valueOf(trial.jsonTrial.parametros.D), cameraGUI.viewportWidth*1/5, cameraGUI.viewportHeight*1/10);
		// Assets.instance.fonts.defaultFont.draw(batch, "Trial #" + trialNumber + " Id: " + trial.Id+" de " + levelInfo.secuenciaTrailsId.size, cameraGUI.viewportWidth*7/10, cameraGUI.viewportHeight*1/10); 
	}
}
