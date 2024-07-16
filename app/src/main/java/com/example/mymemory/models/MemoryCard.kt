package com.example.mymemory.models

data class MemoryCard(
    var identifier :Int ,
    var isFaceUp:Boolean = false ,
    var isMatched:Boolean = false
)
