package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SimpleCalcViewModel : ViewModel() {
    enum class State { EDIT, ERROR }

    private val inputSeq = mutableListOf<String>()
    private val mathEngine = MathEngine()
    var state = MutableLiveData<State>()
    private var _recent = MutableLiveData<String>()
    val recent: LiveData<String>
        get() = _recent
    private var cursorPos = 0

    init {
        state.value = State.EDIT
        _recent.value = ""
    }

    private fun updateRecent() {
        var tempStr = ""
        for (str in inputSeq) tempStr += str
        _recent.value = tempStr
    }

    fun insertEntry(input: String) {
        inputSeq.add(cursorPos, input)
        ++cursorPos
        updateRecent()
    }

    fun removeEntry() {
        if (cursorPos > 0) --cursorPos
        inputSeq.removeAt(cursorPos)
        updateRecent()
    }

    fun clearAll() {
        if (state.value == State.EDIT) {
            inputSeq.clear()
            cursorPos = 0
            updateRecent()
        } else state.value = State.EDIT
    }

    fun getResult() {
        val result = mathEngine.eval(inputSeq)
        inputSeq.clear()
        cursorPos = 0
        for (str in result) {
            inputSeq.add(str.toString())
            ++cursorPos
        }
        updateRecent()
    }
}