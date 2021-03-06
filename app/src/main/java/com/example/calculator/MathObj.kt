package com.example.calculator

import java.math.BigDecimal

class MathObj(private val _str: String, private var type: Int, private val preced: Int) {

    val str: String
        get() = _str
    private var execFun: ((BigDecimal, BigDecimal) -> BigDecimal) = { _, _ -> defaultResult }
    private val defaultResult = BigDecimal.ZERO

    enum class Type(val flag: Int, val pos: Int) {
        NUMBER(0b00001, 0),
        BIN_OP(0b00010, 1),
        UN_OP(0b00100, 2),
        OPEN_PAREN(0b01000, 3),
        CLOSE_PAREN(0b10000, 4)
    }

    constructor(
        str: String, type: Int,
        preced: Int, execFun: (BigDecimal, BigDecimal) -> BigDecimal
    ) : this(str, type, preced) {
        this.execFun = execFun
    }

    fun exec(
        operand1: BigDecimal = defaultResult,
        operand2: BigDecimal = defaultResult
    ): BigDecimal {
        return execFun(operand1, operand2)
    }

    fun isOperator(): Boolean {
        return (((type shr Type.BIN_OP.pos) and 1) > 0) ||
                (((type shr Type.UN_OP.pos) and 1) > 0)
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

    operator fun compareTo(obj: MathObj): Int {
        if (preced < obj.preced) return -1
        else if (preced == obj.preced) return 0
        return 1
    }
}