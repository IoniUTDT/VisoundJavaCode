package com.turin.tur.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.turin.tur.Visound;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;

public class InstructionsScreen extends AbstractGameScreen {

	private static final String TAG = InstructionsScreen.class.getName();
	
	private SpriteBatch batch;
	private OrthographicCamera cameraGUI;
	
	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
	
	public InstructionsScreen(Visound game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();
		textRender();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	private void textRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		Assets.fonts.defaultFont.draw(batch, "La tecnologia Visound consiste en una tecnologia de sustitucion sensorial. Es decir transforma estimulos visuales en estimulos sonoros \n "
				+ "Esto sirve para que personas ciegas o con limitaciones visuales puedan a traves del sonido reconocer elementos de su entorno. "
				+ "Con el entrenamiento adecuado las personas (videntes o no) pueden costumbrarse al uso de esta tecnologia y reconocer patrones y formas. "
				 + "En este experimento pretendemos estudiar algunos aspectos geometricos de esta percepcion" , cameraGUI.viewportWidth/20, cameraGUI.viewportHeight/10);
				
		Assets.fonts.defaultFont.draw(batch, "- El programa cuenta con tres niveles introductorios (tutoriales) para comprender la dinamica del experimento \n y seis niveles donde se realiza la medición", cameraGUI.viewportWidth/20, cameraGUI.viewportHeight/10);
		Assets.fonts.defaultFont.draw(batch, "- Antes de realizar el experimento se deben completar los tutoriales", cameraGUI.viewportWidth/20, cameraGUI.viewportHeight/10*2);
		Assets.fonts.defaultFont.draw(batch, "- El tutorial 1 sirve para ", cameraGUI.viewportWidth/20, cameraGUI.viewportHeight/10*3);
		batch.end();
	}
	
	@Override
	public void show() {
		// Crea las cosas que tienen que ver con los graficos.
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));

		TextButton button = new TextButton("Continuar", skin, "default");
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MenuScreen(game));
			}
		});
		
		table.add(button).width(Gdx.graphics.getWidth()/2.5f).space(Gdx.graphics.getHeight()/10f).padBottom(Gdx.graphics.getHeight()/10f);
		table.row();
		table.align(Align.bottom);
		
		guiRenderInit();
	}

	
	private void guiRenderInit() {
		batch = new SpriteBatch();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}
	
	@Override
	public void hide() {
		stage.dispose();
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}


}
