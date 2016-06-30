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
import com.turin.tur.main.diseno.LevelOLD;
import com.turin.tur.main.util.Constants;

public class ResultsScreen extends AbstractGameScreen {

	private static final String TAG = ResultsScreen.class.getName();

	private LevelOLD level;
	
	private SpriteBatch batch;
	private OrthographicCamera cameraGUI;
	
	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
		
	public ResultsScreen(Visound game) {
		super(game);
		this.level = level;
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();

		// guiRender();
		
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// renderPage();
		batch.end();
	}

	private void renderPage() {
		// Muestra el contenido en funcion de la pagina
		float x = cameraGUI.viewportWidth/10;
		float y = cameraGUI.viewportHeight/8;
		BitmapFont fpsFont = this.assets.fonts.defaultFont;
		fpsFont.getData().setScale(Constants.factorEscala()/2);
		fpsFont.draw(batch, "Ha completado el nivel.", x, y);
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
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
		
		// Arma el menu

		//button.getStyle().font.getData().setScale(Constants.factorEscala()*3,Constants.factorEscala()*3);
		table.add(button).width(Gdx.graphics.getWidth()/2.5f).space(Gdx.graphics.getHeight()/10f);
		table.row();
		table.align(Align.center);
		//table.row();


		guiRenderInit();
		// Gdx.app.debug(TAG, "Resultados cargados");

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
