package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.addChallenge

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentAddChallengeBinding
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@AndroidEntryPoint
class AddChallengeFragment : Fragment() {

    private lateinit var binding: FragmentAddChallengeBinding
    private val viewModel: AddChallengeViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private val loadingDialog by lazy { makeLoadingDialog() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddChallengeBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDatePickerListener()
        setKeyboardInsets()
        collectViewModel()

        binding.run {
            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }

            mcvCategory.setOnClickListener {
                enableDeadlinePicker(false)
                changeCategory()
            }

            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }

            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            mcvAddSquare.setOnClickListener { addChallenge() }
            mcvAddRound.setOnClickListener { addChallenge() }
        }
    }


    private fun addChallenge() {
        hideKeyboard()
        viewModel.addNewChallenge {
            findNavController().popBackStack()
        }
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
            mcvDeadline.strokeWidth = activity.dpToPx(2f).toInt()
            mcvDeadline.setCardBackgroundColor(Color.WHITE)

            hideKeyboard()
            expandAddButton(true)
        } else {
            dpDeadlinePicker.visibility = View.GONE
            mcvDeadline.strokeWidth = 0
            mcvDeadline.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
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

    private fun setKeyboardInsets() = binding.run {
        svScroll.setOnClickListener { hideKeyboard() }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.systemWindowInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            if (imeHeight == 0) {
                binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.etTitle.clearFocus()
        binding.etBody.clearFocus()
    }


    private fun expandAddButton(enabled: Boolean) = binding.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvAddRound.visibility = View.VISIBLE
            mcvAddRound.radius = 0f
            mcvAddRound.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            return@run
        }

        if (dpDeadlinePicker.isVisible) {
            mcvAddSquare.visibility = View.VISIBLE
            mcvAddRound.visibility = View.GONE
            return@run
        }

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
            tvAddRound.setTextColor(Color.WHITE)
            tvAddSquare.setTextColor(Color.WHITE)
        } else {
            mcvAddRound.setCardBackgroundColor(resources.getColor(R.color.gray_300, null))
            mcvAddSquare.setCardBackgroundColor(resources.getColor(R.color.gray_300, null))
            tvAddRound.setTextColor(requireContext().getColor(R.color.gray_500))
            tvAddSquare.setTextColor(requireContext().getColor(R.color.gray_500))
        }
    }

    private fun changeDeadlineView(deadline: Date) {
        val dDay = ceil((deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
        val formatter = SimpleDateFormat("yyyy년 M월 dd일까지", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
        binding.tvDDay.text = if (dDay == 0) {
            requireContext().getString(R.string.add_challenge_d_day_today)
        } else {
            requireContext().getString(R.string.add_challenge_d_day_normal) + dDay
        }
    }

    private fun changeViewWhenKeyboardUp(isUp: Boolean) {
        if (isUp) {
            expandAddButton(true)
            enableDeadlinePicker(false)
        } else {
            expandAddButton(false)
        }
    }

    private fun changeCategoryView(category: ChallengeCategory) {
        when (category) {
            ChallengeCategory.Place -> {
                binding.tvCategory.setText(R.string.change_category_select_1)
            }
            ChallengeCategory.Toy ->  {
                binding.tvCategory.setText(R.string.change_category_select_2)
            }
            ChallengeCategory.Pose -> {
                binding.tvCategory.setText(R.string.change_category_select_3)
            }
            ChallengeCategory.Clothes -> {
                binding.tvCategory.setText(R.string.change_category_select_4)
            }
            ChallengeCategory.Whip ->  {
                binding.tvCategory.setText(R.string.change_category_select_5)
            }
            ChallengeCategory.Handcuffs -> {
                binding.tvCategory.setText(R.string.change_category_select_6)
            }
            ChallengeCategory.VideoCall -> {
                binding.tvCategory.setText(R.string.change_category_select_7)
            }
            ChallengeCategory.Porn -> {
                binding.tvCategory.setText(R.string.change_category_select_8)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.category.collectLatest(::changeCategoryView) }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
            launch { viewModel.isAddEnabled.collectLatest(::enableAddButton) }
        }
    }

    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpDeadlinePicker.isVisible) {
                    enableDeadlinePicker(false)
                    expandAddButton(false)
                    return
                }
                if (viewModel.isEdited()) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.challenge_add_cancel_title),
                        body = requireContext().getString(R.string.challenge_add_cancel_body),
                        cancelButton = requireContext().getString(R.string.challenge_add_cancel_cancel),
                        confirmButton = requireContext().getString(R.string.challenge_add_cancel_confirm),
                        onConfirm = {
                            findNavController().popBackStack()
                        }
                    )
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