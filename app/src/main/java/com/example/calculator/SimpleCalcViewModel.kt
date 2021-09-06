package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SimpleCalcViewModel : ViewModel() {
    enum class State { EDIT, ERROR }

    private val inputManager = InputManager()
    private val mathEngine = MathEngine()
    private var state = State.EDIT
    private val _content = MutableLiveData<String>()
    val content: LiveData<String>
        get() = _content

    fun insertEntry(input: String) {
        inputManager.insertEntry(input)
        _content.value = inputManager.composedStr
    }

    fun removeEntry() {
        inputManager.removeEntry()
        _content.value = inputManager.composedStr
    }

    fun clearAll() {
        if (state == State.EDIT)
            inputManager.removeAll()
        else state = State.EDIT
        _content.value = inputManager.composedStr
    }

    fun getResult() {
        val result = mathEngine.eval(inputManager.tokens)
        inputManager.removeAll()
        inputManager.insertEntry(result)
        _content.value = inputManager.composedStr
    }

    fun setErrMsg(errMsg: String) {
        state = State.ERROR
        _content.value = errMsg
    }
}