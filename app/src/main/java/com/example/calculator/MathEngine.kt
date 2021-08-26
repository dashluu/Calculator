package com.example.calculator

import kotlin.collections.ArrayDeque

class MathEngine {
    companion object Expr {
        private val precedences = mapOf<String, Int>()

        private fun postFix(inputQueue: ArrayDeque<MathObj>, outputQueue: ArrayDeque<MathObj>) {
            // Initialize stack and queue
            val operatorStack = ArrayDeque<MathObj>()
            var top: MathObj
            var openParenFlag: Boolean

            for (mathObj in inputQueue) {
                if (mathObj.isOperator() && !mathObj.isOpenParen()) {
                    /*
                    If the current string is an operator, compare it to the top of the stack.
                    If its precedence is lower or equal to that of stack top, pop the stack
                    until it is empty or an open parenthesis is encountered and then push the
                    operator onto the stack.
                     */
                    while (operatorStack.isNotEmpty() && !operatorStack.first().isOpenParen()) {
                        top = operatorStack.first()
                        if (cmpOperator(mathObj, top) <= 0) {
                            operatorStack.removeLast()
                            outputQueue.addLast(top)
                        }
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
                        top = operatorStack.first()
                        if (top.isOpenParen())
                            openParenFlag = true
                        operatorStack.removeLast()
                        if (top.isOperator())
                            outputQueue.addLast(top)
                    }
                    if (!openParenFlag) {
                        // TODO: throw an exception if an open parenthesis is missing
                    }
                } else {
                    // Simply enqueue operands.
                    outputQueue.addLast(mathObj)
                }
            }

            // Enqueue strings that are popped from the stack
            while (operatorStack.isNotEmpty()) {
                top = operatorStack.first()
                operatorStack.removeLast()
                outputQueue.addLast(top)
            }
        }

        private fun cmpOperator(mathObj1: MathObj, mathObj2: MathObj): Int {
            val precedence1 = precedences.getValue(mathObj1.getStr())
            val precedence2 = precedences.getValue(mathObj2.getStr())
            if (precedence1 < precedence2) return -1
            else if (precedence1 == precedence2) return 0
            return 1
        }

        fun evalExpr(inputQueue: ArrayDeque<MathObj>): Double {
            val evalStack: ArrayDeque<MathObj> = ArrayDeque()
            val outputQueue = ArrayDeque<MathObj>()
            var top: MathObj
            var operand1: Double
            var operand2: Double
            var result: MathObj
            postFix(inputQueue, outputQueue)

            while (outputQueue.isNotEmpty()) {
                top = outputQueue.removeFirst()
                if (top.isBinOp()) {
                    /*
                    If the operator is a binary operator, pop two operands and execute the
                    binary operation.
                     */
                    // TODO: check if the stack is empty and throw an exception if it is
                    operand2 = evalStack.removeLast().getStr().toDouble()
                    // TODO: check if the stack is empty and throw an exception if it is
                    operand1 = evalStack.removeLast().getStr().toDouble()
                    result = evalOperation(operand1, operand2, top.getStr())
                    evalStack.addLast(result)
                } else if (top.isUnOp()) {
                    /*
                    If the operator is a unary operator, pop one operand and execute the
                    unary operation.
                     */
                    // TODO: check if the stack is empty and throw an exception if it is
                    operand1 = evalStack.removeLast().getStr().toDouble()
                    result = evalOperation(operand1, 0.0, top.getStr())
                    evalStack.addLast(result)
                } else evalStack.addLast(top)
            }

            if (evalStack.isEmpty()) return 0.0
            return evalStack.removeLast().getStr().toDouble()
        }

        private fun evalOperation(operand1: Double, operator2: Double, operator: String): MathObj {
            val result: Double = when (operator) {
                "+" -> operand1 + operator2
                "-" -> operand1 - operator2
                "×" -> operand1 * operator2
                "÷" -> operand1 / operator2
                else -> 0.0
            }
            return MathObj(result.toString())
        }
    }
}