package com.example.todoapp
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.LinearLayout


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
class TodoAdapter(private var list: MutableList<TodoModel> = mutableListOf()) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo2, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    // Update the list without requiring a MutableList input
    fun updateTasks(newList: List<TodoModel>) {
        list.clear()
        list.addAll(newList)  // `newList` can be List<TodoModel>
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.txtShowTitle)
        private val subtitle: TextView = itemView.findViewById(R.id.txtShowTask)
        private val category: TextView = itemView.findViewById(R.id.txtShowCategory)
        private val date: TextView = itemView.findViewById(R.id.txtShowDate)
        private val time: TextView = itemView.findViewById(R.id.txtShowTime)
        private val checkBoxContainer: LinearLayout = itemView.findViewById(R.id.checkBoxContainer)

        fun bind(todoModel: TodoModel) {
            title.text = todoModel.title
            subtitle.text = todoModel.description
            category.text = todoModel.category
            date.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(todoModel.date)
            time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(todoModel.time)

            checkBoxContainer.removeAllViews()

            val allCheckboxes = todoModel.checkboxes.split(",").map { it.trim() }
            val checkedTasks = todoModel.tasks.split(",").map { it.trim() }.toMutableList()

            for (checkboxLabel in allCheckboxes) {
                val checkBox = CheckBox(itemView.context)
                checkBox.text = checkboxLabel
                checkBox.isChecked = checkedTasks.contains(checkboxLabel)

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (!checkedTasks.contains(checkboxLabel)) {
                            checkedTasks.add(checkboxLabel)
                        }
                    } else {
                        checkedTasks.remove(checkboxLabel)
                    }

                    todoModel.tasks = checkedTasks.joinToString(",")

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(itemView.context).todoDao().updateTask(todoModel)
                        CoroutineScope(Dispatchers.Main).launch {
                            notifyItemChanged(adapterPosition)
                        }
                    }
                }

                checkBoxContainer.addView(checkBox)
            }
        }
    }
}
