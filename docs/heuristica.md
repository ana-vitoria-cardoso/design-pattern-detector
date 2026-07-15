# Heurísticas Documentadas

## 1. Singleton (DetectorSingleton)

**O que eu observei no código e por quê:**

- Se os construtores da classe são privados, ou pelo menos não são públicos. Faz
  sentido porque, se qualquer um pudesse chamar `new`, a classe não estaria
  controlando quantas instâncias existem.
- Se existe um campo estático do mesmo tipo da própria classe. Que é o "lugar" onde
  a única instância fica guardada.
- Se existe um método estático e público que devolve esse mesmo tipo. É por ele que
  o resto do programa consegue pegar a instância.

Eu não uso o nome do campo nem do método para decidir nada, só o tipo e os
modificadores (`private`, `static`, `public`).

**Quais combinações de elementos configuram o padrão:**

Os elementos são quando esses quatro passos são identificados ao mesmo tempo: a
classe tem construtor, nenhum construtor é público, tem um campo estático do tipo
da própria classe, e tem um método estático público que retorna esse tipo. Se
faltar qualquer uma dessas condições, eu não considero Singleton.

---

## 2. Observer (DetectorObserver)

**O que eu observei no código e por quê:**

- Um campo do tipo lista/coleção (`List`, `Set`, etc.) que guarda outro tipo dentro
  (tipo genérico). Esse é o "grupo de observadores".
- Um método que dá `.add()` nesse campo, ou seja, adiciona alguém na lista. Isso é o
  jeito de "se inscrever".
- Um método que percorre essa lista com um `for` e, dentro do `for`, chama algum
  método no item da lista. Isso é o jeito de "avisar todo mundo".

Eu observei esses três passos, porque é o mínimo necessário pra dizer que essa
classe guarda uma lista de alguém e avisa todo mundo quando algo acontece, que é a
ideia central do Observer, sem depender de nomes como `observers` ou `listeners`.

**Quais combinações de elementos configuram o padrão:**

Os elementos são o campo de coleção, e um método que adiciona nele, e outro método
(ou o mesmo) que percorre esse campo chamando algo em cada elemento. Se só tiver a
lista guardada mas nunca for percorrida chamando o método, eu não considero
Observer.

---

## 3. Decorator (DetectorDecorator)

**O que eu observei no código e por quê:**

- A classe implementa uma interface.
- Ela tem um campo do mesmo tipo dessa interface (ou seja, guarda uma referência do
  mesmo "tipo" que ela mesma é).
- Esse campo é recebido no construtor (é passado de fora, não é criado dentro da
  classe com `new`).
- Algum método da classe chama um método nesse campo, tipo `campo.metodo()`.

Eu observei isso porque, se a classe recebe "algo do mesmo tipo que ela é" e usa
isso para responder suas próprias chamadas, ela está "embrulhando" outro objeto e
adicionando um comportamento a mais, que é exatamente o que o Decorator faz.

**Quais combinações de elementos configuram o padrão:**

Os elementos são: a classe implementar uma interface, e ter um campo desse mesmo
tipo, esse campo vem de um parâmetro do construtor, e pelo menos um método chama
algo nesse campo. Só implementar a interface sem guardar e usar uma referência do
mesmo tipo não conta.
