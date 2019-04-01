package com.master.engcomp.so;
	
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class Main extends Application {
	
	private JSBridge bridge;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,400,400);
			
			WebView webview = (WebView) scene.lookup("#webview");
			
			webview.setContextMenuEnabled(true);
			webview.getEngine().setOnError(event -> System.out.println(event.getMessage()));
			webview.getEngine().setOnAlert(event -> System.out.println(event.getData()));

			WebEngine engine = webview.getEngine();
			engine.setJavaScriptEnabled(true);
			JSObject jsobj = (JSObject) engine.executeScript("window");
			bridge = new JSBridge(engine);
			
			jsobj.setMember("javaobj", bridge);			
			
			engine.load("file:///C:/Users/Alunos/Desktop/index.html");
			
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
