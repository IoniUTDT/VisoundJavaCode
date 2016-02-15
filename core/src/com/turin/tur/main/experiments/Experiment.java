package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.experiments.Experiments.LevelStatus;

public interface Experiment {
	// Cosas offline
	void makeResources(); // Se encarga de crear los recuros
	void makeLevels(); // Se encarga de armar la estructura de niveles
	void exportLevels(); // Se encarga de procesar los recursos y mandarlos al lugar correspondiente en la carpeta assets
	// Cosas online de control de flujo de informacion
	Trial askTrial(); // Devuelve el proximo trial a usar
	void returnAnswer (boolean answer); // Le indica al experimento como salio el trial
	void initGame (); // Se ejecuta cuando se inicial el juego (la idea es que aca se inicialicen todas las variables generales)
	void initLevel(int id); // Se ejecuta cuando se inicial un nivel especifico (la idea es que aca se inicialicen todas las variables dependientes del nivel)
	void stopLevel(); // Se ejecuta cuando se detiene un nivel (la idea es que aca se generen todos los logs y se envien al servidor)
	// Cosas de interfaz general
	Array<LevelStatus> levelsStatus(); // Pasa la info para que la pantalla menu pueda armarse como corresponde
}