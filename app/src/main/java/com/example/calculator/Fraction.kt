package com.example.calculator

import java.math.BigDecimal
import java.math.BigInteger

class Fraction(private var numer: BigInteger, private var denom: BigInteger) {

    init {
        if (denom < BigInteger.ZERO) {
            numer = -numer
            denom = -denom
        }
    }

    companion object {
        fun toFraction(num: BigDecimal, isSimplified: Boolean = true): Fraction {
            val numerator = num.unscaledValue()
            val denominator = BigInteger.TEN.pow(num.scale())
            val frac = Fraction(numerator, denominator)
            if (isSimplified) return frac.simplify()
            return frac
        }
    }

    override fun toString(): String {
        return "$numer/$denom"
    }

    private fun simplify(): Fraction {
        val gcd = findGCD(numer.abs(), denom.abs())
        return Fraction(numer / gcd, denom / gcd)
    }

    fun simplifyAssign(): BigInteger {
        val gcd = findGCD(numer.abs(), denom.abs())
        numer /= gcd
        denom /= gcd
        return gcd
    }

    private fun reciprocal(): Fraction {
        if (numer == BigInteger.ZERO)
            throw ArithmeticException("Cannot calculate the reciprocal of the zero fraction.")
        return Fraction(denom, numer)
    }

    private fun findGCD(num1: BigInteger, num2: BigInteger): BigInteger {
        return if (num2 == BigInteger.ZERO) num1
        else findGCD(num2, num1.rem(num2))
    }

    private fun findLCM(num1: BigInteger, num2: BigInteger): BigInteger {
        return num1 * num2 / findGCD(num1, num2)
    }

    operator fun unaryMinus(): Fraction {
        return Fraction(-numer, denom)
    }

    operator fun plus(other: Fraction): Fraction {
        // Simplifying two fractions before doing addition.
        val frac1 = simplify()
        val frac2 = other.simplify()
        // Find the two denominators' least common multiple.
        val lcdDenom = findLCM(frac1.denom, frac2.denom)
        // Ensure the denominators are the same and add the two numerators.
        val numer1 = frac1.numer * lcdDenom / frac1.denom
        val numer2 = frac2.numer * lcdDenom / frac2.denom
        val sum = Fraction(numer1 + numer2, lcdDenom)
        return sum.simplify()
    }

    operator fun minus(other: Fraction): Fraction {
        return plus(-other)
    }

    operator fun times(other: Fraction): Fraction {
        // Simplifying two fractions before doing multiplication.
        val frac1 = simplify()
        val frac2 = other.simplify()
        val product = Fraction(frac1.numer * frac2.numer, frac1.denom * frac2.denom)
        return product.simplify()
    }

    operator fun div(other: Fraction): Fraction {
        // '/' is the same as doing '* (1 / fraction)'
        return times(other.reciprocal())
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Fraction) return false
        val frac1 = simplify()
        val frac2 = other.simplify()
        return (frac1.numer == frac2.numer) && (frac1.denom == frac2.denom)
    }

    operator fun compareTo(other: Fraction): Int {
        // Simplifying two fractions before doing the comparison.
        val frac1 = simplify()
        val frac2 = other.simplify()
        // Find the two denominators' least common multiple.
        val lcdDenom = findLCM(frac1.denom, frac2.denom)
        // Ensure the denominators are the same and compare the two numerators.
        val numer1 = frac1.numer * lcdDenom / frac1.denom
        val numer2 = frac2.numer * lcdDenom / frac2.denom
        return numer1.compareTo(numer2)
    }
}