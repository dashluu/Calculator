package com.example.calculator

import java.lang.ArithmeticException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.E
import kotlin.math.log

fun BigDecimal.fracScale(): BigDecimal {
    return setScale(CoreMath.MAX_FRAC_DIGITS, CoreMath.ROUNDING_MODE)
}

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
        const val MIN_FRAC_DIGITS = 9
        const val MAX_FRAC_DIGITS = 20
        val ROUNDING_MODE = RoundingMode.HALF_UP
        const val MAX_TAYLOR_TERMS = 50
        val M_ZERO: BigDecimal = BigDecimal.ZERO
        val M_ONE: BigDecimal = BigDecimal.ONE
        val M_TEN: BigDecimal = BigDecimal.TEN
        val M_PI = BigDecimal(PI)
        val M_DEG = BigDecimal(180)
        val M_E = BigDecimal(E)
        val M_LOG_E_TEN = BigDecimal(log(10.0, E))
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

    private fun zeroPow(xVal: BigDecimal): BigDecimal {
        if (xVal < M_ZERO) throw SyntaxError()
        else if (xVal > M_ZERO) return M_ZERO
        return M_ONE
    }

    private fun mAdd(num1: BigDecimal, num2: BigDecimal): BigDecimal {
        return num1.fracScale() + num2.fracScale()
    }

    private fun mSub(num1: BigDecimal, num2: BigDecimal): BigDecimal {
        return num1.fracScale() - num2.fracScale()
    }

    private fun mMult(num1: BigDecimal, num2: BigDecimal): BigDecimal {
        return num1.fracScale() * num2.fracScale()
    }

    private fun mDiv(num1: BigDecimal, num2: BigDecimal): BigDecimal {
        return num1.fracScale() / num2.fracScale()
    }

    private fun mPow(baseVal: BigDecimal, powVal: Int): BigDecimal {
        return baseVal.fracScale().pow(powVal).fracScale()
    }

    private fun testPrecision(
        mVal: BigDecimal, xVal: BigDecimal, aVal: BigDecimal,
        nVal: Int, fact: BigDecimal
    ): Boolean {
        // (mVal * (xVal - aVal).abs().pow(nVal)) / fact == M_ZERO
        return mDiv(mMult(mVal, mPow(mSub(xVal, aVal).abs(), nVal)), fact) == M_ZERO.fracScale()
    }

    fun exp(xVal: BigDecimal): BigDecimal {
        var i = 0
        var xPow = M_ONE
        var fact = M_ONE
        var result = M_ZERO
        // upper = M_E.pow(xVal.toInt() + 1)
        val upper = mPow(M_E, xVal.toInt() + 1)

        while (!testPrecision(upper, xVal, M_ZERO, i, fact)) {
            // result += xPow / fact
            result = mAdd(result, mDiv(xPow, fact))
            // xPow *= xVal
            xPow = mMult(xPow, xVal)
            // fact *= BigDecimal(i + 1)
            fact = mMult(fact, BigDecimal(i + 1))
            ++i
        }

        return result
    }

    fun logECenterOne(xVal: BigDecimal): BigDecimal {
        // Assume that 0 < xVal < 2.
        var i = 1
        var altSign = M_ONE
        var result = M_ZERO
        val xFrac = xVal - M_ONE

        // xFrac.abs().pow(i) / BigDecimal(i) > M_ZERO
        while (mDiv(mPow(xFrac.abs(), i), BigDecimal(i)) > M_ZERO) {
            // result += altSign * xFrac.pow(i) / BigDecimal(i)
            result = mAdd(result, mDiv(mMult(altSign, mPow(xFrac, i)), BigDecimal(i)))
            altSign = -altSign
            ++i
        }

        return result
    }

    fun logE(xVal: BigDecimal): BigDecimal {
        // Assume xVal is a positive real number.
        var tmpVal = xVal
        var tenPow = 0
        while (tmpVal > M_ZERO) {
            tmpVal = tmpVal.divideToIntegralValue(M_TEN)
            ++tenPow
        }
        // powTen = M_TEN.pow(tenPow)
        val powTen = mPow(M_TEN, tenPow)
        val tenPowDec = tenPow.toBigDecimal()
        // tenPowDec * M_LOG_E_TEN + logECenterOne(M_ONE - (powTen - xVal) / powTen)
        return mAdd(
            mMult(tenPowDec, M_LOG_E_TEN),
            logECenterOne(mSub(M_ONE, mDiv(mSub(powTen, xVal), powTen)))
        )
    }

    fun posBasePow(baseVal: BigDecimal, powVal: BigDecimal): BigDecimal {
        // Assume baseVal is a positive real number.
        // exp(powVal * logE(baseVal))
        return exp(mMult(powVal, logE(baseVal)))
    }

    fun sqrt(xVal: BigDecimal): BigDecimal {
        if (xVal < M_ZERO) throw ArithmeticException()
        return posBasePow(xVal, BigDecimal("0.5"))
    }
}