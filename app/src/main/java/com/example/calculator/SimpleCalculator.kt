package com.example.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.FragmentSimpleCalculatorBinding
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SimpleCalculator.newInstance] factory method to
 * create an instance of this fragment.
 */
class SimpleCalculator : Fragment() {
    private lateinit var binding: FragmentSimpleCalculatorBinding
    private lateinit var inputStack: ArrayDeque<String>
    private lateinit var tokenStack: ArrayDeque<MathObj>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_simple_calculator, container, false)
        inputStack = ArrayDeque<String>()

        binding.apply {
            zeroBtn.setOnClickListener { mapBtnInput("0") }
            oneBtn.setOnClickListener { mapBtnInput("1") }
            twoBtn.setOnClickListener { mapBtnInput("2") }
            threeBtn.setOnClickListener { mapBtnInput("3") }
            fourBtn.setOnClickListener { mapBtnInput("4") }
            fiveBtn.setOnClickListener { mapBtnInput("5") }
            sixBtn.setOnClickListener { mapBtnInput("6") }
            sevenBtn.setOnClickListener { mapBtnInput("7") }
            eightBtn.setOnClickListener { mapBtnInput("8") }
            nineBtn.setOnClickListener { mapBtnInput("9") }
            decimalPointBtn.setOnClickListener { mapBtnInput(".") }
            openParenthesisBtn.setOnClickListener { mapBtnInput("(") }
            closeParenthesisBtn.setOnClickListener { mapBtnInput(")") }
            factorialBtn.setOnClickListener { mapBtnInput("!") }
            addBtn.setOnClickListener { mapBtnInput("+") }
            subtractBtn.setOnClickListener { mapBtnInput("-") }
            multiplyBtn.setOnClickListener { mapBtnInput("×") }
            divideBtn.setOnClickListener { mapBtnInput("÷") }
            powerBtn.setOnClickListener { mapBtnInput("^") }
            sqrtBtn.setOnClickListener { mapBtnInput("√(") }
            clearEntryBtn.setOnClickListener { clearEntry() }
            clearAllBtn.setOnClickListener { clearAll() }
        }

        return binding.root
    }

    private fun mapBtnInput(input: String) {
        inputStack.addLast(input)
        binding.expressionInput.text = binding.expressionInput.text.toString() + input
    }

    private fun clearEntry() {
        var input = binding.expressionInput.text.toString()
        if (input.isEmpty()) return
        val lastNumChars = inputStack.last().length
        input = input.substring(0, input.length - lastNumChars)
        binding.expressionInput.text = input
    }

    private fun clearAll() {
        inputStack.clear()
        binding.expressionInput.text = ""
    }

    private fun pushTokenStack() {
        var token = ""
        var currMathObj: MathObj

        for (str in inputStack) {
            if (str[0].isDigit())
                token += str
            else {
                if (token.isNotEmpty()) {
                    currMathObj = MathObj(token)
                    tokenStack.addLast(currMathObj)
                }
                currMathObj = MathObj(str)
                tokenStack.addLast(currMathObj)
                token = ""
            }
        }

        if (token.isNotEmpty()) {
            currMathObj = MathObj(token)
            tokenStack.addLast(currMathObj)
        }
    }
}