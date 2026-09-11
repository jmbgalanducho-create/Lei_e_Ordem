package com.example.lei_e_ordem.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "artigos",
    foreignKeys = [
        ForeignKey(
            entity = Codigo::class,
            parentColumns = ["nome"],
            childColumns = ["codigo"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Gravidade::class,
            parentColumns = ["gravidade"],
            childColumns = ["gravidade"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["codigo"]),
        Index(value = ["gravidade"]),
        Index(value = ["artigo", "numero", "codigo_infracao"], unique = true)
    ]
)
data class Artigo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,
    val artigo: String,
    val numero: String,
    val gravidade: String,
    val coima: String,
    val descricao: String,
    val codigo_infracao: String
)
