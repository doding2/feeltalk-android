package com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAddChallengeBinding
import com.clonect.feeltalk.new_presentation.ui.util.SoftKeyboardDetectorView
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddChallengeFragment : Fragment() {

    private lateinit var binding: FragmentAddChallengeBinding
    private val viewModel: AddChallengeViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDatePickerListener()
        setKeyboardListeners()
        collectViewModel()

        binding.run {
            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }

            mcvCategory.setOnClickListener { changeCategory() }

            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }

            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            mcvAddSquare.setOnClickListener { addChallenge() }
            mcvAddRound.setOnClickListener { addChallenge() }
        }
    }


    private fun addChallenge() {
        viewModel.addNewChallenge()
        onBackCallback.handleOnBackPressed()
    }

    private fun changeCategory() {
        showChangeCategoryDialog(
            previousCategory = viewModel.category.value,
            onConfirm = {
                viewModel.setCategory(it)
            }
        )
    }

    private fun enableDeadlinePicker(enabled: Boolean) = binding.run {
        if (enabled) {
            dpDeadlinePicker.visibility = View.VISIBLE
            mcvDeadline.strokeColor = requireContext().getColor(R.color.main_500)
            mcvDeadline.strokeWidth = activity.dpToPx(2f).toInt()

            hideKeyboard()
        } else {
            dpDeadlinePicker.visibility = View.GONE
            mcvDeadline.strokeColor = requireContext().getColor(R.color.gray_400)
            mcvDeadline.strokeWidth = activity.dpToPx(1f).toInt()
        }
    }


    private fun setDatePickerListener() {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val now = viewModel.deadline.value
        val dateString = formatter.format(now)
        val dateTokens = dateString.split("/")

        binding.dpDeadlinePicker.minDate = now.time

        binding.dpDeadlinePicker.init(
            dateTokens[0].toInt(),
            dateTokens[1].toInt() - 1,
            dateTokens[2].toInt()
        ) { picker, year, monthOfYear, dayOfMonth ->
            val str = "$year/${monthOfYear + 1}/$dayOfMonth"
            val date = formatter.parse(str)
            if (date != null) {
                viewModel.setDeadline(date)
            }
        }
    }

    private fun setKeyboardListeners() = binding.run {
        svScroll.setOnClickListener { hideKeyboard() }

        val keyboardListener = SoftKeyboardDetectorView(requireActivity())
        requireActivity().addContentView(keyboardListener, FrameLayout.LayoutParams(-1, -1))
        keyboardListener.setOnShownKeyboard {
            expandAddButton(true)
            enableDeadlinePicker(false)
        }
        keyboardListener.setOnHiddenKeyboard {
            expandAddButton(false)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.etTitle.clearFocus()
        binding.etBody.clearFocus()
    }


    private fun expandAddButton(enabled: Boolean) = binding.run {
        if (enabled) {
            mcvAddSquare.visibility = View.VISIBLE
            mcvAddRound.visibility = View.GONE
        } else {
            mcvAddSquare.visibility = View.GONE
            mcvAddRound.visibility = View.VISIBLE
        }
    }

    private fun enableAddButton(enabled: Boolean) = binding.run {
        mcvAddRound.isClickable = enabled
        mcvAddRound.isFocusable = enabled
        mcvAddRound.isEnabled = enabled

        mcvAddSquare.isClickable = enabled
        mcvAddSquare.isFocusable = enabled
        mcvAddSquare.isEnabled = enabled

        if (enabled) {
            mcvAddRound.setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            mcvAddSquare.setCardBackgroundColor(resources.getColor(R.color.main_500, null))
        } else {
            mcvAddRound.setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            mcvAddSquare.setCardBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }

    private fun changeDeadlineView(deadline: Date) {
        val formatter = SimpleDateFormat("yyyy년 M월 dd일까지", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.title.collectLatest {
                    enableAddButton(!it.isNullOrBlank())
                }
            }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
        }
    }

    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpDeadlinePicker.isVisible) {
                    enableDeadlinePicker(false)
                    return
                }
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}