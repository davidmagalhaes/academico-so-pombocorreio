package com.master.engcomp.so.proj;

import java.util.ArrayList;
import java.util.List;

public class Usuario extends Thread {
	private int tempoEscrita;

	private EstadoUsuario estadoAtual;
	
	private CaixaMensagens caixaMensagens;
	
	private List<UsuarioListener> listeners = new ArrayList<>();
	
	public void run() {
		try {
			while(true) {
				mudarEstado(EstadoUsuario.ESCREVENDO);
				sleep(tempoEscrita);
				
				mudarEstado(EstadoUsuario.BLOQUEADO);
				caixaMensagens.inserirMensagem();
			}
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		finally {
			mudarEstado(EstadoUsuario.MORTO);
		}
	}
	
	private void mudarEstado(EstadoUsuario novoEstado) {
		for(UsuarioListener listener : listeners) {
			listener.mudancaEstado(this, novoEstado);
		}
		
		estadoAtual = novoEstado;
	}
	
	public void addUsuarioListener(UsuarioListener listener) {
		listeners.add(listener);
	}
	
	public EstadoUsuario getEstadoAtual() {
		return estadoAtual;
	}
	
	public int getTempoEscrita() {
		return tempoEscrita;
	}

	public void setTempoEscrita(int tempoEscrita) {
		this.tempoEscrita = tempoEscrita;
	}
	
	public void setCaixaMensagens(CaixaMensagens caixaMensagens) {
		this.caixaMensagens = caixaMensagens;
	}
}
