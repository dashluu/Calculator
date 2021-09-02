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
    fun sin_isCorrect() {
        assertEquals(
            CoreMath.operandToNum(BigDecimal(0.121869344)),
            CoreMath.operandToNum(coreMath.sin(BigDecimal(7)))
        )
    }
}