package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.Visound;

public class Internet {

	private static final String TAG = Internet.class.getName();

	private static final String pathLogs = Visound.pathLogs + "/internet/";
	private final String serverBackUP = "http://172.18.19.6:3000/";
	private final String serverMAIN = "http://turintur.dynu.com/";
	private String server=serverMAIN;
	private int serverStatus;
	public boolean treadActivo = false;
	public boolean internetDisponible = false; 
	private Array<Enviable> bufferEnviables = new Array<Enviable>();
	
	
	public Internet() {
	}

	public void inicio() {
		revisarConexion();
		revisarLogsViejos();
	}
	
	public void revisarLogsViejos() {
		// Revisamos si hay logs guardados y los procesamos
		FileHandle dirHandle;
		dirHandle = Gdx.files.local(pathLogs);
		if (dirHandle.exists()) {
			for (FileHandle entry: dirHandle.list()) {
				String savedData = entry.readString();
				Json json = new Json();
				json.setUsePrototypes(false);
				Enviable enviableGuardado = new Enviable();
				enviableGuardado = json.fromJson(enviableGuardado.getClass(), savedData);
				if (enviableGuardado.estadoEnvio == ESTADOEnvio.ENVIAR) {
					bufferEnviables.add(enviableGuardado);
				}
			}
		} else {
			dirHandle.mkdirs();
		}
	}

	public void update() {
		
		if (!treadActivo) {
			if (bufferEnviables.size>0) {
				Enviable enviable = bufferEnviables.pop();
				hacerEnvio(enviable);
			}
		}
	}
	
	private void hacerEnvio(final Enviable enviable) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				treadActivo = true;
				
		        String url = server + "Envio";
				
		        Json json = new Json();
				json.setOutputType(OutputType.json);
			    json.setUsePrototypes(false);
				String requestJson = json.toJson(enviable);
				
				Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
				request.setContent(requestJson);
				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				request.setUrl(url);
				
		        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

		        	@Override
					public void cancelled() {
		        		enviable.estadoEnvio = ESTADOEnvio.ENVIAR;
						Gdx.app.debug(TAG, "request cancelled");
						terminate();
					}

					@Override
					public void failed(Throwable t) {
						enviable.estadoEnvio = ESTADOEnvio.ENVIAR;
						Gdx.app.debug(TAG, "Request Failed Completely");
						terminate();
					}

					@Override
					public void handleHttpResponse(Net.HttpResponse httpResponse) {
						int statusCode = httpResponse.getStatus().getStatusCode();
						if (statusCode != HttpStatus.SC_CREATED) {
							Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
							enviable.estadoEnvio = ESTADOEnvio.ENVIAR;
							Gdx.app.debug(TAG, "Request Failed");
							terminate();
						} else {
							enviable.estadoEnvio = ESTADOEnvio.ENVIADO;
							terminate();
						}
					}
					
					private void terminate() {
						updateSavedData();
						treadActivo = false;
					}

					private void updateSavedData() {
						String path = pathLogs + enviable.instance + ".log";
						Json json = new Json();
						json.setUsePrototypes(false);
						FileHelper.writeLocalFile(path, json.toJson(enviable));
					}
			        
		        });

			}
			
		}).start();
		
	}

	public void revisarConexion() {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
			
				treadActivo = true;
				HttpRequest request;
		        String url = server + "status/";
		        
		        request = new HttpRequest();
		        request.setMethod(Net.HttpMethods.GET); //or POST
		        request.setContent(""); //you can put here some PUT/GET content
		        request.setUrl(url);
		        
		        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						serverStatus = httpResponse.getStatus().getStatusCode();
			        	if (serverStatus == 200) {
			        		String servercontent = httpResponse.getResultAsString();
			        		if (servercontent.contains("\"on\"")) {
			        			Gdx.app.debug(TAG, "Servidor Online");
			        		} else {
			        			internetDisponible = false;
			        			Gdx.app.error(TAG, "El servidor esta marcado como apagado!");
			        		}
			        	} else {
			        		if (server.equals(serverMAIN)) {
			            		server = serverBackUP;
			            		Gdx.app.debug(TAG, "Cambiando al servidor local");
			            		revisarConexion();
			            	} else {
			            		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " ERROR: " + serverStatus);
			            		internetDisponible = false;
			            	}
			        	}
			        	terminate();
					}

					@Override
					public void failed(Throwable t) {
						if (server.equals(serverMAIN)) {
			        		server = serverBackUP;
			        		Gdx.app.debug(TAG, "Cambiando al servidor local");
			        		revisarConexion();
			        	} else {
			        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: failed");
			        		internetDisponible = false;
			        	}
						terminate();
					}

					@Override
					public void cancelled() {
						if (server.equals(serverMAIN)) {
			        		server = serverBackUP;
			        		Gdx.app.debug(TAG, "Cambiando al servidor local");
			        		revisarConexion();
			        	} else {
			        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: cancelled");
			        		internetDisponible = false;
			        	}
						terminate();
					}
					
					private void terminate() {
						treadActivo = false;
					}
			        
		        });

			}
			
		}).start();
		        
	}
	
	public void crearEnvio(Object object, TIPOdeENVIO tipo, String origen) {
		Enviable enviable = new Enviable();
		enviable.instance = TimeUtils.millis();
		enviable.objeto = object;
		enviable.tipo = tipo;
		enviable.origen = origen;
		enviable.estadoEnvio = ESTADOEnvio.ENVIAR;
		bufferEnviables.add(enviable);
	}
	
	public static class Enviable {
		public String origen;
		ESTADOEnvio estadoEnvio;
		long instance;
		Object objeto;
		TIPOdeENVIO tipo;
	}
	
	public enum ESTADOEnvio {
		ENVIADO,ENVIAR
	}
	
	public enum TIPOdeENVIO {
		INICIONIVEL, RESULTADOS, SESION
	}
}

