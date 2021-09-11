package com.example.calculator

import java.math.BigDecimal
import kotlin.collections.ArrayDeque

class MathEngine {

    private val tokenQueue = ArrayDeque<MathObj>()
    private val postfixQueue = ArrayDeque<MathObj>()
    private val operatorStack = ArrayDeque<MathObj>()
    private val evalStack = ArrayDeque<MathObj>()
    private val coreMath = CoreMath()

    fun eval(tokens: List<String>): String {
        clearResource()
        processInput(tokens)
        transformToPostfix()
        return evalPostfix()
    }

    private fun clearResource() {
        // Clear all resources before the next computation.
        tokenQueue.clear()
        postfixQueue.clear()
        operatorStack.clear()
        evalStack.clear()
    }

    private fun isNumComp(input: Char): Boolean {
        return input.isDigit() || input == '.'
    }

    private fun processInput(tokens: List<String>) {
        var numToken = ""
        var currMathObj: MathObj

        for (str in tokens) {
            if (isNumComp(str[0]))
                numToken += str
            else {
                if (numToken.isNotEmpty()) {
                    currMathObj = MathObj(numToken, MathObj.Type.NUMBER.flag, 0)
                    tokenQueue.addLast(currMathObj)
                }
                currMathObj = coreMath.getOperator(str)!!
                tokenQueue.addLast(currMathObj)
                numToken = ""
            }
        }

        if (numToken.isNotEmpty()) {
            currMathObj = MathObj(numToken, MathObj.Type.NUMBER.flag, 0)
            tokenQueue.addLast(currMathObj)
        }
    }

    @Throws(SyntaxError::class)
    private fun transformToPostfix() {
        // Initialize stack and queue
        var top: MathObj
        var openParenFlag: Boolean

        for (mathObj in tokenQueue) {
            if (mathObj.isOperator() && !mathObj.isOpenParen()) {
                /*
                If the current string is an operator, compare it to the top of the stack.
                If its precedence is lower or equal to that of stack top, pop the stack
                until it is empty or an open parenthesis is encountered and then push the
                operator onto the stack.
                 */
                while (operatorStack.isNotEmpty()
                    && !operatorStack.last().isOpenParen()
                    && mathObj >= operatorStack.last()
                ) {
                    top = operatorStack.removeLast()
                    postfixQueue.addLast(top)
                }
                operatorStack.addLast(mathObj)
            } else if (mathObj.isOpenParen()) {
                // An open parenthesis signifies a new scope.
                operatorStack.addLast(mathObj)
            } else if (mathObj.isCloseParen()) {
                /*
                If a close parenthesis is encountered, enqueue whatever is popped from
                the stack until an open parenthesis is discarded.
                 */
                openParenFlag = false
                while (operatorStack.isNotEmpty() && !openParenFlag) {
                    top = operatorStack.removeLast()
                    if (top.isOperator())
                        postfixQueue.addLast(top)
                    if (top.isOpenParen())
                        openParenFlag = true
                }
                // Throw an exception if an open parenthesis is missing.
                if (!openParenFlag) throw SyntaxError()
            } else {
                // Simply enqueue operands.
                postfixQueue.addLast(mathObj)
            }
        }

        // Enqueue strings that are popped from the stack
        while (operatorStack.isNotEmpty()) {
            top = operatorStack.removeLast()
            postfixQueue.addLast(top)
        }
    }

    @Throws(SyntaxError::class)
    private fun evalPostfix(): String {
        var top: MathObj
        var operand1: BigDecimal?
        var operand2: BigDecimal?
        var result: BigDecimal
        var resultObj: MathObj

        while (postfixQueue.isNotEmpty()) {
            top = postfixQueue.removeFirst()
            if (top.isBinOp()) {
                /*
                If the operator is a binary operator, pop two operands and execute the
                binary operation.
                 */
                // Check if the stack is empty and throw an exception if it is.
                if (evalStack.isEmpty()) throw SyntaxError()
                operand2 = evalStack.removeLast().str.toBigDecimalOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand2 == null) throw SyntaxError()
                // Check if the stack is empty and throw an exception if it is.
                if (evalStack.isEmpty()) throw SyntaxError()
                operand1 = evalStack.removeLast().str.toBigDecimalOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand1 == null) throw SyntaxError()
                // Format the operands before executing the operation.
                operand1 = CoreMath.formatOperand(operand1)
                operand2 = CoreMath.formatOperand(operand2)
                // Execute the proper operation and save the result in the stack.
                result = top.exec(operand1, operand2)
                resultObj = MathObj(result.toString(), MathObj.Type.NUMBER.flag, 0)
                evalStack.addLast(resultObj)
            } else if (top.isUnOp()) {
                /*
                If the operator is a unary operator, pop one operand and execute the
                unary operation.
                 */
                // Check if the stack is empty and throw an exception if it is.
                if (evalStack.isEmpty()) throw SyntaxError()
                operand1 = evalStack.removeLast().str.toBigDecimalOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand1 == null) throw SyntaxError()
                // Format the operand before executing the operation.
                operand1 = CoreMath.formatOperand(operand1)
                // Execute the proper operation and save the result in the stack.
                result = top.exec(operand1, BigDecimal.ZERO)
                resultObj = MathObj(result.toString(), MathObj.Type.NUMBER.flag, 0)
                evalStack.addLast(resultObj)
            } else {
                operand1 = top.str.toBigDecimalOrNull()
                // Check if the math object is a proper number.
                if (operand1 == null) throw SyntaxError()
                evalStack.addLast(top)
            }
        }

        if (evalStack.isEmpty()) return "0"
        return coreMath.formatResult(evalStack.removeLast().str.toBigDecimal())
    }
}