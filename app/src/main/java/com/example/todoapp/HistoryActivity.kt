package com.example.todoapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoAdapter()
        recyclerView.adapter = adapter

        val exportButton = findViewById<Button>(R.id.exportButton)
        exportButton.setOnClickListener {
            // Open a directory chooser to save the CSV
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, "task_history.csv")
            }
            startActivityForResult(intent, REQUEST_CODE_SAVE_CSV)
        }

        val clearHistoryButton = findViewById<Button>(R.id.btnClearHistory)
        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        loadHistory()
    }

    private fun loadHistory() {
        db.todoDao().getFinishedTasks().observe(this, { tasks ->
            if (tasks.isNotEmpty()) {
                adapter.updateTasks(tasks)
            } else {
                Toast.makeText(this, "No finished tasks to display", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearHistory() {
        lifecycleScope.launch {
            db.todoDao().clearHistory()  // Assuming you have a method to clear tasks from the DB
            loadHistory()
            Toast.makeText(this@HistoryActivity, "History cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToCSV(tasks: List<TodoModel>, uri: Uri) {
        try {
            // Open an output stream to the selected file
            contentResolver.openOutputStream(uri)?.let { outputStream ->
                val writer = BufferedWriter(OutputStreamWriter(outputStream))

                // Write the header
                writer.write("Title,Description,Category,Date,Time,Checkbox Name,Status\n")

                // Write the tasks data
                for (task in tasks) {
                    val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(task.date)
                    val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(task.time)

                    // Parse checkbox names and checked items
                    val checkboxNames = task.checkboxes.split(",") // Split checkbox names into list
                    val checkedItems = task.tasks.split(",") // Split checked tasks into list

                    // For each checkbox, determine if it was checked or unchecked
                    for (checkbox in checkboxNames) {
                        val status = if (checkedItems.contains(checkbox)) "Checked" else "Unchecked"
                        // Write the task data along with checkbox name and status
                        val row = "${task.title},${task.description},${task.category},$dateFormatted,$timeFormatted,$checkbox,$status\n"
                        writer.write(row)
                    }
                }

                // Close the writer
                writer.close()
                Toast.makeText(this@HistoryActivity, "History exported to CSV", Toast.LENGTH_SHORT).show()

            } ?: run {
                Toast.makeText(this, "Failed to export history", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export history", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SAVE_CSV && resultCode == Activity.RESULT_OK) {
            // Get the URI where the file will be saved
            val uri = data?.data ?: return

            // Fetch the finished tasks and export them to the selected file
            db.todoDao().getFinishedTasks().observe(this, { tasks ->
                if (tasks.isNotEmpty()) {
                    exportToCSV(tasks, uri)
                } else {
                    Toast.makeText(this, "No finished tasks to export", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        private const val REQUEST_CODE_SAVE_CSV = 1
    }
}
