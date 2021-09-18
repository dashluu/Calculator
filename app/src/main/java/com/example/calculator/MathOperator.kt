package com.example.calculator

import java.math.BigDecimal

class MathOperator(
    val name: String,
    val type: Type,
    val preced: Int
) {

    enum class Type { BIN_OP, UN_OP }

    private var execFun: ((BigDecimal, BigDecimal) -> BigDecimal) = { _, _ -> defaultResult }
    private val defaultResult = CoreMath.M_ZERO

    constructor(
        name: String, type: Type,
        preced: Int, execFun: (BigDecimal, BigDecimal) -> BigDecimal
    ) : this(name, type, preced) {
        this.execFun = execFun
    }

    fun exec(
        operand1: BigDecimal = defaultResult,
        operand2: BigDecimal = defaultResult
    ): BigDecimal {
        return execFun(operand1, operand2)
    }

    fun isBinOp(): Boolean {
        return type == Type.BIN_OP
    }

    fun isUnOp(): Boolean {
        return type == Type.UN_OP
    }

    operator fun compareTo(obj: MathOperator): Int {
        if (preced < obj.preced) return -1
        else if (preced == obj.preced) return 0
        return 1
    }
}