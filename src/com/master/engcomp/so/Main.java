package com.master.engcomp.so;
	
import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

//Classe que inicializa o JAVAFX e o WebView
public class Main extends Application {
	
	private JSBridge bridge;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			WebView root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			
			Scene scene = new Scene(root,1200,600);
			
			WebView webview = (WebView) scene.lookup("#webview");
			
			
			webview.setContextMenuEnabled(true);
			webview.getEngine().setOnError(event -> System.out.println(event.getMessage()));
			webview.getEngine().setOnAlert(event -> System.out.println(event.getData()));

			WebEngine engine = webview.getEngine();
			engine.setJavaScriptEnabled(true);
			JSObject jsobj = (JSObject) engine.executeScript("window");
			bridge = new JSBridge(engine);
			
			jsobj.setMember("javaobj", bridge);			
			
			//System.out.println("file://" + getClass().getResource("/../..").toExternalForm().replace("file:/", "") + "index.html");
			//engine.load("file:///C:/Users/Alunos/Desktop/so-project/public/index.html");
			//engine.load("file:///home/davidmagalhaes/Projetos/so-project/public/index.html");
			
			System.out.println("file:///" + new File(".").getAbsolutePath().replace("\\", "/") + "/index.html");
			engine.load("file:///" + new File(".").getAbsolutePath().replace(".", "").replace("\\", "/") + "index.html");
			
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
