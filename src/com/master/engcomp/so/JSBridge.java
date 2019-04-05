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
    
    /** 
     * Método que cria um pombo. Se a simulação já estiver executando,
     * o pombo é executado logo após sua criação. Somente um pombo pode estar
     * em execução por vez, por isso, ao criar um novo pombo, o pombo anterior
     * se houver, será morto.
     * 
     * @param numeroMensagens numero de mensagens que o pombo correio leva a cada carga
     * @param tempoCarga tempo, em segundos, que o pombo correio leva para carregar mensagens consigo
     * @param tempoVoo tempo, em segundos, de voo entre a caixa de mensagens e o destino
     * @param tempoDescarga tempo, em segundos, que o pombo correio leva para descarregar as mensagens no destino.
     * 
     * */
    public long criarPombo(int numeroMensagens, int tempoCarga, int tempoVoo, int tempoDescarga) {
    	if(pomboCorreio != null) {
    		pomboCorreio.interrupt();
    	}
    	
    	pomboCorreio = new PomboCorreio();
    	
    	pomboCorreio.setNumeroMensagens(numeroMensagens);
    	pomboCorreio.setTempoCarga(tempoCarga*1000);
    	pomboCorreio.setTempoDescarga(tempoDescarga*1000);
    	pomboCorreio.setTempoVoo(tempoVoo*1000);
    	
    	if(iniciado) {
    		pomboCorreio.start();
    	}
    	
    	log("Pombo criado! %d %d %d %d", numeroMensagens, tempoCarga, tempoVoo, tempoDescarga);
    	
    	return pomboCorreio.getId();
    }
    
    /** 
     * Método que cria um usuário. Se a simulação já estiver em execução,
     * o usuário será executado logo após sua criação.
     * 
     * @param tempoEscrita tempo, em segundos, que um usuário leva para
     * 	escrever uma nova mensagem
     * 
     * @return id do usuário recém criado.
     * */
    public long criarUsuario(int tempoEscrita) {
    	Usuario usuario = new Usuario();
    	
    	usuario.setTempoEscrita(tempoEscrita*1000);
    	
    	usuarios.add(usuario);
    	
    	if(iniciado) {
    		usuario.start();
    	}
    	
    	log("Usuario criado! %d", tempoEscrita);
    	
    	return usuario.getId();
    }
    
    /** 
     * Inicia a simulação
     * */
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
							novoEstadoPomboCorreio.name()));
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
					
					log("Usuario %d mudou para o estado %s", usuario.getId(), novoEstadoUsuario.name());
				}
			});
    		
    		usuario.start();
    	}
    	
    	log("INICIADO!");
    	
    	iniciado = true;
    }
    
    /** 
     * Para a simulação e reseta os valores para os valores iniciais, 
     * para que outra simulação possa ser iniciada em seguida.
     * */
    public void parar() {
    	pomboCorreio.interrupt();
    	pomboCorreio = null;
    	
    	for(Usuario usuario : usuarios) {
    		usuario.interrupt();
    	}
    	
    	usuarios.clear();
    	
    	log("PARADO!");
    	
    	iniciado = false;
    }
    
    /**
     * Mata um usuário.
     * 
     * @param usuarioId Id do usuário que será morto
     */
    public void matarUsuario(long usuarioId) {
    	usuarios.stream().filter(it -> it.getId() == usuarioId).forEach(it -> it.interrupt());
    	log("Usuario %d foi morto!", usuarioId);
    }
    
    /** 
     * Mata o pombo.
     * */
    public void matarPombo() {
    	pomboCorreio.interrupt();
    	log("Pombo foi morto!");
    }
    
    public void jslog(String text){
    	log.log(Level.INFO, String.format(text));  
    }
    
    private void log(String text, Object... args){
    	log.log(Level.INFO, String.format(text, args));    	
    }
}
