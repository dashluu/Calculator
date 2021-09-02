package com.example.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.PI

class CoreMath {

    private enum class RadMode { RAD, DEG }

    private var radMode = RadMode.RAD

    companion object {
        private const val NUM_FRAC_DIGITS = 9
        private val ROUNDING_MODE = RoundingMode.HALF_UP
        private const val MAX_TAYLOR_TERMS = 32
        private val CORE_ZERO = operandToNum(BigDecimal.ZERO)
        private val CORE_ONE = operandToNum(BigDecimal.ONE)
        private val CORE_NEG_ONE = operandToNum(-BigDecimal.ONE)
        private val CORE_PI: BigDecimal
            get() = operandToNum(BigDecimal(PI))
        private val CORE_DEG: BigDecimal
            get() = operandToNum(BigDecimal(180))

        private fun operandToStr(operand: BigDecimal): String {
            val decFormat = DecimalFormat()
            decFormat.maximumFractionDigits = NUM_FRAC_DIGITS
            decFormat.minimumFractionDigits = NUM_FRAC_DIGITS
            decFormat.roundingMode = ROUNDING_MODE
            decFormat.isGroupingUsed = false
            return decFormat.format(operand)
        }

        fun operandToNum(operand: BigDecimal): BigDecimal {
            return operandToStr(operand).toBigDecimal()
        }
    }

    fun formatResult(result: BigDecimal): String {
        val decFormat = DecimalFormat()
        decFormat.maximumFractionDigits = NUM_FRAC_DIGITS
        decFormat.minimumFractionDigits = 0
        decFormat.roundingMode = ROUNDING_MODE
        decFormat.isGroupingUsed = false
        return decFormat.format(result)
    }

    private fun degToRad(deg: BigDecimal): BigDecimal {
        return (deg * CORE_PI) / CORE_DEG
    }

    fun plus(operand1: BigDecimal, operand2: BigDecimal): BigDecimal {
        return operand1 + operand2
    }

    fun sin(operand: BigDecimal): BigDecimal {
        var power = CORE_ONE
        var fact = CORE_ONE
        var sum = CORE_ZERO
        var altSign = CORE_NEG_ONE
        val modeOperand: BigDecimal = if (radMode == RadMode.RAD)
            degToRad(operand)
        else operand
        var i = 0

        while (i < MAX_TAYLOR_TERMS && !testError(CORE_ONE, modeOperand, CORE_ZERO, i, fact)) {
            if (i % 2 != 0) {
                altSign = -altSign
                sum += (altSign * power) / fact
            }
            power *= modeOperand
            fact *= BigDecimal(i + 1)
            ++i
        }

        return sum
    }

    private fun testError(
        m: BigDecimal, x: BigDecimal, a: BigDecimal,
        n: Int, fact: BigDecimal
    ): Boolean {
        val remainder = (m * (x - a).pow(n)) / fact
        return operandToNum(remainder) == CORE_ZERO
    }
}