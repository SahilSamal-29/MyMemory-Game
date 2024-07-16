package com.example.mymemory.models

import com.example.mymemory.Utils.DEFAULT_ICONS

class MemoryGame(private val boardSize:BoardSize){
    val cards:List<MemoryCard>
    var numPairsFound: Int = 0

    private var numCardFlips:Int = 0
    private var indexOfSingleSelectedCard:Int? = null

    init{
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.numPairs())
        val randomizedImages = (chosenImages+chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }
    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card : MemoryCard = cards[position]
        var foundMatch = false
        //Three cases:
        //0 cards previously flipped over => flip over / restore cards + flip over <- both cases are same
        //1 card previously flipped over => flip over + check if images match
        //2 cards previously flipped over =>if no match => restore cards + flip over
        if(indexOfSingleSelectedCard == null){
            //0 or 2 cards previously flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            //exactly 1 card previously flipped over
            foundMatch = checkForMatch(indexOfSingleSelectedCard!! , position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int):Boolean {
        if (cards[position1].identifier != cards[position2].identifier){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(cards in cards){
            if (!cards.isMatched) {
                cards.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.numPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips/2
    }
}