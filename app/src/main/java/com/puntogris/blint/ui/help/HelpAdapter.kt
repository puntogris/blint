package com.puntogris.blint.ui.help

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.model.Question

class HelpAdapter : RecyclerView.Adapter<HelpViewHolder>() {

    private val questions = listOf(
        Question(R.string.question1, R.string.answer1),
        Question(R.string.question2, R.string.answer2),
        Question(R.string.question3, R.string.answer3),
        Question(R.string.question4, R.string.answer4),
        Question(R.string.question5, R.string.answer5),
        Question(R.string.question6, R.string.answer6),
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        return HelpViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount() = questions.size
}