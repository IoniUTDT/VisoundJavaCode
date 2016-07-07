package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.Visound;

public class InternetNuevo {

	public static final String filename = "Internet";
	public static final String fileExt = ".data";
	public static final String path = Visound.pathLogs + "/" + filename + fileExt;
	static long instance = TimeUtils.millis();
	public static final String pathBackup = Visound.pathLogs + "/" + filename + instance + fileExt;
	public static final String serverBackUP = "http://172.18.19.6:3000/";
	public static final String serverMAIN = "http://turintur.dynu.com/";
	private static final String TAG = Internet.class.getName();

	
	private int serverStatus=0;
	private String server=serverMAIN;
	private ESTADOInternet estadoInternet = ESTADOInternet.SINConexion;
	Array<Enviable> CadenaEnvios = new Array<Enviable>();
	
	public InternetNuevo() {
		loadSavedData();
		checkConectividad ();
	}
	
	public void update() {
		if (estadoInternet == ESTADOInternet.ENVIOSPendientes){
			estadoInternet = ESTADOInternet.PROCESANDOEnvios;
			makeEnvio();
		}
	}

	private void makeEnvio() {
		estadoInternet = ESTADOInternet.PROCESANDOEnvios;
		for (final Enviable envio : CadenaEnvios) {
			if (envio.estadoEnvio == ESTADOEnvio.ENVIAR) {
				envio.estadoEnvio = ESTADOEnvio.ENVIANDO;
				new Thread(new Runnable() {

					@Override
					public void run() {
						new Envio(envio);
					}
					
				}).start();
			}
		}
	}

	private void checkConectividad() {
		estadoInternet = ESTADOInternet.PROCESANDOEnvios;
		new Thread(new Runnable() {

			@Override
			public void run() {
				new InternetStatus();
			}
			
		}).start();
		
	}

	private void makeSend() {
		
	}

	private void loadSavedData() {
		FileHandle dataFile = Gdx.files.local(path);
		if (dataFile.exists()) {
			FileHandle backUp = Gdx.files.local(pathBackup);
			dataFile.copyTo(backUp);
			String savedData = FileHelper.readLocalFile(path);
			Json json = new Json();
			json.setUsePrototypes(false);
			Array<Enviable> logsLeidos =  json.fromJson(this.CadenaEnvios.getClass(), savedData);
			for (Enviable entrada : logsLeidos) {
				if (entrada.estadoEnvio == ESTADOEnvio.ENVIAR) {
					agregarEnvio (entrada.objeto, entrada.tipo);
				}
				if (entrada.estadoEnvio == ESTADOEnvio.ENVIANDO) {
					agregarEnvio (entrada.objeto, entrada.tipo);
				}
			}
		}
	}

	private void agregarEnvio(Object objeto, TIPOdeENVIO tipo) {
		Enviable envio = new Enviable();
		envio.objeto = objeto;
		envio.tipo = tipo;
		envio.estadoEnvio = ESTADOEnvio.ENVIAR;
		if (estadoInternet == ESTADOInternet.ESPERANDO) {
			estadoInternet = ESTADOInternet.ENVIOSPendientes;
		}
	}

	public static class Enviable {
		Object objeto;
		TIPOdeENVIO tipo;
		ESTADOEnvio estadoEnvio;
	}
	
	public enum ESTADOEnvio {
		ENVIAR,ENVIANDO,ENVIADO
	}
	
	public enum ESTADOInternet {
		PROCESANDOEnvios, SINConexion, ENVIOSPendientes, ESPERANDO   
	}
	
	public enum TIPOdeENVIO {
		
	}
	
	
	public class InternetStatus implements HttpResponseListener {   

        HttpRequest request;
        String url = server + "status/";
        
        public InternetStatus()
        {
            request = new HttpRequest();
            request.setMethod(Net.HttpMethods.GET); //or POST
            request.setContent(""); //you can put here some PUT/GET content
            request.setUrl(url);
            Gdx.net.sendHttpRequest(request, this);
        }

