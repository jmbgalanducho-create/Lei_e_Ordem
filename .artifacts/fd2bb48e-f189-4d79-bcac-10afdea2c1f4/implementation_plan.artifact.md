# Plano de Implementação da Base de Dados (Room)

Este plano descreve a configuração de uma base de dados local utilizando a biblioteca Room (o padrão para Android) para gerir as tabelas `codigos` e `artigos`. Embora tenha mencionado MySQL, em Android utilizamos normalmente o Room para armazenamento local. Se precisar de MySQL remoto, seria necessário um servidor externo e uma API.

## Propostas de Mudanças

### Configuração de Dependências
Adicionar as bibliotecas do Room ao projeto.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/gradle/libs.versions.toml)
#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/build.gradle.kts)

---

### Camada de Dados (Entidades e DAOs)

#### [NEW] [Codigo.kt](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/src/main/java/com/example/lei_e_ordem/data/Codigo.kt)
Define a tabela `codigos` com o campo `nome` como chave primária.

#### [NEW] [Artigo.kt](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/src/main/java/com/example/lei_e_ordem/data/Artigo.kt)
Define a tabela `artigos` com os campos solicitados e uma chave estrangeira ligada à tabela `codigos`.
Campos: `id`, `codigo` (FK), `artigo`, `numero`, `gravidade`, `coime`, `descricao`, `codigo_infracao`.

#### [NEW] [AppDao.kt](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/src/main/java/com/example/lei_e_ordem/data/AppDao.kt)
Interface para as operações de inserção e consulta nas tabelas.

#### [NEW] [AppDatabase.kt](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/src/main/java/com/example/lei_e_ordem/data/AppDatabase.kt)
Configuração principal da base de dados Room.

## Plano de Verificação

### Testes Automatizados
- Criar um teste de unidade para verificar a criação das tabelas e a relação de chave estrangeira.

### Verificação Manual
- Instanciar a base de dados na `MainActivity` para testar uma inserção simples e verificar se não ocorrem erros de compilação ou runtime.
