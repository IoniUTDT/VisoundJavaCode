package com.turin.tur.main.util.builder;

import java.io.File;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.experiments.Experiments.CategoriaAngulo;
import com.turin.tur.main.experiments.Experiments.SetupUmbralAngulos;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.Imagenes.Linea;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;

public class ResourcesMaker {

	private static final String TAG = ResourcesMaker.class.getName();
	// public static int contadorDeRecursos = Constants.Resources.Reservados;
	// public static int contadorDeReferenciasUmbral = 0;
	
	public ResourcesMaker() {
		this.verifyResourcesVersion();
		Textos.crearTextos();
		if (Builder.AppVersion == "UmbralCompletoAngulos") {
			SetupUmbralAngulos setup = new SetupUmbralAngulos();
			setup.nombre="SetupAngulosUmbral";
			setup.saltoChico=1;
			setup.saltoGrande=5;
			setup.angulosCriticos.add(0);
			setup.angulosCriticos.add(90);
			setup.angulosCriticos.add(180);
			setup.angulosCriticos.add(270);
			setup.searchAngles(); // Hace que el setup busque todos los angulos
			this.constructUmbralAngulos(setup);
		}
		if (Builder.AppVersion=="Prueba") {
			Imagenes imagen = new Imagenes();
			Linea infoLinea = imagen.new Linea();
			infoLinea.radial.angulo=30;
			infoLinea.radial.largo=50;
			infoLinea.radial.Xcenter = 70;
			infoLinea.radial.Ycenter = 70;
			infoLinea.lineaFromRadial();
			//infoLinea.x1=50;
			//infoLinea.y1=50;
			//infoLinea.x2=75;
			//infoLinea.y2=75;
			imagen.lineas.add(infoLinea);
			imagen.toSVG();
		}
	}
	
	
	/**
	 * Funcion que se encarga de construir todos los recursos asociados un experimento de umbral de angulos
	 */
	private void constructUmbralAngulos (SetupUmbralAngulos setup){
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width>Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		// Hacemos dos indices que recorran todos los angulos de manera que el segundo indice solo llegue hasta el primero para evitar duplicar los recursos
		for (int indice1 = 0; indice1<setup.angulos.size ; indice1++) {
			for (int indice2 = indice1 +1; indice2<setup.angulos.size; indice2++) {
				int angulo1 = setup.angulos.get(indice1);
				int angulo2 = setup.angulos.get(indice2);
				if (setup.cumpleCriterioDistanciaMinima(angulo1, angulo2)) {
					int deltaAngulo = angulo2-angulo1;
					if (deltaAngulo < 0) {deltaAngulo=-deltaAngulo;}  // Hacemos que sean todos los numeros positivos
					if (deltaAngulo >= 180) {deltaAngulo = 360 - deltaAngulo;} // Hacemos que los angulos sean considerados siempre del lado "concavo")

					// Creamos la imagen correspondiente
					Imagenes imagen = new Imagenes();
					
					float Xcenter = Resources.Display.width/2;
					float Ycenter = Resources.Display.height/2;
					imagen.infoConceptualAngulos.direccionLado1 = angulo1;
					imagen.infoConceptualAngulos.direccionLado2 = angulo2;
					
					// Agregamos al setup que el objeto creado tiene los angulos dados para facilitar la busqueda posterior
					int indice = setup.angulos.indexOf(angulo1, false);
					setup.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);
					indice = setup.angulos.indexOf(angulo2, false);
					setup.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);

					// Clasificamos el angulo segun sea agudo recto o grave
					imagen.infoConceptualAngulos.separacionAngular = deltaAngulo;
					if (deltaAngulo < 90) {
						imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Agudo;
						imagen.categories.add(Categorias.Agudo);
					} else {
						if (deltaAngulo > 90) {
							imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Grave;
							imagen.categories.add(Categorias.Grave);
						} else {
							imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Recto;
							imagen.categories.add(Categorias.Recto);
						}
					}
				
					// agrega la primer linea (notas de sistema de coordenadas:
					// El SVG considera el y positivo hacia abajo, pero eso se compensa al crear el archivo. Todo el codigo considera
					// El eje x positivo hacia la derecha y el y positivo hacia arriba
					Linea infoLinea = new Linea();
					infoLinea.radial.angulo=angulo1;
					infoLinea.radial.largo=tamano/2;
					infoLinea.radial.Xcenter = (float) (Xcenter + tamano/4 * MathUtils.cosDeg(angulo1));
					infoLinea.radial.Ycenter = (float) (Ycenter + tamano/4 * MathUtils.sinDeg(angulo1));
					infoLinea.lineaFromRadial();
					imagen.lineas.add(infoLinea);
					// agrega la segunda linea
					infoLinea = new Linea();
					infoLinea.radial.angulo=angulo2;
					infoLinea.radial.largo=tamano/2;
					infoLinea.radial.Xcenter = (float) (Xcenter + tamano/4 * MathUtils.cosDeg(angulo2));
					infoLinea.radial.Ycenter = (float) (Ycenter + tamano/4 * MathUtils.sinDeg(angulo2));
					infoLinea.lineaFromRadial();
					imagen.lineas.addAll(infoLinea);
		
					// agregamos la info a la imagen
					imagen.comments = "Imagen generada por secuencia automatica 'recursosAnguloAnalisisUmbral'.";
					imagen.name = "Imagen de angulos generada automaticamente";
					imagen.idVinculo = "";
					imagen.categories.add(Categorias.Angulo);
					
					imagen.toSVG();
					// objetos.add(imagen); // aca hay que hacer que cree la imagen
				}
			}
		}
		// Guardamos el setup 
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetupUmbralAngulos.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(setup));
	}
	
	/**
	 * Funcion que verifica que no haya recursos creados con la misma version que la que se quiere crear
	 */
	private void verifyResourcesVersion() {
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(Resources.Paths.fullCurrentVersionPath);
		if (file.exists()) {
			System.out.println("Modifique la version de los recursos porque ya existe una carpeta con la version actual");
			System.exit(0);
		}
	}
	
	
	
	

	

}
