package com.puntogris.blint.ui.help

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.HelpVhBinding
import com.puntogris.blint.model.Question

class HelpViewHolder private constructor(private val binding: HelpVhBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(question: Question) {
        binding.question = question
        binding.questionText.setOnClickListener { binding.questionExpandable.toggle() }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): HelpViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HelpVhBinding.inflate(layoutInflater,parent, false)
            return HelpViewHolder(binding)
        }
    }
}