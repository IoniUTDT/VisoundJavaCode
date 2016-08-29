package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.turin.tur.main.levelsDesign.Level.LISTAdeRECURSOS;
import com.turin.tur.main.util.builder.Builder;

public class Constants {

	// Version of game; Es importante porque dentro de una version (notada por
	// la parte entera) se respeta compatibilidad de todos los identificadores.
	// al cambiar la version no se cargan los datos viejo ni las estructuras
	// viejas
	public static final float VERSION = 1.1f;
	public static final int CODEVERSION = 42;

	// Version of game for internal use
	public static int version() {
		int version_temp = MathUtils.roundPositive(Constants.VERSION);
		int temp;
		if (version_temp > Constants.VERSION) {
			temp = -1;
		} else {
			temp = 0; 
		}
		return version_temp + temp;
	}

	// recupera la escala para mostrar todos los textos con el tamaño correcto
	public static float factorEscala () {
		float factorEscala;
		// Escala de referencia
		float anchoDefault = 600;
		float altoDefault = 300;
		factorEscala = Gdx.graphics.getWidth()/anchoDefault*1/3;
		return factorEscala;
	}
	
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 7.0f;

	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;

	// Archivo con imagenes del juego
	public static final String TEXTURE_ATLAS_OBJECTS = "images/cajas.pack.atlas";
	// public static final String TEXTURE_ATLAS_IMAGES_EXP = "images/imagesexp.pack.atlas";

	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI = "skins/uiskin.json";

	// Localizacion de archivos
	public static String resourcesPath = "experimentalsource" + "/" + version() + "/";

	// ESTO no parece hacer nada!
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 100.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 80.0f;

	// Constantes relacionadas con el diseno de experimentos

	public static class Diseno {

		/*
		 * Hay dos tipo de trial: Entrenamiento, Test
		 * 
		 * En el primero de los casos se muestra un conjunto de estimulos experimentales y se los puede reproducir para escuchar como suenan. En principio el
		 * trial se supera cuando se tocaron todos los estimulos En el caso del Test, se muestra un estimulo especifico y el usuario debe elegir entre
		 * diferentes opciones, que pueden ser imagenes especificas o imagenes que representen una categoria
		 */
		public enum TIPOdeTRIAL {
			ENTRENAMIENTO, // Este tipo de trial sirve para mostrar imagenes y cual es su sonido 
			TEST // Este tipo de test sirve para mostrar un estimulo y pedir al usuario que elija (con o sin feedback)
		}

		public enum TIPOdeLEVEL {
			UMBRALPARALELISMO,UMBRALANGULO
		}
		
		public enum DISTRIBUCIONESenPANTALLA {
			LINEALx3(new float[][] { { -1.5f, 0 }, { 0, 0 }, { +1.5f, 0 } }),
			LINEALx2(new float[][] { { -1.5f, 0 }, { +1.5f, 0 } }),
			BILINEALx6(new float[][] { { -1.5f, 1 }, { 0, 1 }, { 1.5f, 1 }, { -1.5f, -1 }, { 0, -1 }, { 1.5f, -1 } }),
			BILINEALx4(new float[][] { { -1, 1 }, { 1, 1 }, { -1, -1 }, { 1, -1 } }),
			BILINEALx2(new float[][] { { 0f, 1f }, { 0f, -1f } }),
			LINEALx1(new float[][] { { 0, 0 } }),
			BILINEALx7(new float[][]{ { -1.5f, 1 }, { 0, 1 }, { 1.5f, 1 }, { -1.5f, -1 }, { 0, -1 }, { 1.5f, -1 }, {3f, 0f} });

			public float[][] distribucion;

			DISTRIBUCIONESenPANTALLA(final float[][] distribucion) {
				this.distribucion = distribucion;
			}

			public float X(int i) {
				return this.distribucion[i][0];
			}

			public float Y(int i) {
				return this.distribucion[i][1];
			}
		}

	}

