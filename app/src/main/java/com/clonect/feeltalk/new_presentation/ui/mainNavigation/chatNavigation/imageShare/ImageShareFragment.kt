package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageShare

import android.annotation.SuppressLint
import android.app.Dialog
import com.clonect.feeltalk.R
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.FragmentImageShareBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.clonect.feeltalk.new_presentation.ui.util.stateFlow
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/10/24.
 */
@AndroidEntryPoint
class ImageShareFragment : Fragment() {

    private lateinit var binding: FragmentImageShareBinding
    private val viewModel: ImageShareViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentImageShareBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.ivImage.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            binding.llActionBar.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(false, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.black), false)
        }
        viewModel.uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("uri", Uri::class.java)
        } else {
            requireArguments().getParcelable("uri") as? Uri
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            tvSend.setOnClickListener { sendImageChat() }

            ivImage.registerDoubleTouchReset()
        }
    }

    private fun sendImageChat() {
        viewModel.sendImageChat {
            setFragmentResult(
                requestKey = "imageShareFragment",
                result = bundleOf("uri" to viewModel.uri)
            )
            onBackCallback.handleOnBackPressed()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.registerDoubleTouchReset() {
        val detector = GestureDetector(requireContext(), GestureDetector.SimpleOnGestureListener())
        detector.setOnDoubleTapListener(object: GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTap(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTapEvent(p0: MotionEvent): Boolean {
                binding.zlImage.engine.zoomTo(1f, true)
                return true
            }
        })

        binding.zlImage.setOnTouchListener { view, event ->
            detector.onTouchEvent(event)
            false
        }
    }

    private fun applyUriChanges(uri: Uri?) = lifecycleScope.launch {
        binding.run {
            if (uri == null) return@launch

            viewModel.isLoading = true
            ivImage.setImageURI(uri)
            viewModel.isLoading = false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel::isLoading.stateFlow.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel::uri.stateFlow.collectLatest(::applyUriChanges) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
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