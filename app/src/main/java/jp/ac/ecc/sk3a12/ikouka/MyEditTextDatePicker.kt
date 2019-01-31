package jp.ac.ecc.sk3a12.ikouka

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import java.util.*


class MyEditTextDatePicker: View.OnClickListener, DatePickerDialog.OnDateSetListener {
    lateinit var _editText: EditText
    private var _day: Int = 0
    private var _month: Int = 0
    private var _year: Int = 0
    private lateinit var _context: Context

    companion object {
        fun newInstance(context: Context, editTextViewID: Int): MyEditTextDatePicker {
            return MyEditTextDatePicker().apply {
                val act = context as Activity
                this._editText = act.findViewById(editTextViewID)
                this._editText.setOnClickListener(this)
                this._context = context
            }
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        _year = year
        _month = monthOfYear
        _day = dayOfMonth
        updateDisplay()
    }

    override fun onClick(v: View) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val dialog = DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        dialog.show()

    }

    // updates the date in the birth date EditText
    private fun updateDisplay() {

        _editText.setText(StringBuilder()
                // Month is 0 based so add 1
                .append(_year).append("年").append(_month + 1).append("月").append(_day).append("日"))
    }
}