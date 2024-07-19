package com.example.mymemory

import android.annotation.SuppressLint
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.graphics.Paint
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.example.mymemory.databinding.ActivityMainBinding
import com.example.mymemory.models.BoardSize.*
import com.example.mymemory.models.MemoryGame
import com.google.android.material.snackbar.Snackbar
import com.example.mymemory.models.BoardSize as BoardSize

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var binding: ActivityMainBinding

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private var boardSize: BoardSize = EASY
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
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))

        rvBoard = binding.rvBoards
        setupBoard()
        binding.btnRefresh.setOnClickListener {
            binding.btnRefresh.paintFlags = binding.btnRefresh.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if(!memoryGame.haveWonGame() && memoryGame.getNumMoves() > 0){
                //alert dialog
                showAlertDialog("Quit your current game?", null , View.OnClickListener {
                    setupBoard()
                })
                }else{
                    setupBoard()
            }
        }
        binding.tvMode.setOnClickListener {
            binding.tvMode.paintFlags = binding.tvMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            showNewSizeDialog()
        }
    }

    private fun showNewSizeDialog() {
        val boardSizeView = layoutInflater.inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            EASY -> radioGroupSize.check(R.id.rbEasy)
            MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            //set new value for board size
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD

            }
            setupBoard()
        })
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "RestrictedApi")
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
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.numPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.numPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(binding.main, "You won! Congratulations.", Snackbar.LENGTH_SHORT).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    private fun setupBoard() {
        when(boardSize){
            EASY -> {
                tvNumMoves.text = "Easy: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            MEDIUM -> {
                tvNumMoves.text = "Med: 6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            HARD -> {
                tvNumMoves.text = "Hard: 6 x 4"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        memoryGame = MemoryGame(boardSize)
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

    private fun showAlertDialog(Title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(Title)
            .setView(view)
            .setNegativeButton("CANCEL", null )
            .setPositiveButton("OK"){_, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }
}
