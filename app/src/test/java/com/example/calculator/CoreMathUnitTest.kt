package com.example.calculator

import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoreMathUnitTest {
    private val coreMath = CoreMath()

    @Test
    fun exp_isCorrect() {
        assertEquals(
            BigDecimal("0.1495686192").fracScale(),
            coreMath.exp(BigDecimal("-1.9")).fracScale()
        )
    }

    @Test
    fun logECenterOne_isCorrect() {
        assertEquals(
            BigDecimal("-1.203972804").fracScale(),
            coreMath.logECenterOne(BigDecimal("0.3")).fracScale()
        )
    }

    @Test
    fun logE_isCorrect() {
        assertEquals(
            BigDecimal("1.098612289").fracScale(),
            coreMath.logE(BigDecimal("3")).fracScale()
        )
    }

    @Test
    fun posBasePow_isCorrect() {
        assertEquals(
            BigDecimal("3").fracScale(),
            coreMath.posBasePow(BigDecimal("3"), BigDecimal("1")).fracScale()
        )
    }
}