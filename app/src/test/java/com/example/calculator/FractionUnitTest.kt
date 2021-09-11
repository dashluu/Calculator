package com.example.calculator

import org.junit.Assert.*

import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class FractionUnitTest {

    @Test
    fun plus() {
        val frac1 = Fraction(BigInteger("100"), BigInteger("200"))
        val frac2 = Fraction(BigInteger("150"), BigInteger("300"))
        val frac3 = Fraction(BigInteger.ONE, BigInteger.ONE)
        assertEquals(frac3, frac1 + frac2)
    }

    @Test
    fun toFraction() {
        val num = BigDecimal("314.000")
        val frac = Fraction.toFraction(num)
        assertEquals(Fraction(BigInteger("314"), BigInteger.ONE), frac)
    }
}