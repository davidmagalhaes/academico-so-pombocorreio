package com.master.engcomp.so.proj;

import java.util.ArrayList;
import java.util.List;

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
				mudarEstado(EstadoPomboCorreio.BLOQUEADO);
				caixaMensagens.pegarMensagens(numeroMensagens);
				
				mudarEstado(EstadoPomboCorreio.CARREGANDO);
				sleep(tempoCarga);
				
				mudarEstado(EstadoPomboCorreio.VOANDO_IDA);
				sleep(tempoVoo);
				
				mudarEstado(EstadoPomboCorreio.DESCARREGANDO);
				sleep(tempoDescarga);
				
				mudarEstado(EstadoPomboCorreio.VOANDO_VOLTA);
				sleep(tempoVoo);
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
