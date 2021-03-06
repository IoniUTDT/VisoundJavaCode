package com.turin.tur.main.diseno;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;

public class MedidorDeConfianza {

	public static final String TAG = MedidorDeConfianza.class.getName();
	public Vector2 posicionCenter = new Vector2(); // Esta es la posicion de la caja dada por las coordenadas de su centro. 
	public Sprite spr; // Guarda la imagen que se va a mostrar (se genera a partir del contenido de la caja)
	public boolean visible;
	
	public void render(SpriteBatch batch) {
		if (this.visible) {
			// Render the main content of the box
			float x;
			float y;
			// Find the position of the main imagen and setup it
			spr.setSize(Constants.ConfianzaConstants.ANCHO, Constants.ConfianzaConstants.ALTO);
			x = posicionCenter.x - Constants.ConfianzaConstants.ANCHO / 2;
			y = posicionCenter.y - Constants.ConfianzaConstants.ALTO / 2;
			spr.setPosition(x, y);
			spr.draw(batch);
		}
	}
	
	public void SetPosition(float xCenter, float yCenter) {
		this.posicionCenter.x = xCenter;
		this.posicionCenter.y = yCenter;
	}

	public MedidorDeConfianza() {
		this.visible = false;
		this.spr = new Sprite (Assets.imagenes.rectaConfianza);
	}
}
