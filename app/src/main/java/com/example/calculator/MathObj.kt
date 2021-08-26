package com.example.calculator

class MathObj(private val str: String) {
    private val types = arrayOf(false, false, false, false, false)

    enum class Type { NUMBER, BIN_OP, UN_OP, OPEN_PAREN, CLOSE_PAREN }

    fun setType(type: Type) {
        types[type.ordinal] = true
    }

    fun getStr(): String {
        return str
    }

    fun isOperator(): Boolean {
        return types[Type.BIN_OP.ordinal] || types[Type.UN_OP.ordinal]
    }

    fun isBinOp(): Boolean {
        return types[Type.BIN_OP.ordinal]
    }

    fun isUnOp(): Boolean {
        return types[Type.UN_OP.ordinal]
    }

    fun isOpenParen(): Boolean {
        return types[Type.OPEN_PAREN.ordinal]
    }

    fun isCloseParen(): Boolean {
        return types[Type.CLOSE_PAREN.ordinal]
    }

    fun isOperand(): Boolean {
        return types[Type.NUMBER.ordinal]
    }
}