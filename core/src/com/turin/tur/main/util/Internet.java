package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.obsoleto.Enviables;
import com.turin.tur.obsoleto.SessionEnviables;



public class Internet {

	private static final String TAG = Internet.class.getName();
	public static boolean internetChecked=false;
	public static boolean serverOnline=false;
	public static String serverStatus="";
	public static HttpStatus serverStatusCode;
	public static boolean serverOk;
	public static final String server = "http://turintur.dynu.com/";
	public static String pathToSend = "logToSend";
	public static String pathSent = "logSent";
	
	public static void Check() {
		serverOnline = false; // Reinicia el status del server
		serverOk = false;
		internetChecked=true; // Indica que comenzo a chaequear
		new Thread(new Runnable() {

			@Override
			public void run() {

				String requestJson = "";
				
				final Net.HttpRequest request = new Net.HttpRequest(HttpMethods.GET);
				request.setContent(requestJson);

				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				request.setUrl(server+"status/");

				Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

					@Override
					public void handleHttpResponse(Net.HttpResponse httpResponse) {

						serverOnline=true;
						serverStatus = httpResponse.getStatus().toString();
						serverStatusCode= httpResponse.getStatus();
						String rta = httpResponse.getResultAsString();
						if (rta.contains("on")) {
							serverOk=true;
							Gdx.app.debug(TAG, "Server ok");
						} else {
							serverOk = false;
							Gdx.app.debug(TAG, "Server no ok");
						}
						
					}

					@Override
					public void failed(Throwable t) {
						serverOnline=false;
						Gdx.app.debug(TAG, "Request Failed Completely");
					}

					@Override
					public void cancelled() {
						serverOnline=false;
						Gdx.app.debug(TAG, "request cancelled");
					}

				});

			}
		}).start();
		
	}
	
	
	public static void PUT(final Enviables objetoEnviado) {

		Array<String> urls = new Array<String>();
		urls.add("http://turintur.dynu.com/" + objetoEnviado.getClass().getSimpleName());
		//urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		/*
		if (objetoEnviado.contenidoLevel.size>0) {
			Gdx.app.debug(TAG, "Tamañan de datos level" + objetoEnviado.contenidoLevel.size);
		}
		*/
		
		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					Json json = new Json();
					json.setOutputType(OutputType.json);
				    json.setUsePrototypes(false);
					String requestJson = json.toJson(objetoEnviado.contenido);
					
					Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
					request.setContent(requestJson);

					request.setHeader("Content-Type", "application/json");
					request.setHeader("Accept", "application/json");
					request.setUrl(url);

					
					/*
					if (objetoEnviado.contenidoLevel.size>0) {
						Gdx.app.debug(TAG, "Contexto: " + objetoEnviado.levelLogHistory);
						Gdx.app.debug(TAG, "Json:" + requestJson);
					}
					*/
					
					Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

						@Override
						public void handleHttpResponse(Net.HttpResponse httpResponse) {
							int statusCode = httpResponse.getStatus().getStatusCode();
							if (statusCode != HttpStatus.SC_CREATED) {
								Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
								objetoEnviado.noEnviado();
								Gdx.app.debug(TAG, "Request Failed");
							} else {
								objetoEnviado.enviado();
							}
						}

						@Override
						public void failed(Throwable t) {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "Request Failed Completely");
						}

						@Override
						public void cancelled() {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "request cancelled");
						}

					});

				}
			}).start();
		}
	}
	
	public static void PUT2(final Enviable objetoEnviado) {

		Array<String> urls = new Array<String>();
		urls.add("http://turintur.dynu.com/" + objetoEnviado.tipoDeEnvio + "/");
		
		//urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		/*
		if (objetoEnviado.contenidoLevel.size>0) {
			Gdx.app.debug(TAG, "Tamañan de datos level" + objetoEnviado.contenidoLevel.size);
		}
		*/
		
		for (final String url : urls) {

			Gdx.app.debug(TAG, url);
			new Thread(new Runnable() {

				@Override
				public void run() {

					//Json json = new Json();
					//json.setOutputType(OutputType.json);
				    //json.setUsePrototypes(false);
					String requestJson = objetoEnviado.contenido;
					
					Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
					request.setContent(requestJson);

					request.setHeader("Content-Type", "application/json");
					request.setHeader("Accept", "application/json");
					
					
					request.setUrl(url);
					//request.setUrl(server+"status/");
					// Gdx.app.debug(TAG, server+"status/");

					
					/*
					if (objetoEnviado.contenidoLevel.size>0) {
						Gdx.app.debug(TAG, "Contexto: " + objetoEnviado.levelLogHistory);
						Gdx.app.debug(TAG, "Json:" + requestJson);
					}
					*/
					
					Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

						@Override
						public void handleHttpResponse(Net.HttpResponse httpResponse) {
							int statusCode = httpResponse.getStatus().getStatusCode();
							if (statusCode != HttpStatus.SC_CREATED) {
								Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
								objetoEnviado.noEnviado();
								Gdx.app.debug(TAG, "Request Failed");
							} else {
								objetoEnviado.enviado();
							}
						}

						@Override
						public void failed(Throwable t) {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "Request Failed Completely");
						}

						@Override
						public void cancelled() {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "request cancelled");
						}

					});

				}
			}).start();
		}
	} 

	public static enum TIPO_ENVIO {
		SESION
	}
	
	public static class Enviable {
		public long instance;
		public String contenido;
		public TIPO_ENVIO tipoDeEnvio;
		
		public void enviado() {
			Gdx.app.debug(TAG, "EHA!");
		}
		
		public void noEnviado() {
		}
		
		public Enviable (String data, TIPO_ENVIO tipoDeEnvio) {
			this.instance = TimeUtils.millis();
			this.contenido = data;
			this.tipoDeEnvio = tipoDeEnvio;
		}
		
		public Enviable (long id, String data, TIPO_ENVIO tipoDeEnvio) {
			this.instance = id;
			this.contenido = data;
			this.tipoDeEnvio = tipoDeEnvio;
		}
	}

	public static void addData(Enviable enviable) {
		Gdx.app.debug(TAG, "Creando log");
		checkLogs();
		String path = pathToSend + "/" + enviable.instance + "." + enviable.tipoDeEnvio;
		FileHandle file = Gdx.files.local(path);
		file.writeString(enviable.contenido, false);
	}
	
	public static void checkLogs() {
		if (!Gdx.files.local(pathToSend).exists()){
			FileHandle dir = Gdx.files.local(pathToSend);
			dir.mkdirs();
		}
		if (!Gdx.files.local(pathSent).exists()){
			FileHandle dir = Gdx.files.local(pathSent);
			dir.mkdirs();
		}
	}
	
	public static void tryToSend () {
		for(FileHandle file: Gdx.files.local(pathToSend).list()) {
			String data = file.readString();
			Enviable envio = new Enviable (Long.valueOf(file.nameWithoutExtension()), data, TIPO_ENVIO.valueOf(file.extension()));
			SessionEnviables prueba = new SessionEnviables();
			prueba.contenido = "{'hola':'chau'}";
			Internet.PUT(prueba);
		}
	}
}