        @Override
        public void handleHttpResponse(HttpResponse httpResponse) 
        {
        	serverStatus = httpResponse.getStatus().getStatusCode();
        	if (serverStatus == 200) {
        		String servercontent = httpResponse.getResultAsString();
        		if (servercontent.contains("\"on\"")) {
        			estadoInternet = ESTADOInternet.ESPERANDO;
        		} else {
        			estadoInternet = ESTADOInternet.SINConexion;
        			Gdx.app.error(TAG, "El servidor esta marcado como apagado!");
        		}
        	} else {
        		if (server.equals(serverMAIN)) {
            		server = serverBackUP;
            		Gdx.app.debug(TAG, "Cambiando al servidor local");
            		new InternetStatus();
            	} else {
            		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " ERROR: " + serverStatus);
            		estadoInternet = ESTADOInternet.SINConexion;
            	}
        	}
        }

        @Override
        public void failed(Throwable t) 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		estadoInternet = ESTADOInternet.SINConexion;
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: failed");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }

        @Override
        public void cancelled() 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		estadoInternet = ESTADOInternet.SINConexion;
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: cancelled");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }
    }
	
	public class Envio implements HttpResponseListener {   

        HttpRequest request;
        String url = server + "Envio";
        
        public Envio(final Enviable envio)
        {
        	Json json = new Json();
			json.setOutputType(OutputType.json);
		    json.setUsePrototypes(false);
			String requestJson = json.toJson(envio);
			
			Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
			request.setContent(requestJson);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			request.setUrl(url);

        	
			Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

				@Override
				public void handleHttpResponse(Net.HttpResponse httpResponse) {
					int statusCode = httpResponse.getStatus().getStatusCode();
					if (statusCode != HttpStatus.SC_CREATED) {
						Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
						envio.estadoEnvio = ESTADOEnvio.ENVIAR;
						InternetNuevo.updateList();
						Gdx.app.debug(TAG, "Request Failed");
					} else {
						envio.estadoEnvio = ESTADOEnvio.ENVIADO;
						InternetNuevo.updateList();
					}
				}

				@Override
				public void failed(Throwable t) {
					envio.estadoEnvio = ESTADOEnvio.ENVIAR;
					InternetNuevo.updateList();
					Gdx.app.debug(TAG, "Request Failed Completely");
				}

				@Override
				public void cancelled() {
					envio.estadoEnvio = ESTADOEnvio.ENVIAR;
					InternetNuevo.updateList();
					Gdx.app.debug(TAG, "request cancelled");
				}

			});
        }

        @Override
        public void handleHttpResponse(HttpResponse httpResponse) 
        {
        	serverStatus = httpResponse.getStatus().getStatusCode();
        	if (serverStatus == 200) {
        		String servercontent = httpResponse.getResultAsString();
        		if (servercontent.contains("\"on\"")) {
        			estadoInternet = ESTADOInternet.ESPERANDO;
        		} else {
        			estadoInternet = ESTADOInternet.SINConexion;
        			Gdx.app.error(TAG, "El servidor esta marcado como apagado!");
        		}
        	} else {
        		if (server.equals(serverMAIN)) {
            		server = serverBackUP;
            		Gdx.app.debug(TAG, "Cambiando al servidor local");
            		new InternetStatus();
            	} else {
            		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " ERROR: " + serverStatus);
            		estadoInternet = ESTADOInternet.SINConexion;
            	}
        	}
        }

        @Override
        public void failed(Throwable t) 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		estadoInternet = ESTADOInternet.SINConexion;
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: failed");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }

        @Override
        public void cancelled() 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		estadoInternet = ESTADOInternet.SINConexion;
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: cancelled");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }
    }

	protected static void updateList() {
		// TODO Auto-generated method stub
		
		// TODO. Tengo que revisar que el envio sea de un objeto que incluya el contenido y la etiqueta!
	}
}
