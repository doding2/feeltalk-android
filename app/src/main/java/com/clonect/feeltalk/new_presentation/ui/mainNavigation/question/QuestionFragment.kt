package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.PokeSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val viewModel: QuestionViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private var viewModelJob: Job? = null

    @Inject
    lateinit var adapter: QuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.gray_100), true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        collectViewModel()
        navViewModel.setShowQuestionPage(true)

        binding.apply {
            ivScrollTop.setOnClickListener { scrollToTop() }
        }
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(true, activity, binding.root)
    }

    private fun showAnswerBottomSheet(question: Question) {
        navViewModel.setAnswerTargetQuestion(question)
        navViewModel.setShowAnswerSheet(true)
    }


    private fun onQuestionClick(question: Question, itemViewType: Int) {
//        if (itemViewType == QuestionAdapter.TYPE_TODAY_QUESTION
//            && question.myAnswer != null && question.partnerAnswer == null
//            ) {
//            showPokeSnackBar(question.index)
//            return
//        }

        showAnswerBottomSheet(question)
        navViewModel.setShowChatNavigation(false)
    }

    private fun showPokeSnackBar(index: Long) {
        val decorView = activity?.window?.decorView ?: return
        PokeSnackbar.make(
            view = decorView,
            message = requireContext().getString(R.string.home_poke_snackbar_title),
            pokeText = requireContext().getString(R.string.home_poke_snackbar_button),
            duration = Snackbar.LENGTH_SHORT,
            bottomMargin = activity.dpToPx(56f).toInt(),
            onClick = {
                it.dismiss()
            },
            onPoke = {
                it.dismiss()
                viewModel.pressForAnswer(index) {
                    showSnackBar(requireContext().getString(R.string.answer_poke_partner_snack_bar))
                }
            }
        ).show()
    }

    private fun showSnackBar(message: String?) {
        if (message == null) return
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            bottomMargin = activity.dpToPx(56f).toInt(),
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun initRecyclerView() = binding.run {
        rvQuestion.adapter = adapter
        adapter.setOnItemClickListener(::onQuestionClick)

        rvQuestion.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isInTop = !recyclerView.canScrollVertically(-10)
                viewModel.setInQuestionTop(isInTop)
                navViewModel.setInQuestionTop(isInTop)
            }
        })
    }

    private fun scrollToTop() {
        binding.rvQuestion.smoothScrollToPosition(0)
    }

    private fun collectViewModel() {
        viewModelJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pagingQuestion.collectLatest {
                        adapter.submitData(lifecycle, it)
                    }
                }
                launch {
                    viewModel.scrollToTop.collectLatest {
                        if (it) {
                            delay(50)
                            scrollToTop()
                            viewModel.setScrollToTop(false)
                        }
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navViewModel.setShowQuestionPage(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelJob?.cancel()
    }
}