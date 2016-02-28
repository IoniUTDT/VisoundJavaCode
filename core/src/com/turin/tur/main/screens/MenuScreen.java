package com.turin.tur.main.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.Visound;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;


public class MenuScreen extends AbstractGameScreen {

	private static final String TAG = MenuScreen.class.getName();

	// For debug drawing
	private ShapeRenderer shapeRenderer;

	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
	private Array<TextButton> levelButtons = new Array<TextButton>();

	// Variables para funcionamiento interno
	int levelIterator;

	public SpriteBatch batch;
	public OrthographicCamera cameraGUI;


	public MenuScreen(Visound game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();
		table.drawDebug(shapeRenderer); // This is optional, but enables debug
										// lines for tables.
		guiRender();
		
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderServerStatus();
		batch.end();
	}

	private void renderServerStatus() {
		float x = cameraGUI.viewportWidth - cameraGUI.viewportWidth*1/10;
		float y = cameraGUI.viewportHeight - cameraGUI.viewportHeight*1/20;
		BitmapFont fpsFont = this.assets.fonts.defaultFont;
		fpsFont.getData().setScale(Constants.factorEscala());
		if (Internet.serverOk) {
			// show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else {
			// show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "Server", x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
		
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
		guiRenderInit();
		// Crea las cosas que tienen que ver con los graficos.
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		shapeRenderer = new ShapeRenderer();
		skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
		
		// Crea los botones de los niveles
		Array<LevelStatus> levels = new Array<LevelStatus>();
		for (Experiment exp : this.game.exps) {
			levels.addAll(exp.levelsStatus());
		}

		for (final LevelStatus level : levels) {
			TextButton button = new TextButton(level.publicName, skin, "default");
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new LevelScreen(game,level.id, level.expName));
				}
			});
			if (level.alreadyPlayed) {
				button.setColor(0, 1, 0, 1); // Green
			} else {
				//	button.setColor(1, 1, 0, 1); //Yellow
				button.setColor(1, 0, 0, 1); //Red
			}
			levelButtons.add(button);
			//Gdx.app.debug(TAG, "agregado boton" + button.getText());
		}
		
		// Arma el menu
		int n=0;
		for (TextButton button : levelButtons) {
			button.getStyle().font.getData().setScale(Constants.factorEscala()*3,Constants.factorEscala()*3);
			table.add(button).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f);
			n=n+1;
			if (n%3 == 0) {
				table.row();
			}
		}

		Gdx.app.debug(TAG, "Menu cargado");

	}

	@Override
	public void hide() {
		stage.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}
}
