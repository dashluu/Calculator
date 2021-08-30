package com.example.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.FragmentSimpleCalculatorBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SimpleCalculator.newInstance] factory method to
 * create an instance of this fragment.
 */
class SimpleCalculator : Fragment() {
    // TODO: create inputSeq as a list.
    // TODO: consider creating EditMode and ErrorMode as subclasses of Mode.
    // TODO: consider saving the current state before switching.

    private enum class State { EDIT, ERROR }

    private lateinit var binding: FragmentSimpleCalculatorBinding
    private val inputSeq = ArrayDeque<String>()
    private val mathEngine = MathEngine()
    private var state = State.EDIT
    private var recentResult = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_simple_calculator, container, false)

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
            equalBtn.setOnClickListener { showResult() }
        }

        return binding.root
    }

    private fun mapBtnInput(input: String) {
        inputSeq.addLast(input)
        binding.exprInput.text = binding.exprInput.text.toString() + input
    }

    private fun clearEntry() {
        val input = binding.exprInput.text.toString()
        val lastNumChars = inputSeq.removeLast().length
        binding.exprInput.text = input.substring(0, input.length - lastNumChars)
    }

    private fun clearAll() {
        if (state == State.EDIT) {
            inputSeq.clear()
        } else {
            state = State.EDIT
            binding.exprInput.text = recentResult
        }
    }

    private fun showResult() {
        try {
            recentResult = mathEngine.eval(inputSeq)
            inputSeq.clear()
            for (c in recentResult)
                inputSeq.addLast(c.toString())
            binding.exprInput.text = recentResult
        } catch (exception: SyntaxError) {
            binding.exprInput.text = exception.message
            state = State.ERROR
        }
    }
}