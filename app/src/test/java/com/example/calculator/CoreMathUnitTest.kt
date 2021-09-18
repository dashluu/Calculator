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
    fun testExp() {
        assertEquals(
            coreMath.formatResult(BigDecimal("0.1495686192")),
            coreMath.formatResult(coreMath.exp(BigDecimal("-1.9")))
        )
    }

    @Test
    fun testLogECenterOne() {
        assertEquals(
            coreMath.formatResult(BigDecimal("-1.203972804")),
            coreMath.formatResult(coreMath.logECenterOne(BigDecimal("0.3")))
        )
    }

    @Test
    fun testLogE() {
        assertEquals(
            coreMath.formatResult(BigDecimal("1.098612289")),
            coreMath.formatResult(coreMath.logE(BigDecimal("3")))
        )
    }

    @Test
    fun testPosBasePow() {
        assertEquals(
            coreMath.formatResult(BigDecimal("36203333.15")),
            coreMath.formatResult(coreMath.posBasePow(BigDecimal("5.7"), BigDecimal("10")))
        )
    }

    @Test
    fun testComplexExpr() {
        assertEquals(
            coreMath.formatResult(BigDecimal("2")),
            coreMath.formatResult(
                coreMath.posBasePow(
                    coreMath.posBasePow(
                        BigDecimal("2"),
                        BigDecimal("0.5")
                    ), BigDecimal("2")
                )
            )
        )
    }
}