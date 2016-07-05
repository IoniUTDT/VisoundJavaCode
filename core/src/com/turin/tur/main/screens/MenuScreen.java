package com.turin.tur.main.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
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
import com.turin.tur.main.levelsDesign.Level;
import com.turin.tur.main.levelsDesign.Level.LISTAdeNIVELES;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;


public class MenuScreen extends AbstractGameScreen implements InputProcessor{

	private static final String TAG = MenuScreen.class.getName();

	public SpriteBatch batch;

	public OrthographicCamera cameraGUI;
	private Array<TextButton> FaseButtons = new Array<TextButton>();
	// For debug drawing
	private ShapeRenderer shapeRenderer;
	// Elementos graficos
	private Skin skin;

	private Stage stage;

	private Table table;
	// Variables para funcionamiento interno
	int levelIterator;


	public MenuScreen(Visound game) {
		super(game);
	}

	@Override
	public void hide() {
		stage.dispose();
		shapeRenderer.dispose();
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
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
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

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
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
		
		if (this.game.session.user.faseDeExperimentoActiva == FASEdeEXPERIMENTO.Intro) {
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

			TextButton siguiente = new TextButton("Comenzar", skin, "default");
			siguiente.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.session.user.pasarFase();
					game.setScreen(new MenuScreen(game));
				}
			});
			table.add(siguiente).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f).colspan(3);
			table.row();
		}
		
		final LISTAdeNIVELES nextLevel = game.session.user.nextLevelToPlay();
		for (final LISTAdeNIVELES nivel : game.session.user.faseDeExperimentoActiva.niveles) {
			TextButton button = new TextButton(nivel.toString(), skin, "default");
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.levelActivo = Level.createLevel(nivel);
					game.setScreen(new LevelScreen(game));
				}
			});
			
			if (game.session.user.alreadyPlayed(nivel)) {
				button.setColor(0, 1, 0, 0.5f); // Green
				if (!Visound.mododesarrollo) { button.setTouchable(Touchable.disabled); }
			} else {
				if (nextLevel == nivel) {
					button.setColor(0, 1, 0, 1); // Green
				} else { 
					button.setColor(1, 0, 0, 0.5f); // Green
					if (!Visound.mododesarrollo) {button.setTouchable(Touchable.disabled);}
				}
			}
			
			FaseButtons.add(button);
		}
		
		if (Visound.mododesarrollo) {
			TextButton siguiente = new TextButton("Comenzar", skin, "default");
			siguiente.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.session.user.pasarFase();
					game.setScreen(new MenuScreen(game));
				}
			});
			FaseButtons.add(siguiente);
		}
		
		
		// Arma el menu
		int n=0;
		for (TextButton button : FaseButtons) {
			//button.getStyle().font.getData().setScale(Constants.factorEscala()*3,Constants.factorEscala()*3);
			table.add(button).width(Gdx.graphics.getWidth()/5f).space(Gdx.graphics.getHeight()/30f);
			n=n+1;
			if (n%3 == 0) {
				table.row();
			}
		}
		//Gdx.app.debug(TAG, "Menu cargado");
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderServerStatus();
		batch.end();
	}

	private void guiRenderInit() {
		batch = new SpriteBatch();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
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
}
