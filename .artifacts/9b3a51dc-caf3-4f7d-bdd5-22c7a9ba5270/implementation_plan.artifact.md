# Plano de Implementação - Detalhes de Artigo na Pesquisa por Parâmetros

Este plano descreve as alterações necessárias para exibir os resultados da pesquisa por parâmetros de forma detalhada, utilizando caixas de texto (`OutlinedTextField`) para cada campo e uma borda azul ao redor de cada entrada.

## Alterações Propostas

### UI Componentes

#### [MODIFY] [SearchScreen.kt](file:///C:/Users/jmbga/AndroidStudioProjects/Lei_e_Ordem/app/src/main/java/com/example/lei_e_ordem/ui/SearchScreen.kt)

1.  **Adicionar Importações:**
    *   `androidx.compose.foundation.BorderStroke`
    *   `androidx.compose.ui.graphics.Color`

2.  **Criar Composable `ReadOnlyField`:**
    *   Um componente reutilizável que utiliza `OutlinedTextField` em modo `readOnly` para exibir o par rótulo/valor.

3.  **Criar Composable `ArtigoDetailedItem`:**
    *   Um componente que agrupa todos os campos de um `Artigo` dentro de uma `Surface` (ou `Card`) com uma borda azul (`PoliceBlue`).

4.  **Atualizar a lista de resultados da pesquisa por parâmetros:**
    *   Substituir o uso de `ArtigoLinkItem` por `ArtigoDetailedItem` quando a lista `infracoesPorArtigo` é exibida.

## Plano de Verificação

### Teste Manual
1.  Abrir a aplicação.
2.  Clicar em "Procura por parâmetros".
3.  Selecionar um Código (ex: "Infracões Código da Estrada").
4.  Selecionar um Artigo (ex: "Artigo 3º").
5.  Verificar se os resultados aparecem com:
    *   Uma borda azul ao redor de cada infração.
    *   Cada campo (Código, Artigo, Número, etc.) dentro de uma caixa de texto.
