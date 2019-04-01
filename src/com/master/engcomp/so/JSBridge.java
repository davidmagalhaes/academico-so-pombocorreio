package com.master.engcomp.so;

import javafx.scene.web.WebEngine;

public class JSBridge {
	WebEngine webEngine;

    JSBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public boolean printHelloWorld(){
    	System.out.println("Hello World!");
    	webEngine.executeScript("HelloWorld()");
    	return true;
    }
    
    public boolean checkSolution(String solve) {
        if (solve.equalsIgnoreCase("god")) {
            webEngine.executeScript("document.getElementById(\"out\").classList.add(\"enabled\");");
            return true;
        } else return false;
    }
}
