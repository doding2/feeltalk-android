package com.clonect.feeltalk.new_presentation.ui.mainNavigation.ongoingChallengeDetail

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
import com.clonect.feeltalk.databinding.FragmentOngoingChallengeDetailBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge.showChangeCategoryDialog
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@AndroidEntryPoint
class OngoingChallengeDetailFragment : Fragment() {

    private lateinit var binding: FragmentOngoingChallengeDetailBinding
    private val viewModel: OngoingChallengeDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOngoingChallengeDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        initChallenge()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDatePickerListener()
        setKeyboardInsets()
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivEdit.setOnClickListener { viewModel.setEditMode(true) }
            ivDelete.setOnClickListener { deleteChallenge() }


            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }

            mcvCategory.setOnClickListener {
                enableDeadlinePicker(false)
                changeCategory()
            }

            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }

            mcvEditSquare.setOnClickListener { editChallenge() }
            mcvEditRound.setOnClickListener { editChallenge() }

            mcvCompleteRound.setOnClickListener { completeChallenge() }
        }
    }

    private fun initChallenge() {
        val challenge = if (viewModel.challenge.value == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable("challenge", Challenge::class.java)
            } else {
                arguments?.getSerializable("challenge") as? Challenge
            }
        } else {
            viewModel.challenge.value
        }
        if (challenge != null) {
            viewModel.initChallenge(challenge)
            binding.etTitle.setText(challenge.title)
            binding.etBody.setText(challenge.body)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun completeChallenge() {
        viewModel.completeChallenge()
        onBackCallback.handleOnBackPressed()
    }

    private fun editChallenge() {
        viewModel.editChallenge()
        onBackCallback.handleOnBackPressed()
    }

    private fun deleteChallenge() {
        showConfirmDialog(
            title = requireContext().getString(R.string.delete_challenge_title),
            body = requireContext().getString(R.string.delete_challenge_body),
            cancelButton = requireContext().getString(R.string.delete_challenge_cancel),
            confirmButton = requireContext().getString(R.string.delete_challenge_confirm),
            onConfirm = {
                viewModel.deleteChallenge()
            }
        )
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
            expandEditButton(true)
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

            if (viewModel.isEditMode.value) {
                viewModel.setKeyboardUp(imeHeight != 0)
            }

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


    private fun enableEditMode(enabled: Boolean) = binding.run {
        if (enabled) {
            tvDetailTitle1.setText(R.string.add_challenge_title_1)
            tvDetailTitle2.setText(R.string.add_challenge_title_2)

            ivEdit.visibility = View.GONE
            ivDelete.visibility = View.GONE

            etTitle.isEnabled = true
            etBody.isEnabled = true
            mcvCategory.isEnabled = true
            mcvDeadline.isEnabled = true
            mcvEditSquare.isEnabled = true
            mcvEditRound.isEnabled = true

            mcvCompleteRound.visibility = View.GONE
            mcvEditRound.visibility = View.VISIBLE
            mcvEditSquare.visibility = View.GONE
        } else {
            tvDetailTitle1.setText(R.string.challenge_detail_title_1)
            tvDetailTitle2.setText(R.string.challenge_detail_title_2)

            ivEdit.visibility = View.VISIBLE
            ivDelete.visibility = View.VISIBLE

            etTitle.isEnabled = false
            etBody.isEnabled = false
            mcvCategory.isEnabled = false
            mcvDeadline.isEnabled = false
            mcvEditSquare.isEnabled = false
            mcvEditRound.isEnabled = false

            mcvCompleteRound.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
            mcvEditSquare.visibility = View.GONE
        }
    }


    private fun expandEditButton(enabled: Boolean) = binding.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mcvEditRound.visibility = View.VISIBLE
            mcvEditRound.radius = 0f
            mcvEditRound.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(left = 0, right = 0)
            }
            return@run
        }

        if (dpDeadlinePicker.isVisible) {
            mcvEditSquare.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
            return@run
        }

        if (enabled) {
            mcvEditSquare.visibility = View.VISIBLE
            mcvEditRound.visibility = View.GONE
        } else {
            mcvEditSquare.visibility = View.GONE
            mcvEditRound.visibility = View.VISIBLE
        }
    }

    private fun enableEditButton(enabled: Boolean) = binding.run {
        mcvEditRound.isClickable = enabled
        mcvEditRound.isFocusable = enabled
        mcvEditRound.isEnabled = enabled

        mcvEditSquare.isClickable = enabled
        mcvEditSquare.isFocusable = enabled
        mcvEditSquare.isEnabled = enabled

        if (enabled) {
            mcvEditRound.setCardBackgroundColor(resources.getColor(R.color.black, null))
            mcvEditSquare.setCardBackgroundColor(resources.getColor(R.color.black, null))
            tvEditRound.setTextColor(Color.WHITE)
            tvEditSquare.setTextColor(Color.WHITE)
        } else {
            mcvEditRound.setCardBackgroundColor(resources.getColor(R.color.gray_300, null))
            mcvEditSquare.setCardBackgroundColor(resources.getColor(R.color.gray_300, null))
            tvEditRound.setTextColor(requireContext().getColor(R.color.gray_500))
            tvEditSquare.setTextColor(requireContext().getColor(R.color.gray_500))
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
            requireContext().getString(R.string.add_challenge_d_day_deco) + dDay
        }
    }

    private fun changeViewWhenKeyboardUp(isUp: Boolean) {
        if (isUp) {
            expandEditButton(true)
            enableDeadlinePicker(false)
        } else {
            expandEditButton(false)
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

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isEditMode.collectLatest(::enableEditMode) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeViewWhenKeyboardUp) }
            launch { viewModel.category.collectLatest(::changeCategoryView) }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
            launch { viewModel.isEditEnabled.collectLatest(::enableEditButton) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dpDeadlinePicker.isVisible) {
                    enableDeadlinePicker(false)
                    expandEditButton(false)
                    return
                }
                if (viewModel.isEditMode.value) {
                    if (viewModel.isEdited()) {
                        showConfirmDialog(
                            title = requireContext().getString(R.string.challenge_edit_cancel_title),
                            body = requireContext().getString(R.string.challenge_edit_cancel_body),
                            cancelButton = requireContext().getString(R.string.challenge_edit_cancel_cancel),
                            confirmButton = requireContext().getString(R.string.challenge_edit_cancel_confirm),
                            onConfirm = {
                                viewModel.setEditMode(false)
                                initChallenge()
                            }
                        )
                    } else {
                        viewModel.setEditMode(false)
                        initChallenge()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}