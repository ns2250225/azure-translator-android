package com.example.azuretranslator

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.azuretranslator.databinding.ItemChatMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.tvOriginal.text = msg.originalText
            binding.tvTranslated.text = msg.translatedText
            binding.tvLangInfo.text = "${msg.sourceLang} -> ${msg.targetLang}"

            val params = binding.layoutBubble.layoutParams as LinearLayout.LayoutParams
            val context = binding.root.context

            if (msg.isFromMe) {
                // Right side (Greenish)
                params.gravity = Gravity.END
                binding.layoutBubble.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_bubble_me))
            } else {
                // Left side (Gray)
                params.gravity = Gravity.START
                binding.layoutBubble.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_bubble_other))
            }
            binding.layoutBubble.layoutParams = params
        }
    }
}
