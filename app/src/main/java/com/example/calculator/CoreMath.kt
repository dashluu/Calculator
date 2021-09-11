package com.example.calculator

import java.lang.ArithmeticException
import java.math.BigDecimal
import java.math.BigInteger
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
        private const val MIN_FRAC_DIGITS = 10
        private const val MAX_FRAC_DIGITS = 20
        private val ROUNDING_MODE = RoundingMode.HALF_UP
        private const val MAX_TAYLOR_TERMS = 32
        private val M_ZERO = formatOperand(BigDecimal.ZERO)
        private val M_ONE = formatOperand(BigDecimal.ONE)
        private val M_NEG_ONE = formatOperand(-BigDecimal.ONE)
        private val M_PI: BigDecimal
            get() = formatOperand(BigDecimal(PI))
        private val M_DEG: BigDecimal
            get() = formatOperand(BigDecimal(180))

        fun formatOperand(operand: BigDecimal): BigDecimal {
            val decFormat = DecimalFormat()
            decFormat.maximumFractionDigits = MAX_FRAC_DIGITS
            decFormat.minimumFractionDigits = MAX_FRAC_DIGITS
            decFormat.roundingMode = ROUNDING_MODE
            decFormat.isGroupingUsed = false
            return decFormat.format(operand).toBigDecimal()
        }

        private fun isInt(num: BigDecimal): Boolean {
            val decimalVal = formatOperand(num)
            val intVal = toInt(num)
            return decimalVal == intVal
        }

        private fun toInt(num: BigDecimal): BigDecimal {
            return formatOperand(num.toBigInteger().toBigDecimal())
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
        decFormat.maximumFractionDigits = MIN_FRAC_DIGITS
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

    private fun negOnePosPow(powVal: BigDecimal): BigDecimal {
        if (isInt(powVal)) return powVal.pow(powVal.intValueExact())
        val intPowVal = toInt(powVal)
        val fracPowVal = powVal - intPowVal
        val frac = Fraction.toFraction(fracPowVal, false)
        val fracGCD = frac.simplifyAssign()

        if (fracGCD.rem(BigInteger("2")) == BigInteger.ZERO)
            throw ArithmeticException()
    }
}