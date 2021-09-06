package com.example.calculator

class InputManager {

    private val _tokens = mutableListOf<String>()
    val tokens: List<String>
        get() = _tokens
    private var _composedStr = ""
    val composedStr: String
        get() = _composedStr
    private var tokenCursor = 0
    private var composedStrCursor = 0

    fun insertEntry(input: String) {
        _tokens.add(tokenCursor, input)
        ++tokenCursor
        composedStrInsert(input)
    }

    fun removeEntry() {
        if (tokenCursor <= 0) return
        --tokenCursor
        val str = _tokens.removeAt(tokenCursor)
        composedStrRemove(str.length)
    }

    fun removeAll() {
        _tokens.clear()
        _composedStr = ""
        tokenCursor = 0
        composedStrCursor = 0
    }

    private fun composedStrInsert(input: String) {
        val subStr1 = _composedStr.substring(0, composedStrCursor)
        val subStr2 = _composedStr.substring(composedStrCursor, _composedStr.length)
        _composedStr = subStr1 + input + subStr2
        composedStrCursor += input.length
    }

    private fun composedStrRemove(length: Int) {
        val subStr1 = _composedStr.substring(0, composedStrCursor - length)
        val subStr2 = _composedStr.substring(composedStrCursor, _composedStr.length)
        _composedStr = subStr1 + subStr2
        composedStrCursor -= length
    }
}