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
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.TimeUtils;


public class Internet {

	private static final String TAG = Internet.class.getName();
	public static boolean internetChecked=false;
	public static boolean serverOnline=false;
	public static String serverStatus="";
	public static HttpStatus serverStatusCode;
	public static boolean serverOk;
	public static final String serverBackUP2 = "http://www.google.com";
	public static final String serverBackUP = "http://172.18.19.6:3000";
	public static final String server = "http://turintur.dynu.com/";
	public static String pathToSend = "logs/logToSend";
	public static String pathSent = "logs/logSent";
	
	public static void Check() {
		checkLogs();
		serverOnline = false; // Reinicia el status del server
		serverOk = false;
		internetChecked=true; // Indica que comenzo a chaequear
		new Thread(new Runnable() {

			@Override
			public void run() {

				//HttpChecker pruebaBackUp = new HttpChecker(serverBackUP2);
				
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
	
	private static void PUT(final Enviable envio) {

		Array<String> urls = new Array<String>();
		urls.add("http://turintur.dynu.com/Envio");
		//urls.add("http://turintur.dynu.com/" + objetoEnviado.getClass().getSimpleName());
		
		//urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

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
								envio.noEnviado();
								Gdx.app.debug(TAG, "Request Failed");
							} else {
								envio.enviado();
							}
						}

						@Override
						public void failed(Throwable t) {
							envio.noEnviado();
							Gdx.app.debug(TAG, "Request Failed Completely");
						}

						@Override
						public void cancelled() {
							envio.noEnviado();
							Gdx.app.debug(TAG, "request cancelled");
						}

					});

				}
			}).start();
		}
	}
	
	public static enum TIPO_ENVIO {
		NEWSESION, NEWLEVEL, CONVERGENCIAPARALELISMOV1, CONVERGENCIA, CONVERGENCIAANGULOSV1, NEWLEVELANGULOS, NEWLEVELPARALELISMO
	}
	
	private static class Enviable {
		public long instance;
		public String contenido;
		public TIPO_ENVIO tipoDeEnvio;
		public String tag;
		
		private void enviado() {
			// Gdx.app.debug(TAG, "Paquete de datos enviado: " + this.instance + ", tipo: " + this.tipoDeEnvio);
			// Mueve el archivo a la carpeta de datos enviados
			String pathFrom = pathToSend + "/" + this.instance + "." + this.tipoDeEnvio;
			String pathTo = pathSent + "/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFrom).moveTo(Gdx.files.local(pathTo));
			// Mueves los tags
			String pathFromTag = pathToSend + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			String pathToTag = pathSent + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFromTag).moveTo(Gdx.files.local(pathToTag));
			
			//Gdx.app.debug(TAG, "EHA!");
		}
		
		private void noEnviado() {
			Gdx.app.error(TAG, "Verifique conectividad con el servidor!");
			Gdx.app.error(TAG, "Paquete de datos: " + this.instance);
			Gdx.app.error(TAG, "Tag: " + this.tag);
		}
		
		private Enviable (String data, TIPO_ENVIO tipoDeEnvio, String tag) {
			this.instance = TimeUtils.millis();
			this.contenido = data;
			this.tipoDeEnvio = tipoDeEnvio;
			this.tag = tag;
		}
		
		public Enviable (long id, String data, TIPO_ENVIO tipoDeEnvio, String tag) {
			this.tag = tag;
			this.instance = id;
			this.contenido = data;
			this.tipoDeEnvio = tipoDeEnvio;
		}
	}

	public static void sendData(Object objeto, TIPO_ENVIO tipo, String tag) {
		// Transformamos el objeto en un enviable
		Json json = new Json();
		json.setUsePrototypes(false);
		json.setOutputType(OutputType.json);
		String string = json.toJson(objeto);
		// Gdx.app.debug(TAG, string);
		Enviable envio = new Enviable (string, tipo, tag);	
		String path = pathToSend + "/" + envio.instance + "." + envio.tipoDeEnvio;
		FileHandle file = Gdx.files.local(path);
		file.writeString(envio.contenido, false);
		String path2 = pathToSend + "/tags/" + envio.instance + "." + envio.tipoDeEnvio;
		FileHandle file2 = Gdx.files.local(path2);
		file2.writeString(envio.tag, false);
		Internet.tryToSend(file);
		
	}
	
	private static void checkLogs() {
		if (!Gdx.files.local(pathToSend).exists()){
			FileHandle dir = Gdx.files.local(pathToSend);
			dir.mkdirs();
		}
		if (!Gdx.files.local(pathSent).exists()){
			FileHandle dir = Gdx.files.local(pathSent);
			dir.mkdirs();
		}
		Internet.tryToSendAll();
	}
	
	private static void tryToSendAll () {
		for(FileHandle file: Gdx.files.local(pathToSend).list()) {
			if (!file.isDirectory()) {
				tryToSend(file);
			}
		}
	}
	
	private static void tryToSend (FileHandle file) {
		String data = file.readString();
		long id = Long.valueOf(file.nameWithoutExtension());
		String pathTag = file.parent() + "/tags/" + file.name();
		FileHandle fileTag = Gdx.files.local(pathTag);
		String tag = fileTag.readString();
		TIPO_ENVIO tipo = TIPO_ENVIO.valueOf(file.extension());
		Enviable envio = new Enviable (id, data, tipo, tag);
		Internet.PUT(envio);
	}
	
	
	public static class HttpChecker implements HttpResponseListener {   

        HttpRequest request;
        public int state;
        

        public HttpChecker(String url)
        {
            request = new HttpRequest();
            request.setMethod(Net.HttpMethods.GET); //or POST
            request.setContent(""); //you can put here some PUT/GET content
            request.setUrl("http://www.google.com");
            Gdx.net.sendHttpRequest(request, this);
        }

        @Override
        public void handleHttpResponse(HttpResponse httpResponse) 
        {
        	state = httpResponse.getStatus().getStatusCode();
        	Gdx.app.debug(TAG, "Exitos" +state);
        }

        @Override
        public void failed(Throwable t) 
        {
        	state = -1;
            Gdx.app.debug(TAG, "failed HttpChecker");
        }

        @Override
        public void cancelled() 
        {
        	state = -1;
        	Gdx.app.debug(TAG, "cancelled  HttpChecker");  
        }
    }
}
