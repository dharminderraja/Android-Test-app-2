package com.example.yumemitest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listitem.view.*
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.text.method.TextKeyListener.clear





class TodoListAdapter(var items: MutableList<Todo>, val context: Context) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.todoText.text = items[position].content
        holder.todoCheckBox.isChecked = items[position].status as Boolean

        if (items[position].status == true)
        {
            holder.todoText.paintFlags = holder.todoText.getPaintFlags() or STRIKE_THRU_TEXT_FLAG
        }

        holder.todoCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                holder.todoText.paintFlags = holder.todoText.getPaintFlags() or STRIKE_THRU_TEXT_FLAG
                (context as MainActivity).updateStatus(position,compoundButton.isChecked)
            } else {
                holder.todoText.paintFlags = holder.todoText.getPaintFlags() and STRIKE_THRU_TEXT_FLAG.inv()
                (context as MainActivity).updateStatus(position,compoundButton.isChecked)
            }
        }

        holder.todoDelete.setOnClickListener {
            (context as MainActivity).deleteTodo(items[position])
            items.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val todoText = view.todotext
        val todoCheckBox = view.todocheckbox
        val todoDelete = view.tododelete
    }

    fun updateData(newData: MutableList<Todo>) {
        this.items.clear()
        this.items = newData
        this.notifyDataSetChanged()
    }

}