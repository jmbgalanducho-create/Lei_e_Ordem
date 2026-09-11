package com.example.lei_e_ordem.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gravidade",
    indices = [Index(value = ["gravidade"], unique = true)]
)
data class Gravidade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gravidade: String
)
