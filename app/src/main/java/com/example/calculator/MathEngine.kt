package com.example.calculator

import kotlin.collections.ArrayDeque
import kotlin.math.pow

class MathEngine {
    private val tokenQueue = ArrayDeque<MathObj>()
    private val postfixQueue = ArrayDeque<MathObj>()
    private val operatorStack = ArrayDeque<MathObj>()
    private val evalStack = ArrayDeque<MathObj>()

    companion object Expr {
        private val operatorSet = mapOf(
            "(" to MathObj("(", MathObj.Type.OPEN_PAREN.flag, 1),
            ")" to MathObj(")", MathObj.Type.CLOSE_PAREN.flag, 1),
            "√(" to MathObj("√(", MathObj.Type.UN_OP.flag or MathObj.Type.OPEN_PAREN.flag, 1),
            "!" to MathObj("!", MathObj.Type.UN_OP.flag, 2),
            "^" to MathObj("^", MathObj.Type.BIN_OP.flag, 3) { operand1: Double, operand2: Double ->
                operand1.pow(operand2)
            },
            "×" to MathObj("×", MathObj.Type.BIN_OP.flag, 4) { operand1: Double, operand2: Double ->
                operand1 * operand2
            },
            "÷" to MathObj("÷", MathObj.Type.BIN_OP.flag, 4) { operand1: Double, operand2: Double ->
                operand1 / operand2
            },
            "+" to MathObj("+", MathObj.Type.BIN_OP.flag, 5) { operand1: Double, operand2: Double ->
                operand1 + operand2
            },
            "-" to MathObj("-", MathObj.Type.BIN_OP.flag, 5) { operand1: Double, operand2: Double ->
                operand1 - operand2
            }
        )
    }

    private fun getOperator(str: String): MathObj {
        return operatorSet.getValue(str)
    }

    fun eval(inputSeq: ArrayDeque<String>): String {
        clearResource()
        processInput(inputSeq)
        transformToPostfix()
        return evalPostfix()
    }

    private fun clearResource() {
        tokenQueue.clear()
        postfixQueue.clear()
        operatorStack.clear()
        evalStack.clear()
    }

    private fun isNumComp(input: Char): Boolean {
        return input.isDigit() || input == '.'
    }

    private fun processInput(inputSeq: ArrayDeque<String>) {
        var token = ""
        var currMathObj: MathObj

        for (str in inputSeq) {
            if (isNumComp(str[0]))
                token += str
            else {
                if (token.isNotEmpty()) {
                    currMathObj = MathObj(token, MathObj.Type.NUMBER.flag, 0)
                    tokenQueue.addLast(currMathObj)
                }
                currMathObj = getOperator(str)
                tokenQueue.addLast(currMathObj)
                token = ""
            }
        }

        if (token.isNotEmpty()) {
            currMathObj = MathObj(token, MathObj.Type.NUMBER.flag, 0)
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
        var operand1: Double?
        var operand2: Double?
        var result: Double
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
                operand2 = evalStack.removeLast().getStr().toDoubleOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand2 == null) throw SyntaxError()
                // Check if the stack is empty and throw an exception if it is.
                if (evalStack.isEmpty()) throw SyntaxError()
                operand1 = evalStack.removeLast().getStr().toDoubleOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand1 == null) throw SyntaxError()
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
                operand1 = evalStack.removeLast().getStr().toDoubleOrNull()
                // Check if whatever is popped from the stack is numeric.
                if (operand1 == null) throw SyntaxError()
                result = top.exec(operand1, 0.0)
                resultObj = MathObj(result.toString(), MathObj.Type.NUMBER.flag, 0)
                evalStack.addLast(resultObj)
            } else {
                operand1 = top.getStr().toDoubleOrNull()
                // Check if the math object is a proper number.
                if (operand1 == null) throw SyntaxError()
                evalStack.addLast(top)
            }
        }

        if (evalStack.isEmpty()) return "0"
        return processResult(evalStack.removeLast().getStr())
    }

    private fun processResult(result: String): String {
        return result
    }
}