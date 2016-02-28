package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.experiments.Experiments.LevelStatus;

public interface Experiment {
	// Cosas offline
	String getName();
	void makeResources(); // Se encarga de crear los recuros
	void makeLevels(); // Se encarga de armar la estructura de niveles
	// Cosas online de control de flujo de informacion
	Trial getTrial(); // Devuelve el proximo trial a usar
	void askNext();
	void returnAnswer (boolean answer); // Le indica al experimento como salio el trial
	void initGame (Session session); // Se ejecuta cuando se inicial el juego (la idea es que aca se inicialicen todas las variables generales.
	void initLevel(Level level); // Se ejecuta cuando se inicial un nivel especifico (la idea es que aca se inicialicen todas las variables dependientes del nivel)
	void stopLevel(); // Se ejecuta cuando se detiene un nivel (la idea es que aca se generen todos los logs y se envien al servidor)
	// Cosas de interfaz general
	Array<LevelStatus> levelsStatus(); // Pasa la info para que la pantalla menu pueda armarse como corresponde
	boolean askCompleted(); // Indica si se completo el nivel o no.
	void interrupt(); // Sirve para ejecutar acciones cuando se interrumpe el experimento porque el usuario decide salir del nivel
	int trialsLeft(); // Indica cuantos trials quedan como maximo (sievr para mostrar indicadores en la interfaz
}