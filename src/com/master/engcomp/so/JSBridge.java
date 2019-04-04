package com.master.engcomp.so;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.master.engcomp.so.proj.CaixaMensagens;
import com.master.engcomp.so.proj.CaixaMensagensListener;
import com.master.engcomp.so.proj.EstadoPomboCorreio;
import com.master.engcomp.so.proj.EstadoUsuario;
import com.master.engcomp.so.proj.PomboCorreio;
import com.master.engcomp.so.proj.PomboCorreioListener;
import com.master.engcomp.so.proj.Usuario;
import com.master.engcomp.so.proj.UsuarioListener;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;

public class JSBridge {
	
	Logger log = Logger.getLogger("log4j");
	
	private WebEngine webEngine;

	private CaixaMensagens caixaMensagens;
	private List<Usuario> usuarios = new ArrayList<>();
	private PomboCorreio pomboCorreio;
	
	private boolean iniciado = false;
	
    JSBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public long criarPombo(int numeroMensagens, int tempoCarga, int tempoVoo, int tempoDescarga) {
    	if(pomboCorreio != null && pomboCorreio.isAlive()) {
    		pomboCorreio.interrupt();
    	}
    	
    	pomboCorreio = new PomboCorreio();
    	
    	pomboCorreio.setTempoCarga(tempoCarga*1000);
    	pomboCorreio.setTempoDescarga(tempoDescarga*1000);
    	pomboCorreio.setTempoVoo(tempoVoo*1000);
    	pomboCorreio.setNumeroMensagens(numeroMensagens*1000);
    	
    	if(iniciado) {
    		pomboCorreio.start();
    	}
    	
    	log("Pombo criado! %d %d %d %d", numeroMensagens, tempoCarga, tempoVoo, tempoDescarga);
    	
    	return pomboCorreio.getId();
    }
    
    public long criarUsuario(int tempoEscrita) {
    	Usuario usuario = new Usuario();
    	
    	usuario.setTempoEscrita(tempoEscrita*1000);
    	
    	usuarios.add(usuario);
    	
    	if(iniciado) {
    		usuario.start();
    	}
    	
    	log("Usu�rio criado! %d", tempoEscrita);
    	
    	return usuario.getId();
    }
    
    public void iniciar(int maximoCaixaMensagens) {
    	log("INICIANDO...");
    	
    	caixaMensagens = new CaixaMensagens(maximoCaixaMensagens);
    	    	
    	caixaMensagens.addCaixaMensagensListener(new CaixaMensagensListener() {
			@Override
			public void mensagensConsumidas(int numeroMensagens) {
				Platform.runLater(()->{
					webEngine.executeScript(String.format("updateCaixaMensagens(%d)", caixaMensagens.getNumeroMensagens()));
				});
			}
			
			@Override
			public void mensagemInserida() {
				Platform.runLater(()->{
					webEngine.executeScript(String.format("updateCaixaMensagens(%d)", caixaMensagens.getNumeroMensagens()));
				});
			}
		});
    	
    	pomboCorreio.setCaixaMensagens(caixaMensagens);    	
    	pomboCorreio.addPomboCorreioListener(new PomboCorreioListener() {
			@Override
			public void mudancaEstado(PomboCorreio pomboCorreio, EstadoPomboCorreio novoEstadoPomboCorreio) {
				Platform.runLater(()->{
					webEngine.executeScript(String.format("mudancaEstadoPomboCorreio('%s')", 
							novoEstadoPomboCorreio.ordinal()));
				});
				
				log("Pombo mudou para o estado %s", novoEstadoPomboCorreio.name());
			}
		});
    	    	
    	pomboCorreio.start();
    	    	
    	for(Usuario usuario : usuarios) {
    		usuario.setCaixaMensagens(caixaMensagens);
    		usuario.addUsuarioListener(new UsuarioListener() {
				@Override
				public void mudancaEstado(Usuario usuario, EstadoUsuario novoEstadoUsuario) {
					Platform.runLater(()->{
						webEngine.executeScript(String.format("mudancaEstadoUsuario(%d, '%s')", 
								usuario.getId(), novoEstadoUsuario.name()));
					});
					
					log("Usu�rio %d mudou para o estado %s", usuario.getId(), novoEstadoUsuario.name());
				}
			});
    		
    		usuario.start();
    	}
    	
    	log("INICIADO!");
    	
    	iniciado = true;
    }
    
    public void parar() {
    	pomboCorreio.interrupt();
    	
    	for(Usuario usuario : usuarios) {
    		usuario.interrupt();
    	}
    	
    	usuarios.clear();
    	
    	log("PARADO!");
    	
    	iniciado = false;
    }
    
    public void matarUsuario(long usuarioId) {
    	usuarios.stream().filter(it -> it.getId() == usuarioId).forEach(it -> it.interrupt());
    	log("Usu�rio %d foi morto!", usuarioId);
    }
    
    public void matarPombo() {
    	pomboCorreio.interrupt();
    	log("Pombo foi morto!");
    }
    
    public void jslog(String text){
    	Platform.runLater(()->{
    		System.out.println(String.format("--- JS-LOG: %s", text));
    	});
    }
    
    private void log(String text, Object... args){
    	log.log(Level.INFO, String.format(text, args));    	
    }
}