	// Constantes relacionadas con las cajas
	public class Box {
		public static final float TAMANO = 1; // tama�o de la caja
		public static final float TAMANO_ESTIMULO = 2; // tama�o de la caja
		public static final float DURACION_REPRODUCCION_PREDETERMINADA = 5; // medida
																			// en
																			// segundos
		public static final float TAMANO_CONTORNO_X = 0.02f;
		public static final float TAMANO_CONTORNO_Y = 0.1f;
		public static final float SHIFT_MODO_SELECCIONAR = 1.5f;
		public static final float SHIFT_ESTIMULO_MODO_SELECCIONAR = -2.5f;
		public static final float DELAY_ESTIMULO_MODO_SELECCIONAR = 2f;
		public static final float ANIMATION_ANSWER_TIME = 0.1f;
		public static final float SELECT_BOX_ANCHO_RTA = 0.1f;
	}

	public class ConfianzaConstants {
		public static final float ALTO = 0.3f;
		public static final float ANCHO = 2;
	}
	
	public static final String SPANISH_FONT_NAME = "fonts/goodfish rg.ttf";
	
	// Constantes relacionadas a IDs de cosas fijas
	public static class ResourcesCategorias {

		public enum CategoriasImagenes {
			Nada(0,""), 
			Texto(1), 
			Imagen(2),
			Ejemplos(3),
			Siguiente(4),
			
			Lineax1(6,"Una Linea"), 
			Lineax2(7, "Dos Lineas"), 
			Paralelas(8),
			NoParalelas(9, "No Paralelas"),
			Angulo(10),
			Agudo(11, "Ángulo agudo"),
			Obtuso(12, "Ángulo obtuso"),
			Recto(13, "Ángulo recto"),
			Lineas(14),  
			NoAngulo(15,"No Ángulo"),
			Cuadrilatero(16),
			Cuadrado(17),
			Rombo(18),
			Rotado(19),
			SinRotar(20),
			Converge (21),
			Diverge (22),
			NoRecto (23, "Ángulo No Recto")
			;

			/*
			 * Son las categorias con las que se puede taguear a los recursos experimentales.
			 */
			public int ID; // El id de la categoria que se corresponde con el del recurso que la nombra
			public String nombre; // Nombre de la categoria (igual a su nombre, pero incluido para que sea explicito en los Jsons)
			public String texto; // Texto que se muestra en el cuadro. A priori es igual al nombre salvo que se especifique otra cosa

			private CategoriasImagenes(int Id) {
				this.ID = Id;
				this.nombre = this.name();
				this.texto = this.name();
			}
			
			private CategoriasImagenes(int Id, String texto) {
				this.ID = Id;
				this.nombre = this.name();
				this.texto = texto;
			}
		}

		public static class Display {
			public static int height = 100;
			public static int width = 100;
		}
		
		public static class Paths {
			
			// Carpetas para el builder			
			public static String ResourcesBuilder = "./resourcesbuild/" + Builder.ResourceVersion + "/";
			public static String LevelsBackUp = "./resourcesbuild/levelsOlds/";
			public static String finalInternalPath = "../android/assets/resources/";
			public static String finalInternalPathSettings = "../android/assets/Settings/";
			public static String processingTempFolder = "./resourcesbuild/temp/";
			// Carpetas para el tiempo de ejecucion
			public static String InternalResources = "resources/";
			public static String LocalSettingsCopy = "settings/";
			
			public static String ExtraFldr = "extra/";
			public static String ResourcesSetupExt = ".SetupRes";
//			public static String LevelSetupExt = ".SetupLvl";
			
			public static String SetupResourcesPath (LISTAdeRECURSOS identificador) {
				return ResourcesBuilder + ExtraFldr + identificador.toString() + ResourcesSetupExt;
			}			  
		}

		public static final int NumeroDeRecursosReservados = ResourcesCategorias.CategoriasImagenes.values().length;
	}
}
