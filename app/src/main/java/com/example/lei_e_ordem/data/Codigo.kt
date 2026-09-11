package com.example.lei_e_ordem.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "codigos",
    indices = [Index(value = ["nome"], unique = true)]
)
data class Codigo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String
)
