package com.master.engcomp.so.proj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 
 * Classe que representa uma caixa de mensagens, que interage com usuários e com o pombo correio
 * Possui os semaforos e bloqueia threads.
 *
 */
public class CaixaMensagens {
	private Semaphore mensagens;
	private Semaphore capacidade;
	
	private List<CaixaMensagensListener> listeners = new ArrayList<>();
	
	public CaixaMensagens(int maximoMensagens) {
		mensagens  = new Semaphore(0);
		capacidade = new Semaphore(maximoMensagens);
	}
	
	/**
	 * Pega <code>numeroMensagens</code> mensagens da caixa de mensagens.
	 * Bloqueia a thread caso a caixa não tenha o número de mensagens requisitado no momento.
	 * 
	 * @param numeroMensagens número de mensagens para serem retiradas da caixa
	 * @throws InterruptedException caso a thread seja morta
	 */
	public void pegarMensagens(int numeroMensagens) throws InterruptedException {
		mensagens.acquire(numeroMensagens);	
		capacidade.release(numeroMensagens);
		
		for(CaixaMensagensListener listener : listeners) {
			listener.mensagensConsumidas(numeroMensagens);
		}
	}
	
	/**
	 * Insere uma mensagem na caixa de mensagens. Bloqueia a thread 
	 * caso a caixa esteja cheia.
	 * 
	 * @throws InterruptedException caso a thread seja morta
	 */
	public void inserirMensagem() throws InterruptedException {
		capacidade.acquire(1);
		mensagens.release(1);
		
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
