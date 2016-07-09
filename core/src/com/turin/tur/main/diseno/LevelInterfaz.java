package com.turin.tur.main.diseno;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.util.Assets;



public class LevelInterfaz {
	
	private static final String TAG = LevelInterfaz.class.getName();
	// private LevelOLD levelInfo;   
	private Trial trial;
	private Level levelActivo;
	// private Experiment exp;
	
	public LevelInterfaz (Level levelActivo, Trial trial){
		// this.exp = exp;
		this.trial = trial;
		this.levelActivo = levelActivo;
		// this.levelInfo = levelInfo;		
	}
	
	/*
	public void renderFps(SpriteBatch batch, OrthographicCamera cameraGUI) {
		float x = cameraGUI.viewportWidth - cameraGUI.viewportWidth*1/10;
		float y = cameraGUI.viewportHeight - cameraGUI.viewportHeight*1/20;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.fonts.defaultFont;
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
	*/
	
	public void renderTitle (SpriteBatch batch, OrthographicCamera cameraGUI) {
		
		
		BitmapFont font = Assets.fonts.defaultFont;
		//font.getData().setScale(Constants.factorEscala());
		//font.draw(batch, levelInfo.levelTitle, cameraGUI.viewportWidth*1/20 , cameraGUI.viewportHeight*1/10); 
	//	font.draw(batch, trial.jsonTrial.title , cameraGUI.viewportWidth*2/5 , cameraGUI.viewportHeight*1/10);
		font.draw(batch, trial.jsonTrial.title, cameraGUI.viewportWidth/2, cameraGUI.viewportHeight*1/10, 10, 1, false);
		// Assets.fonts.defaultFont.draw(batch, trial.jsonTrial.caption , cameraGUI.viewportWidth*1/5 , cameraGUI.viewportHeight*9/10);
		font.draw(batch, "Trials restantes: " + this.levelActivo.trialsLeft(), cameraGUI.viewportWidth/2, cameraGUI.viewportHeight*9/10, 10, 1, false);
	}
}
