package com.example.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.PI

class CoreMath {

    private enum class RadMode { RAD, DEG }

    private var radMode = RadMode.RAD

    private val operatorSet = arrayOf(
        MathObj("-(", MathObj.Type.UN_OP.flag or MathObj.Type.OPEN_PAREN.flag, 1)
        { operand1: BigDecimal, _: BigDecimal -> -operand1 },
        MathObj("(", MathObj.Type.OPEN_PAREN.flag, 1),
        MathObj(")", MathObj.Type.CLOSE_PAREN.flag, 1),
        MathObj("√(", MathObj.Type.UN_OP.flag or MathObj.Type.OPEN_PAREN.flag, 1),
        MathObj("!", MathObj.Type.UN_OP.flag, 2),
        MathObj("^", MathObj.Type.BIN_OP.flag, 3),
        MathObj("×", MathObj.Type.BIN_OP.flag, 4)
        { operand1: BigDecimal, operand2: BigDecimal -> operand1 * operand2 },
        MathObj("÷", MathObj.Type.BIN_OP.flag, 4)
        { operand1: BigDecimal, operand2: BigDecimal -> operand1 / operand2 },
        MathObj("+", MathObj.Type.BIN_OP.flag, 5)
        { operand1: BigDecimal, operand2: BigDecimal -> operand1 + operand2 },
        MathObj("-", MathObj.Type.BIN_OP.flag, 5)
        { operand1: BigDecimal, operand2: BigDecimal -> operand1 - operand2 }
    )

    companion object {
        private const val NUM_FRAC_DIGITS = 9
        private val ROUNDING_MODE = RoundingMode.HALF_UP
        private const val MAX_TAYLOR_TERMS = 32
        private val M_ZERO = operandToNum(BigDecimal.ZERO)
        private val M_ONE = operandToNum(BigDecimal.ONE)
        private val M_NEG_ONE = operandToNum(-BigDecimal.ONE)
        private val M_PI: BigDecimal
            get() = operandToNum(BigDecimal(PI))
        private val M_DEG: BigDecimal
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

    init {
        // Sort the operator set to use binary search later.
        operatorSet.sortBy { it.str }
    }

    fun getOperator(str: String): MathObj? {
        var low = 0
        var high = operatorSet.size
        var mid: Int
        // Use binary search to find the operator in the set.
        while (low <= high) {
            mid = (low + high) / 2
            when {
                str == operatorSet[mid].str -> return operatorSet[mid]
                str < operatorSet[mid].str -> high = mid - 1
                else -> low = mid + 1
            }
        }
        // If the operator is not found, return null, although this never happens.
        return null
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
        return (deg * M_PI) / M_DEG
    }

    private fun zeroPow(powVal: BigDecimal): BigDecimal {
        if (powVal < M_ZERO) throw SyntaxError()
        else if (powVal == M_ZERO) return M_ONE
        return M_ZERO
    }

    fun sin(operand: BigDecimal): BigDecimal {
        var power = M_ONE
        var fact = M_ONE
        var sum = M_ZERO
        var altSign = M_NEG_ONE
        val modeOperand: BigDecimal = if (radMode == RadMode.RAD)
            degToRad(operand)
        else operand
        var i = 0

        while (i < MAX_TAYLOR_TERMS && !testError(M_ONE, modeOperand, M_ZERO, i, fact)) {
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
        return operandToNum(remainder) == M_ZERO
    }
}