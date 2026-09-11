package com.example.lei_e_ordem.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lei_e_ordem.data.Artigo
import com.example.lei_e_ordem.ui.theme.PoliceBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtigoDetailScreen(artigo: Artigo, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Artigo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PoliceBlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DetailItem(label = "Código", value = artigo.codigo)
            DetailItem(label = "Artigo", value = artigo.artigo)
            DetailItem(label = "Número", value = artigo.numero)
            DetailItem(label = "Gravidade", value = artigo.gravidade)
            DetailItem(label = "Coima", value = artigo.coima)
            DetailItem(label = "Descrição", value = artigo.descricao)
            DetailItem(label = "Código de Infração", value = artigo.codigo_infracao)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = PoliceBlue,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))
    }
}
