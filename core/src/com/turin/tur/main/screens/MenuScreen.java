package com.turin.tur.main.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.Visound;
import com.turin.tur.main.diseno.Session.FASEdeEXPERIMENTO;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.main.experiments.Experiments.LevelStatus;
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.LevelInfo;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;


public class MenuScreen extends AbstractGameScreen implements InputProcessor{

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

		// Gdx.input.setInputProcessor(this);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();
		table.drawDebug(shapeRenderer); // This is optional, but enables debug
										// lines for tables.
		guiRender();
		
		// Verificamos el estatus de envio de datos
		FileHandle[] files = Gdx.files.local(Internet.pathSending).list();
		if (files.length>1) { // Hay que considerar que esta la carpeta tags. Si se mejora y esa carpeta vuela entonces hayq ue cambiar la comparacion a 0
			game.sendingData = true;
		} else {
			game.sendingData = false;
		}
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderServerStatus();
		batch.end();
	}

	private void renderServerStatus() {
		BitmapFont fpsFont = this.assets.fonts.defaultFont;
		fpsFont.getData().setScale(Constants.factorEscala());
		if (Internet.serverOk) {
			// show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else {
			// show up in red
			fpsFont.setColor(1, 0, 0, 1);
			fpsFont.draw(batch, "Servidor offline", cameraGUI.viewportWidth*1/5, cameraGUI.viewportHeight - cameraGUI.viewportHeight*1/20);
		}
		if (game.sendingData) {
			fpsFont.draw(batch, "Enviando datos...", cameraGUI.viewportWidth - cameraGUI.viewportWidth*1/5, cameraGUI.viewportHeight - cameraGUI.viewportHeight*1/20);
		}
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
		
		if (this.game.session.user.faseDeExperimento == FASEdeEXPERIMENTO.Intro) {
			// Creamos el boton de las instrucciones
			TextButton instrucciones = new TextButton("Instrucciones", skin, "default");
			instrucciones.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new InstructionsScreen(game));
				}
			});
			table.add(instrucciones).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f).colspan(3);
			table.row();

		}
		
		if (this.game.session.user.faseDeExperimento != FASEdeEXPERIMENTO.ExperimentoCompleto) {
			TextButton Siguiente = new TextButton("Comenzar", skin, "default");
			Siguiente.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.session.user.pasarFase();
				}
			});
			table.add(Siguiente).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f).colspan(3);
			table.row();
		}
		
		// Crea los botones de los niveles
/*		
		// Cargamos los niveles
		Array<LevelInfo> levels = new Array<LevelInfo>();
		for (Level level : this.game.levelList) {
			levels.addAll(level.levelInfo);
		}
		
		// Los ordenamos segun prioridad
		levels.sort();
		
		// Seleccionamos solo los no jugados
		Array<LevelStatus> levelsToPlay = new Array<LevelStatus>();
		for (LevelStatus level : levels) {
			if (!level.alreadyPlayed) {levelsToPlay.add(level);}
		}
		levelsToPlay.sort();
		
		// Buscamos que nivel de prioridad es la primera no jugada
		int priorityGoal = 100; // Asumimos que 100 nunca va a ser una prioridad de un nivel en juego
		if (levelsToPlay.size != 0) {
			priorityGoal = levelsToPlay.first().priority;
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
				button.setColor(0, 1, 0, 0.5f); // Green
			} else {
				//	button.setColor(1, 1, 0, 1); //Yellow
				button.setColor(1, 0, 0, 0.5f); //Red
			}
			
			if (level.priority == priorityGoal) { // Significa que esta en el nivel de opciones a jugar
				if (level.alreadyPlayed) {
					button.setColor(0, 1, 0, 1); // Green
				} else {
					button.setColor(1, 0, 0, 1); //Red
				}	
			} else {
				if (level.priority > priorityGoal) {
					button.setTouchable(Touchable.disabled);
				}
			}
			levelButtons.add(button);
			//Gdx.app.debug(TAG, "agregado boton" + button.getText());
		}
		
		// Arma el menu
		int n=0;
		for (TextButton button : levelButtons) {
			//button.getStyle().font.getData().setScale(Constants.factorEscala()*3,Constants.factorEscala()*3);
			table.add(button).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f);
			n=n+1;
			if (n%3 == 0) {
				table.row();
			}
		}
*/
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

	@Override
	public boolean keyDown(int keycode) {
		// Back to Menu
		if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
			if (!game.sendingData) {
				Gdx.app.exit();
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
