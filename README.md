# academico-so-pombocorreio

Projeto da cadeira de S.O que simula um problema de produtor-consumidor, utilizando pombos, usuários e uma caixa de mensagens.

# API do Javascript
- criarUsuario (int) : long
- criarPombo(int, int, int, int)
- iniciar(int)
- parar()
- matarUsuario(int)
- matarPombo()

# Entidades
- Usuário  (Produz mensagens e insere na caixa de mensagens. Pode ter vários)
- Pombo    (Consome mensagens da caixa de mensagens. Só pode haver um.)
- Caixa de mensagens   (Armazena mensagens)

# Estados do pombo
Morto, Bloqueado, Carregando, Voando ida, Descarregando, Voando volta

# Estados do usuário
Morto, Bloqueado, Escrevendo
