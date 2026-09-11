package com.example.lei_e_ordem.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodigo(codigo: Codigo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtigo(artigo: Artigo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtigos(artigos: List<Artigo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGravidade(gravidade: Gravidade)

    @Query("SELECT * FROM codigos")
    fun getAllCodigos(): Flow<List<Codigo>>

    @Query("SELECT * FROM artigos")
    fun getAllArtigos(): Flow<List<Artigo>>

    @Query("SELECT * FROM gravidade")
    fun getAllGravidades(): Flow<List<Gravidade>>

    @Query("SELECT * FROM artigos WHERE codigo = :codigoNome")
    fun getArtigosByCodigo(codigoNome: String): Flow<List<Artigo>>

    @Query("SELECT DISTINCT artigo FROM artigos WHERE codigo = :codigoNome ORDER BY id ASC")
    fun getDistinctArtigoNamesByCodigo(codigoNome: String): Flow<List<String>>

    @Query("SELECT * FROM artigos WHERE codigo = :codigoNome AND artigo = :artigoNome")
    fun getArtigosByCodigoAndNome(codigoNome: String, artigoNome: String): Flow<List<Artigo>>

    @Query("""
        SELECT * FROM artigos 
        WHERE codigo LIKE '%' || :query || '%' 
        OR artigo LIKE '%' || :query || '%' 
        OR numero LIKE '%' || :query || '%' 
        OR gravidade LIKE '%' || :query || '%' 
        OR coima LIKE '%' || :query || '%' 
        OR descricao LIKE '%' || :query || '%' 
        OR codigo_infracao LIKE '%' || :query || '%'
    """)
    fun searchArtigos(query: String): Flow<List<Artigo>>

    @Query("DELETE FROM artigos")
    suspend fun deleteAllArtigos()
}
