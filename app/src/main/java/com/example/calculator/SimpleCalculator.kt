package com.example.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.FragmentSimpleCalculatorBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SimpleCalculator.newInstance] factory method to
 * create an instance of this fragment.
 */
class SimpleCalculator : Fragment() {

    private lateinit var binding: FragmentSimpleCalculatorBinding
    private lateinit var calcViewModel: SimpleCalcViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_simple_calculator, container, false)

        calcViewModel = ViewModelProvider(this).get(SimpleCalcViewModel::class.java)
        calcViewModel.recent.observe(viewLifecycleOwner, { newRecent ->
            binding.exprInput.text = newRecent
        })

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
        calcViewModel.insertEntry(input)
    }

    private fun clearEntry() {
        calcViewModel.removeEntry()
    }

    private fun clearAll() {
        calcViewModel.clearAll()
        binding.exprInput.text = calcViewModel.recent.value
    }

    private fun showResult() {
        try {
            calcViewModel.getResult()
        } catch (error: SyntaxError) {
            binding.exprInput.text = error.message
            calcViewModel.state.value = SimpleCalcViewModel.State.ERROR
        }
    }
}