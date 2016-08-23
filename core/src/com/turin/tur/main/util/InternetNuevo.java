package com.turin.tur.main.util;
/*
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

	public static class Enviable {
		public String origen;
		ESTADOEnvio estadoEnvio;
		long instance = TimeUtils.millis();
		Object objeto;
		TIPOdeENVIO tipo;
	}
	
	public class Envio {   

        HttpRequest request;
        String url = server + "Envio";
        
        public Envio(final Enviable envio) {
        	
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
				public void cancelled() {
					envio.estadoEnvio = ESTADOEnvio.ENVIAR;
					InternetNuevo.afterEnvio();
					Gdx.app.debug(TAG, "request cancelled");
					checkConectividad ();
				}

				@Override
				public void failed(Throwable t) {
					envio.estadoEnvio = ESTADOEnvio.ENVIAR;
					InternetNuevo.afterEnvio();
					Gdx.app.debug(TAG, "Request Failed Completely");
					checkConectividad ();
				}

				@Override
				public void handleHttpResponse(Net.HttpResponse httpResponse) {
					int statusCode = httpResponse.getStatus().getStatusCode();
					if (statusCode != HttpStatus.SC_CREATED) {
						Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
						envio.estadoEnvio = ESTADOEnvio.ENVIAR;
						InternetNuevo.afterEnvio();
						Gdx.app.debug(TAG, "Request Failed");
						checkConectividad ();
					} else {
						envio.estadoEnvio = ESTADOEnvio.ENVIADO;
						InternetNuevo.afterEnvio();
					}
				}
			});
        }
    }
	
	public enum ESTADOEnvio {
		ENVIADO,ENVIANDO,ENVIAR
	}
	
	public enum ESTADOInternet {
		ENVIOSPendientes, ESPERANDO, PROCESANDOEnvios, SINConexion   
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
            Gdx.net.sendHttpRequest(request,this);
        }

        @Override
        public void cancelled() 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: cancelled");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }

        @Override
        public void failed(Throwable t) 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		new InternetStatus();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: failed");
        		estadoInternet = ESTADOInternet.SINConexion;
        	}
        }

        @Override
        public void handleHttpResponse(HttpResponse httpResponse) 
        {
        	serverStatus = httpResponse.getStatus().getStatusCode();
        	if (serverStatus == 200) {
        		String servercontent = httpResponse.getResultAsString();
        		if (servercontent.contains("\"on\"")) {
        			Gdx.app.debug(TAG, "Servidor Online");
        			InternetNuevo.afterEnvio();
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
    }
	
	public enum TIPOdeENVIO {
		INICIONIVEL, RESULTADOS, SESION
	}
	
	public static final String fileExt = ".data";
	public static final String filename = "Internet";
	public static final String path = Visound.pathLogs + "/" + filename + fileExt;
	static long instance = TimeUtils.millis();
	public static final String pathBackup = Visound.pathLogs + "/" + filename + instance + fileExt;
	public static final String serverBackUP = "http://172.18.19.6:3000/";
	public static final String serverMAIN = "http://turintur.dynu.com/";
	private static Array<Enviable> CadenaEnvios = new Array<Enviable>();
	private static ESTADOInternet estadoInternet = ESTADOInternet.ENVIOSPendientes;
	private static boolean savePendiente;
	private static String server=serverMAIN;

	private static int serverStatus=0;
	
	private static final String TAG = InternetNuevo.class.getName();


	
	public static void agregarEnvio(Object objeto, TIPOdeENVIO tipo, String origen) {
		Enviable envio = new Enviable();
		envio.objeto = objeto;
		envio.tipo = tipo;
		envio.origen = origen;
		envio.estadoEnvio = ESTADOEnvio.ENVIAR;
		if (estadoInternet == ESTADOInternet.ESPERANDO) {
			estadoInternet = ESTADOInternet.ENVIOSPendientes;
		}
		CadenaEnvios.add(envio);
		if (estadoInternet == ESTADOInternet.SINConexion) {
			saveDataToDisk();
		}
	}
	private static void saveDataToDisk() {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(CadenaEnvios));
	}

	protected static void afterEnvio() {
		for (Enviable envio : CadenaEnvios) {
			if (envio.estadoEnvio == ESTADOEnvio.ENVIANDO) {
				return;
			}
		}
		for (Enviable envio : CadenaEnvios) {
			if (envio.estadoEnvio == ESTADOEnvio.ENVIAR) {
				estadoInternet = ESTADOInternet.ENVIOSPendientes;
				return;
			}
		}
		// Significa que no hay ni envio pendiente ni envio en proceso
		InternetNuevo.savePendiente = true;
		estadoInternet = ESTADOInternet.ESPERANDO;
	}
	
	public InternetNuevo() {
	}
	
	public void checkConectividad() {
		estadoInternet = ESTADOInternet.PROCESANDOEnvios;
		new Thread(new Runnable() {

			@Override
			public void run() {
				new InternetStatus();
			}
			
		}).start();
		
	}
	
	public void loadSavedData() {
		FileHandle dataFile = Gdx.files.external(path);
		if (dataFile.exists()) {
			FileHandle backUp = Gdx.files.external(pathBackup);
			dataFile.copyTo(backUp);
			String savedData = FileHelper.readLocalFile(path);
			Json json = new Json();
			json.setUsePrototypes(false);
			Array<Enviable> logsLeidos =  json.fromJson(InternetNuevo.CadenaEnvios.getClass(), savedData);
			for (Enviable entrada : logsLeidos) {
				if (entrada.estadoEnvio == ESTADOEnvio.ENVIAR) {
					agregarEnvio (entrada.objeto, entrada.tipo, entrada.origen);
				}
				if (entrada.estadoEnvio == ESTADOEnvio.ENVIANDO) {
					agregarEnvio (entrada.objeto, entrada.tipo, entrada.origen);
				}
			}
		}
	}
	
	
	public boolean offline() {
		return (estadoInternet == ESTADOInternet.SINConexion);
	}
	
	public boolean procesandoCosas() {
		return (estadoInternet == ESTADOInternet.PROCESANDOEnvios);
	}

	public void update() {
		if (estadoInternet == ESTADOInternet.ENVIOSPendientes){
			makeEnvio();
		}
		if (savePendiente) {
			saveDataToDisk();
			savePendiente = false;
		}
	}

	private void makeEnvio() {
		if (Visound.modoDebug) {
			return;
		}
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
}
*/