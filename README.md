# design-pattern-detector
Ferramenta de análise estática para detecção estrutural de padrões de projeto em Java — SI405

## O que faz
Recebe um arquivo `.java` e detecta estruturalmente (sem depender de nomes de classes, 
métodos ou variáveis) se algum dos seguintes padrões está presente:
- Singleton
- Observer
- Decorator

Para cada padrão detectado, mostra os elementos que caracterizaram a detecção, uma 
vantagem e um risco específicos do código analisado.

## Como rodar
\`\`\`bash
mvn clean compile
mvn exec:java "-Dexec.mainClass=detector.Main" "-Dexec.args=exemplos/NomeDoArquivo.java"
\`\`\`

## Documentação
As heurísticas usadas por cada detector estão documentadas em [`docs/heuristica.md`](docs/heuristica.md).

## Exemplos
A pasta [`exemplos/`](exemplos) contém arquivos de teste, incluindo um caso com nomes 
genéricos (`C.java`) para comprovar que a detecção é estrutural.