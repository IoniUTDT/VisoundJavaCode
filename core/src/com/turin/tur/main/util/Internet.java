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
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.TimeUtils;


public class Internet {

	private static final boolean CODETESTMODE = true;
	private static final String TAG = Internet.class.getName();
	//public static boolean internetChecked=false;
	public static boolean serverOnline=false;
	//public static String serverStatus="";
	//public static HttpStatus serverStatusCode;
	public static boolean serverOk;
	public static int serverStatus=0;
	public static final String serverBackUP = "http://172.18.19.6:3000/";
	public static final String serverMAIN = "http://turintur.dynu.com/";
	public static String server=serverMAIN;
	public static String pathToSend = "logs/logToSend";
	public static String pathSent = "logs/logSent";
	public static String pathSending = "logs/logSending";
	public static boolean envioPendiente=false;
	
	public static void checkInternet() {

		checkLogsFolders();
		if (CODETESTMODE) return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				new HttpChecker();
			}
			
		}).start();
		
	}

	
	private static void PUT(final Enviable envio) {

		if (CODETESTMODE) return;
		Array<String> urls = new Array<String>();
		//urls.add(server+"Envio");
		urls.add("http://turintur.dynu.com/Envio");
		
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

					//Gdx.app.debug(TAG, url);
					
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
			
			
			// Mueve el archivo a la carpeta de datos enviados
			String pathFrom = pathSending + "/" + this.instance + "." + this.tipoDeEnvio;
			String pathTo = pathSent + "/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFrom).moveTo(Gdx.files.local(pathTo));
			
			// Mueves los tags
			String pathFromTag = pathSending + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			String pathToTag = pathSent + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFromTag).moveTo(Gdx.files.local(pathToTag));
			
		}
		
		private void noEnviado() {
			
			// devuelve el archivo a la carpeta de datos para enviar
			String pathFrom = pathSending + "/" + this.instance + "." + this.tipoDeEnvio;
			String pathTo = pathToSend + "/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFrom).moveTo(Gdx.files.local(pathTo));
			// Mueves los tags
			String pathFromTag = pathSending + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			String pathToTag = pathToSend + "/tags/" + this.instance + "." + this.tipoDeEnvio;
			Gdx.files.local(pathFromTag).moveTo(Gdx.files.local(pathToTag));

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

	public static void addDataToSend(Object objeto, TIPO_ENVIO tipo, String tag) {
		// Transformamos el objeto en un enviable
		Json json = new Json();
		json.setUsePrototypes(false);
		json.setOutputType(OutputType.json);
		String string = json.toJson(objeto);
		Enviable envio = new Enviable (string, tipo, tag);	
		// Creamos el archivo que se va a enviar
		String path = pathToSend + "/" + envio.instance + "." + envio.tipoDeEnvio;
		FileHandle file = Gdx.files.local(path);
		file.writeString(envio.contenido, false);
		String path2 = pathToSend + "/tags/" + envio.instance + "." + envio.tipoDeEnvio;
		FileHandle file2 = Gdx.files.local(path2);
		file2.writeString(envio.tag, false);
		// Mandamos la instruccion de enviar todo
		Internet.tryToSendAll();
	}
	
	private static void checkLogsFolders() {
		if (!Gdx.files.local(pathToSend).exists()){
			FileHandle dir = Gdx.files.local(pathToSend);
			dir.mkdirs();
			dir = Gdx.files.local(pathToSend+"/tags");
			dir.mkdirs();
		}
		if (!Gdx.files.local(pathSent).exists()){
			FileHandle dir = Gdx.files.local(pathSent);
			dir.mkdirs();
			dir = Gdx.files.local(pathSent+"/tags");
			dir.mkdirs();
		}
		if (!Gdx.files.local(pathSending).exists()){
			FileHandle dir = Gdx.files.local(pathSending);
			dir.mkdirs();
			dir = Gdx.files.local(pathSending+"/tags");
			dir.mkdirs();
		} else {
			// movemos los archivos que podrian haber quedado en el sending si justo se cerro la app cuando enviaba al to send
			for(FileHandle file: Gdx.files.local(pathSending).list()) {
				if (!file.isDirectory()) {
					file.moveTo(Gdx.files.local(pathToSend));
				}
			}
			for(FileHandle file: Gdx.files.local(pathSending+"/tags").list()) {
				if (!file.isDirectory()) {
					file.moveTo(Gdx.files.local(pathToSend+"/tags"));
				}
			}
		}
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
		// movemos el archivo que genero en envio a la carpeta temporal
		file.moveTo(Gdx.files.local(pathSending));
		fileTag.moveTo(Gdx.files.local(pathSending+"/tags/"));
		Internet.PUT(envio);
	}
	
	
	public static class HttpChecker implements HttpResponseListener {   

        HttpRequest request;
        String url = server+"status/";
        
        public HttpChecker()
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
        			serverOk = true;
        		} else {
        			serverOk = false;
        			Gdx.app.error(TAG, "El servidor esta marcado como apagado!");
        		}
        	} else {
        		if (server.equals(serverMAIN)) {
            		server = serverBackUP;
            		Gdx.app.debug(TAG, "Cambiando al servidor local");
            		new HttpChecker();
            	} else {
            		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " ERROR: " + serverStatus);
            	}
        	}
        }

        @Override
        public void failed(Throwable t) 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		new HttpChecker();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: failed");
        	}
        }

        @Override
        public void cancelled() 
        {
        	if (server.equals(serverMAIN)) {
        		server = serverBackUP;
        		Gdx.app.debug(TAG, "Cambiando al servidor local");
        		new HttpChecker();
        	} else {
        		Gdx.app.debug(TAG, "Error conectando con el servidor " + server + " STATUS: cancelled");
        	}
        }
    }
}
*/