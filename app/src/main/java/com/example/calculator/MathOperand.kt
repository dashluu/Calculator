package com.example.calculator

class MathOperand(val content: String, val type: Type) {

    enum class Type { CONST, VAR }
}