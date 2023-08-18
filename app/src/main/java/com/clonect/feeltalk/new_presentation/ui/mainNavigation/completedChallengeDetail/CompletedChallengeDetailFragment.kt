package com.clonect.feeltalk.new_presentation.ui.mainNavigation.completedChallengeDetail

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCompletedChallengeDetailBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge.showChangeCategoryDialog
import com.clonect.feeltalk.new_presentation.ui.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CompletedChallengeDetailFragment : Fragment() {

    private lateinit var binding: FragmentCompletedChallengeDetailBinding
    private val viewModel: CompletedChallengeDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private val loadingDialog by lazy { makeLoadingDialog() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCompletedChallengeDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        initChallenge()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDatePickerListener()
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivDelete.setOnClickListener { deleteChallenge() }

            etTitle.addTextChangedListener { viewModel.setTitle(it?.toString()) }
            etBody.addTextChangedListener { viewModel.setBody(it?.toString()) }

            mcvCategory.setOnClickListener {
                enableDeadlinePicker(false)
                changeCategory()
            }

            mcvDeadline.setOnClickListener { enableDeadlinePicker(true) }
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
            binding.run {
                viewModel.initChallenge(challenge)
                etTitle.setText(challenge.title)
                etBody.setText(challenge.body)

                etTitle.isEnabled = false
                etBody.isEnabled = false
                mcvCategory.isEnabled = false
                mcvDeadline.isEnabled = false
                mcvCompleteRound.isEnabled = false
            }
        } else {
            findNavController().popBackStack()
        }
    }


    private fun deleteChallenge() {
        showConfirmDialog(
            title = requireContext().getString(R.string.delete_challenge_title),
            body = requireContext().getString(R.string.delete_challenge_body),
            cancelButton = requireContext().getString(R.string.delete_challenge_cancel),
            confirmButton = requireContext().getString(R.string.delete_challenge_confirm),
            onConfirm = {
                viewModel.deleteChallenge {
                    findNavController().popBackStack()
                }
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


    private fun changeDeadlineView(deadline: Date) {
        val formatter = SimpleDateFormat("yyyy년 M월 dd일까지", Locale.getDefault())
        val str = formatter.format(deadline)
        binding.tvDeadline.text = str
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
            launch { viewModel.category.collectLatest(::changeCategoryView) }
            launch { viewModel.deadline.collectLatest(::changeDeadlineView) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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