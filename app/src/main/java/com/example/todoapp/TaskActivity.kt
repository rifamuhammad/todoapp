package com.example.todoapp
import android.widget.Toast
import android.widget.TimePicker
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_task2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myCalendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private var finalDate = 0L
    private var finalTime = 0L

    private val labels = arrayListOf("Radiologi", "UGD", "Operasi", "Rawat inap", "ICU")

    private val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task2)

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        setUpSpinner()

        // Set up the button to add new checkboxes
        addCheckboxButton.setOnClickListener {
            addNewCheckbox()
        }
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        labels.sort()
        spinnerCategory.adapter = adapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> {
                setListener()
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()
            }
        }
    }

    private fun addNewCheckbox() {
        val checkboxLabelInputLayout = findViewById<TextInputLayout>(R.id.checkboxLabelInput)
        val checkboxLabelEditText = checkboxLabelInputLayout.editText // Get the TextInputEditText

        checkboxLabelEditText?.let {
            val checkboxText = it.text.toString()
            if (checkboxText.isNotEmpty()) {
                // Check for duplicates before adding
                val existingCheckboxes = (0 until checkBoxContainer.childCount).map { index ->
                    val checkBox = checkBoxContainer.getChildAt(index) as CheckBox
                    checkBox.text.toString()
                }

                if (!existingCheckboxes.contains(checkboxText)) {
                    // Create a new CheckBox and set its text
                    val checkBox = CheckBox(this)
                    checkBox.text = checkboxText

                    // Add CheckBox to the LinearLayout
                    val checkBoxContainer = findViewById<LinearLayout>(R.id.checkBoxContainer)
                    checkBoxContainer.addView(checkBox)

                    // Clear the input field
                    it.text.clear()
                } else {
                    Toast.makeText(this, "Checkbox with this label already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a checkbox label", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

        // Collect checked tasks
        val tasks = mutableListOf<String>()
        // Collect all checkboxes
        val allCheckboxes = mutableListOf<String>()
        for (i in 0 until checkBoxContainer.childCount) {
            val checkBox = checkBoxContainer.getChildAt(i) as CheckBox
            allCheckboxes.add(checkBox.text.toString())
            if (checkBox.isChecked) {
                tasks.add(checkBox.text.toString())
            }
        }

        // Convert lists to comma-separated strings
        val tasksAsString = tasks.joinToString(",")
        val checkboxesAsString = allCheckboxes.joinToString(",")

        // Save the todo to the database
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.todoDao().insertTask(
                    TodoModel(
                        title = title,
                        description = description,
                        category = category,
                        date = finalDate,
                        time = finalTime,
                        tasks = tasksAsString,       // Save checked tasks
                        checkboxes = checkboxesAsString, // Save all checkboxes
                        isFinished = 0
                    )
                )
            }
            finish() // Close the activity after saving
        }
    }



    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, min: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, min)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat = "h:mm a"
        val sdf = SimpleDateFormat(myformat)
        finalTime = myCalendar.time.time
        timeEdt.setText(sdf.format(myCalendar.time))
    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this, dateSetListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myformat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myformat)
        finalDate = myCalendar.time.time
        dateEdt.setText(sdf.format(myCalendar.time))
        timeInptLay.visibility = View.VISIBLE
    }
}
