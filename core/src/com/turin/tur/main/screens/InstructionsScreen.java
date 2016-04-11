package com.turin.tur.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	
	private String completeText= "        Visound es una tecnología de sustitución sensorial que transforma imágenes en sonidos. Esta "
			+ "tecnología permite que personas ciegas aprendan a reconocer elementos visuales de su entorno a través de los sonidos. \n \n"
			+ "        Con esta aplicación queremos estudiar como se perciben ciertos aspectos geométricos" 
			+ " al transformar las imagenes en sonidos. \n \n "
			+ "        Para transformar imágenes en sonidos se utiliza la siguiente lógica: \n \n"
			+ " - Para distinguir izquierda de derecha se realiza un barrido de manera que primero suenan los elementos más a la izquierda"
			+ " y luego los ubicados hacia la derecha hasta recorrer toda la imagen. \n \n"
			+ " - Para distinguir arriba de abajo se utiliza la frecuencia del sonido, de manera que un elemento alto en la imagen suene"
			+ " agudo y uno bajo suene grave. Algo ubicado en el medio de la imagen sonara en un tono medio.\n \n"
			+ "        El programa cuenta con tres niveles tutoriales que se deben completar antes de comenzar los niveles avanzados. El"
			+ " primer tutorial permite escuchar el sonido asociado a diferentes imágenes para familiarizarse con el mecanismo de sustitución"
			+ " sensorial. Los tutoriales 2 y 3 representar ejemplos sencillos de los niveles siguientes. En estos niveles se reproducira un"
			+ " sonido (sin mostrar la imagen a la que corresponde)"
			+ " y se hara una pregunta respecto a dicha imagen. La pregunta puede ser cuan grande es un angulo (si es agudo, menos de 90º, recto,"
			+ " exactamente 90º, u obtuso, entre 90 y 180), o bien en que direccion se juntan dos segmentos. En los tutoriales se indicara con"
			+ " color verde o rojo si la respues elegida fue la correcta. En los niveles siguientes esto no sucederá. \n \n"
			+ "        Es importante remarcar que la dificultad de las preguntas varía durante el experimento por lo que es esperable que haya "
			+ " preguntas fáciles, preguntas difíciles y preguntas muy difíciles de responder. En un experimento de estas características no hay"
			+ " respuestas buenas o malas, porque presisamente lo que se busca medir es cuan bueno es uno en la tarea de percibir las diferencias"
			+ " y todas las respuestas aportan información.";

	
	
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
		
		float x = cameraGUI.viewportWidth/20;
		float y = cameraGUI.viewportHeight/20;
		float ancho = cameraGUI.viewportWidth/20*18;
			
		Assets.fonts.defaultFont.draw(batch, completeText, x, y, ancho, Align.left, true);
				
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
		
		table.add(button).width(Gdx.graphics.getWidth()/2.5f).space(Gdx.graphics.getHeight()/10f).padBottom(Gdx.graphics.getHeight()/20f);
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
