package com.example.calculator

class MathObj(private val str: String, private var type: Int, private val preced: Int) {
    enum class Type(val flag: Int, val pos: Int) {
        NUMBER(0b00001, 0),
        BIN_OP(0b00010, 1),
        UN_OP(0b00100, 2),
        OPEN_PAREN(0b01000, 3),
        CLOSE_PAREN(0b10000, 4)
    }

    fun getStr(): String {
        return str
    }

    fun isOperator(): Boolean {
        return (((type shr Type.BIN_OP.pos) and 1) > 0)
                || (((type shr Type.UN_OP.pos) and 1) > 0)
    }

    fun isBinOp(): Boolean {
        return ((type shr Type.BIN_OP.pos) and 1) > 0
    }

    fun isUnOp(): Boolean {
        return ((type shr Type.UN_OP.pos) and 1) > 0
    }

    fun isOpenParen(): Boolean {
        return ((type shr Type.OPEN_PAREN.pos) and 1) > 0
    }

    fun isCloseParen(): Boolean {
        return ((type shr Type.CLOSE_PAREN.pos) and 1) > 0
    }

    fun isOperand(): Boolean {
        return ((type shr Type.NUMBER.pos) and 1) > 0
    }

    fun cmp(obj: MathObj): Int {
        if (preced < obj.preced) return -1
        else if (preced == obj.preced) return 0
        return 1
    }
}