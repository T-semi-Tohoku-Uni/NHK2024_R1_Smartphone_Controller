package com.example.nhk2024_r1_smartphone_controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: create debug console class

class ViewHolderList (item: View): RecyclerView.ViewHolder(item) {
    val characterList: TextView = item.findViewById(R.id.DebugText)
}

class RecyclerAdapter(
    private val list: MutableList<String>,
    private val recyclerView: RecyclerView
    ) : RecyclerView.Adapter<ViewHolderList>() {
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolderList {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.debug_console_recycler, parent, false)
        return ViewHolderList(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderList, position: Int) {
        holder.characterList.text = list[position]
    }

    override fun getItemCount(): Int = list.size

    fun addItemToDebugConsole(item: String) {
        if (list.size >= 10) {
            list.removeAt(0) // 最も古いアイテムを削除
            notifyItemRemoved(0)
        }
        list.add(item)
        notifyItemInserted(list.size - 1)
        recyclerView.scrollToPosition(list.size - 1)
        notifyDataSetChanged()
    }
}