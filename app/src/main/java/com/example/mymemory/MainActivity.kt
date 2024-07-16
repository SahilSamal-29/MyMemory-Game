package com.example.mymemory

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.databinding.ActivityMainBinding
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var binding: ActivityMainBinding

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private var boardSize: BoardSize = BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvNumMoves = binding.tvMoves
        tvNumPairs = binding.tvPairs

        memoryGame = MemoryGame(boardSize)

        rvBoard = binding.rvBoards
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards,
            object : MemoryBoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }
            })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

        rvBoard.setHasFixedSize(true)
        binding.rvBoards.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateGameWithFlip(position: Int) {
        //error handling
        if (memoryGame.haveWonGame()) {
            //alert the user of an invalid move
            Snackbar.make(binding.main, "You already won!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            //alert the user of an invalid move
            Snackbar.make(binding.main, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        //actually flip over the card
        if (memoryGame.flipCard(position)) {
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.numPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(binding.main, "You won! Congratulations.", Snackbar.LENGTH_SHORT).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}