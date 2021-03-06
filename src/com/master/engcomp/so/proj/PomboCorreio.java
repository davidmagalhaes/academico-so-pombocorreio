package com.master.engcomp.so.proj;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Thread que representa o pombo correio que entrega as mensagens da caixa de mensagens.
 * é uma máquina de estados que define os estados do pombo.
 *
 */
public class PomboCorreio extends Thread {
	private int numeroMensagens;
	private int tempoCarga;
	private int tempoVoo;
	private int tempoDescarga;
	
	private EstadoPomboCorreio estadoAtual;
	private List<PomboCorreioListener> listeners = new ArrayList<>();;
	
	private CaixaMensagens caixaMensagens;
	
	@Override
	public void run() {
		try {
			while(true) {
				//O Pombo tenta pegar as mensagens da caixa de mensagens.
				//Se não houver mensagens suficientes, ele dorme.
				mudarEstado(EstadoPomboCorreio.BLOQUEADO);
				caixaMensagens.pegarMensagens(numeroMensagens);
				
				mudarEstado(EstadoPomboCorreio.CARREGANDO);
				busyWaitLoop(tempoCarga);
				
				mudarEstado(EstadoPomboCorreio.VOANDO_IDA);
				busyWaitLoop(tempoVoo);
				
				mudarEstado(EstadoPomboCorreio.DESCARREGANDO);
				busyWaitLoop(tempoDescarga);
				
				mudarEstado(EstadoPomboCorreio.VOANDO_VOLTA);
				busyWaitLoop(tempoVoo);
			}
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		finally {
			mudarEstado(EstadoPomboCorreio.MORTO);
		}
	}
	
	private void mudarEstado(EstadoPomboCorreio novoEstado) {
		for(PomboCorreioListener listener : listeners) {
			listener.mudancaEstado(this, novoEstado);
		}
		
		estadoAtual = novoEstado;
	}
	
	private void busyWaitLoop(int millis) throws InterruptedException {
		long current = System.currentTimeMillis();
		
		while(current + millis > System.currentTimeMillis()) {
			if(isInterrupted()) {
				throw new InterruptedException();
			}
		}
	}
	
	public void addPomboCorreioListener(PomboCorreioListener listener) {
		listeners.add(listener);
	}
	
	public EstadoPomboCorreio getEstadoAtual() {
		return estadoAtual;
	}
	
	public void setCaixaMensagens(CaixaMensagens caixaMensagens) {
		this.caixaMensagens = caixaMensagens;
	}
	
	public int getNumeroMensagens() {
		return numeroMensagens;
	}
	public void setNumeroMensagens(int numeroMensagens) {
		this.numeroMensagens = numeroMensagens;
	}
	public int getTempoCarga() {
		return tempoCarga;
	}
	public void setTempoCarga(int tempoCarga) {
		this.tempoCarga = tempoCarga;
	}
	public int getTempoVoo() {
		return tempoVoo;
	}
	public void setTempoVoo(int tempoVoo) {
		this.tempoVoo = tempoVoo;
	}
	public int getTempoDescarga() {
		return tempoDescarga;
	}
	public void setTempoDescarga(int tempoDescarga) {
		this.tempoDescarga = tempoDescarga;
	}
}
