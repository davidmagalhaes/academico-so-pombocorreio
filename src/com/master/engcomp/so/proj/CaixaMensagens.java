package com.master.engcomp.so.proj;

import java.util.List;
import java.util.concurrent.Semaphore;

public class CaixaMensagens {
	private Semaphore mensagens;
	private Semaphore capacidade;
	
	private List<CaixaMensagensListener> listeners;
	
	public CaixaMensagens(int maximoMensagens) {
		mensagens = new Semaphore(0);
		capacidade = new Semaphore(maximoMensagens);
	}
	
	public void pegarMensagens(int numeroMensagens) throws InterruptedException {
		mensagens.acquire(numeroMensagens);	
		capacidade.release(numeroMensagens);
		
		for(CaixaMensagensListener listener : listeners) {
			listener.mensagensConsumidas(numeroMensagens);
		}
	}
	
	public void inserirMensagem() throws InterruptedException {
		capacidade.acquire();
		mensagens.release();
		
		for(CaixaMensagensListener listener : listeners) {
			listener.mensagemInserida();
		}
	}
	
	public int getCapacidadeAtual() {
		return capacidade.availablePermits();
	}
	
	public int getNumeroMensagens() {
		return mensagens.availablePermits();
	}
	
	public void addCaixaMensagensListener(CaixaMensagensListener listener) {
		this.listeners.add(listener);
	}
}
