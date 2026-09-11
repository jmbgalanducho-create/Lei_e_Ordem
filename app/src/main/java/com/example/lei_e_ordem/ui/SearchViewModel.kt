package com.example.lei_e_ordem.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lei_e_ordem.data.AppDao
import com.example.lei_e_ordem.data.Artigo
import com.example.lei_e_ordem.data.Codigo
import com.example.lei_e_ordem.data.Gravidade
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class SearchViewModel(private val appDao: AppDao) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lista de todos os códigos
    val codigos: StateFlow<List<Codigo>> = appDao.getAllCodigos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado da seleção do código
    private val _selectedCodigo = MutableStateFlow<String?>(null)
    val selectedCodigo: StateFlow<String?> = _selectedCodigo.asStateFlow()

    // Estado da seleção do nome do artigo (ex: "Artigo 13º")
    private val _selectedArtigoNome = MutableStateFlow<String?>(null)
    val selectedArtigoNome: StateFlow<String?> = _selectedArtigoNome.asStateFlow()

    // Estado para o artigo em detalhe (vista de artigo)
    private val _selectedArtigoForDetail = MutableStateFlow<Artigo?>(null)
    val selectedArtigoForDetail: StateFlow<Artigo?> = _selectedArtigoForDetail.asStateFlow()

    // Pesquisa simples
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchTrigger = MutableSharedFlow<Unit>(replay = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Artigo>> = _searchTrigger
        .flatMapLatest {
            val query = _searchQuery.value
            if (query.length < 2) flowOf(emptyList())
            else appDao.searchArtigos(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 1. Lista de nomes de artigos (DISTINCT) para o dropdown
    @OptIn(ExperimentalCoroutinesApi::class)
    val nomesArtigosFiltrados: StateFlow<List<String>> = _selectedCodigo
        .flatMapLatest { codigo ->
            if (codigo == null) flowOf(emptyList())
            else appDao.getDistinctArtigoNamesByCodigo(codigo)
        }
        .onEach { lista ->
            Log.d("SearchViewModel", "Artigos encontrados para o código ${_selectedCodigo.value}: ${lista.size}")
            lista.take(5).forEach { Log.d("SearchViewModel", "Exemplo de artigo: $it") }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Lista final de infrações (artigos) após selecionar o nome do artigo
    @OptIn(ExperimentalCoroutinesApi::class)
    val infracoesPorArtigo: StateFlow<List<Artigo>> = combine(_selectedCodigo, _selectedArtigoNome) { codigo, nome ->
        Pair(codigo, nome)
    }.flatMapLatest { (codigo, nome) ->
        if (codigo == null || nome == null) flowOf(emptyList())
        else appDao.getArtigosByCodigoAndNome(codigo, nome)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            viewModelScope.launch { _searchTrigger.emit(Unit) }
        }
    }

    fun performSearch() {
        viewModelScope.launch {
            _searchTrigger.emit(Unit)
        }
    }

    fun onCodigoSelected(codigoNome: String) {
        Log.d("SearchViewModel", "Código selecionado: $codigoNome")
        _selectedCodigo.value = codigoNome
        _selectedArtigoNome.value = null // Resetar seleção seguinte
    }

    fun onArtigoNomeSelected(artigoNome: String) {
        _selectedArtigoNome.value = artigoNome
    }

    fun onArtigoClicked(artigo: Artigo) {
        _selectedArtigoForDetail.value = artigo
    }

    fun clearSelectedArtigo() {
        _selectedArtigoForDetail.value = null
    }

    fun checkAndPopulateData(context: Context) {
        viewModelScope.launch {
            val artigosExistentes = appDao.getAllArtigos().first()
            Log.d("SearchViewModel", "Total de artigos na DB: ${artigosExistentes.size}")
            if (artigosExistentes.isNotEmpty()) {
                val primeiros3 = artigosExistentes.take(3)
                primeiros3.forEach { Log.d("SearchViewModel", "Artigo na DB: ${it.artigo} ${it.numero} - ${it.codigo}") }
            }
            
            // Se a tabela de artigos estiver vazia, carrega os dados
            if (artigosExistentes.isEmpty()) {
                _isLoading.value = true
                withContext(Dispatchers.IO) {
                    try {
                        appDao.insertCodigo(Codigo(nome = "Infracões Código da Estrada"))
                        appDao.insertCodigo(Codigo(nome = "Código Penal"))
                        appDao.insertGravidade(Gravidade(gravidade = "LEVE"))
                        appDao.insertGravidade(Gravidade(gravidade = "GRAVE"))
                        appDao.insertGravidade(Gravidade(gravidade = "MUITO GRAVE"))

                        val inputStream = context.assets.open("artigos.json")
                        val reader = InputStreamReader(inputStream)
                        val listType = object : TypeToken<List<Artigo>>() {}.type
                        val artigos: List<Artigo> = Gson().fromJson(reader, listType)
                        
                        val formattedArtigos = artigos.map { artigo ->
                            val formattedArtigo = if (artigo.artigo.startsWith("Artigo")) {
                                artigo.artigo
                            } else {
                                "Artigo ${artigo.artigo}º"
                            }
                            artigo.copy(artigo = formattedArtigo)
                        }
                        
                        appDao.insertArtigos(formattedArtigos)
                        Log.d("SearchViewModel", "Sucesso ao inserir ${formattedArtigos.size} artigos.")
                        reader.close()
                    } catch (e: Exception) {
                        Log.e("SearchViewModel", "Erro ao popular dados", e)
                    }
                }
                _isLoading.value = false
            } else {
                // Se não estiver vazio, verifica se o "Código Penal" já existe, se não, adiciona
                val codigos = appDao.getAllCodigos().first()
                if (codigos.none { it.nome == "Código Penal" }) {
                    withContext(Dispatchers.IO) {
                        appDao.insertCodigo(Codigo(nome = "Código Penal"))
                    }
                }
            }
        }
    }
}
