package com.example.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object{
        private const val MARGIN_SIZE = 10        //dist between cards
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListener{
        fun onCardClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2* MARGIN_SIZE)//top and bottom margin
        val cardHeight = parent.height /boardSize.getHeight() - (2* MARGIN_SIZE)
        val cardSidelength: Int = min(cardWidth , cardHeight)

        val view = LayoutInflater.from(context).inflate(R.layout.memory_card , parent , false)

        val layoutParams = view.findViewById<CardView>(R.id.memCard).layoutParams as MarginLayoutParams
        layoutParams.width = cardSidelength
        layoutParams.height = cardSidelength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE , MARGIN_SIZE) //on all four sides
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return boardSize.numcards
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image_button = itemView.findViewById<ImageView>(R.id.imageView)

        fun bind(position: Int) {
            val memoryCard = cards[position]
            image_button.setImageResource(if (memoryCard.isFaceUp){memoryCard.identifier}else{R.drawable.card_image})

            image_button.alpha = if(memoryCard.isMatched) .4f else 1.0f
            val colorStateList = if(memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray)else null
            ViewCompat.setBackgroundTintList(image_button, colorStateList)

            image_button.setOnClickListener{
                Log.i(TAG , "u clicked on $position") //i = info
                cardClickListener.onCardClicked(position)
            }
        }
    }

}