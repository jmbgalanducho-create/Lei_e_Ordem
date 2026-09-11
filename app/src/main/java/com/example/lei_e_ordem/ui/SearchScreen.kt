package com.example.lei_e_ordem.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.lei_e_ordem.data.Artigo
import com.example.lei_e_ordem.ui.theme.PoliceBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var isParameterSearch by remember { mutableStateOf(false) }
    var expandedArtigoDropdown by remember { mutableStateOf(false) }
    var artigoFilterQuery by remember { mutableStateOf("") }
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    
    val codigos by viewModel.codigos.collectAsState()
    val selectedCodigo by viewModel.selectedCodigo.collectAsState()
    
    val nomesArtigosFiltrados by viewModel.nomesArtigosFiltrados.collectAsState()
    val selectedArtigoNome by viewModel.selectedArtigoNome.collectAsState()
    val infracoesPorArtigo by viewModel.infracoesPorArtigo.collectAsState()
    
    val selectedArtigoForDetail by viewModel.selectedArtigoForDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Lidar com o botão de retroceder do sistema
    BackHandler(enabled = selectedArtigoForDetail != null || isParameterSearch || expandedArtigoDropdown) {
        if (expandedArtigoDropdown) {
            expandedArtigoDropdown = false
        } else if (selectedArtigoForDetail != null) {
            viewModel.clearSelectedArtigo()
        } else {
            isParameterSearch = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = PoliceBlue)
                Spacer(modifier = Modifier.height(16.dp))
                Text("A carregar infrações...", color = PoliceBlue)
            }
        }
    } else if (selectedArtigoForDetail != null) {
        ArtigoDetailScreen(
            artigo = selectedArtigoForDetail!!,
            onBack = { viewModel.clearSelectedArtigo() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Botão Pesquisa Avançada no topo se houver texto
            if (!isParameterSearch && searchQuery.isNotEmpty()) {
                MainActionButton(
                    text = "Procura por parâmetros",
                    onClick = { isParameterSearch = true }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!isParameterSearch) {
                // Pesquisa Simples
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = PoliceBlue,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.performSearch() })
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.performSearch() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PoliceBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Procurar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(searchResults) { artigo ->
                        ArtigoLinkItem(artigo = artigo, onClick = { viewModel.onArtigoClicked(artigo) })
                    }
                }
            } else {
                // Pesquisa por Parâmetros
                Text(
                    text = "Procura por Parâmetros",
                    style = MaterialTheme.typography.titleLarge,
                    color = PoliceBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    // 1. Dropdown de Código
                    var expandedCodigo by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCodigo,
                        onExpandedChange = { expandedCodigo = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedCodigo ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Código") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCodigo) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCodigo,
                            onDismissRequest = { expandedCodigo = false }
                        ) {
                            codigos.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.nome) },
                                    onClick = {
                                        viewModel.onCodigoSelected(item.nome)
                                        expandedCodigo = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Seletor de Artigo (Sempre visível, mas desativado até selecionar o código)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedArtigoNome ?: "",
                            onValueChange = { artigoFilterQuery = it },
                            label = { Text("Artigo") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(if (selectedCodigo != null) "Selecione ou pesquise o artigo..." else "Selecione primeiro o código") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { if (selectedCodigo != null) expandedArtigoDropdown = !expandedArtigoDropdown },
                                    enabled = selectedCodigo != null
                                ) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            readOnly = true,
                            enabled = selectedCodigo != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PoliceBlue,
                                unfocusedBorderColor = Color.Gray,
                                disabledBorderColor = Color.LightGray,
                                disabledLabelColor = Color.Gray,
                                disabledTextColor = Color.Gray
                            ),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release && selectedCodigo != null) {
                                                expandedArtigoDropdown = true
                                            }
                                        }
                                    }
                                }
                        )

                        if (expandedArtigoDropdown && selectedCodigo != null) {
                            Popup(
                                alignment = Alignment.TopStart,
                                onDismissRequest = { expandedArtigoDropdown = false },
                                properties = PopupProperties(focusable = true)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .heightIn(max = 300.dp),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Column {
                                        TextField(
                                            value = artigoFilterQuery,
                                            onValueChange = { artigoFilterQuery = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("Filtrar artigos...") },
                                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent
                                            )
                                        )
                                        
                                        val filteredList = nomesArtigosFiltrados.filter {
                                            it.contains(artigoFilterQuery, ignoreCase = true)
                                        }

                                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                            items(filteredList) { nome ->
                                                ListItem(
                                                    headlineContent = { Text(nome) },
                                                    modifier = Modifier.clickable {
                                                        viewModel.onArtigoNomeSelected(nome)
                                                        expandedArtigoDropdown = false
                                                        artigoFilterQuery = ""
                                                    }
                                                )
                                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Lista final de infrações
                    if (selectedArtigoNome != null) {
                        Text(
                            text = "Infrações encontradas:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(infracoesPorArtigo) { infração ->
                                ArtigoDetailedItem(artigo = infração)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }

            // Botão "Pesquisa Avançada" no fundo apenas se o campo estiver vazio
            if (!isParameterSearch && searchQuery.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                MainActionButton(
                    text = "Procura por parâmetros",
                    onClick = { isParameterSearch = true }
                )
            }

            if (isParameterSearch) {
                TextButton(onClick = { isParameterSearch = false }) {
                    Text("Voltar para pesquisa simples", color = PoliceBlue)
                }
            }
        }
    }
}

@Composable
fun MainActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = PoliceBlue,
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}

@Composable
fun ArtigoLinkItem(artigo: Artigo, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        color = Color.Transparent
    ) {
        Column {
            Text(
                text = "${artigo.artigo} - ${artigo.numero}",
                color = PoliceBlue,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = artigo.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ArtigoDetailedItem(artigo: Artigo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, PoliceBlue),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            ReadOnlyField(label = "Código", value = artigo.codigo)
            ReadOnlyField(label = "Artigo", value = artigo.artigo)
            ReadOnlyField(label = "Número", value = artigo.numero)
            ReadOnlyField(label = "Gravidade", value = artigo.gravidade)
            ReadOnlyField(label = "Coima", value = artigo.coima)
            ReadOnlyField(label = "Descrição", value = artigo.descricao)
            ReadOnlyField(label = "Código de Infração", value = artigo.codigo_infracao)
        }
    }
}

@Composable
fun ReadOnlyField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PoliceBlue.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = PoliceBlue,
            unfocusedLabelColor = Color.Gray
        )
    )
}
