package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageShare

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import com.clonect.feeltalk.R
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import com.clonect.feeltalk.new_presentation.ui.util.closeRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.extendRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.clonect.feeltalk.new_presentation.ui.util.stateFlow
import com.clonect.feeltalk.new_presentation.ui.util.toBitmap
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by doding2 on 2023/10/24.
 */
@AndroidEntryPoint
class ImageShareFragment : Fragment() {

    companion object {
        const val REQUEST_KEY = "imageShareFragment"
        const val RESULT_KEY_URI = "bitmap"
        const val RESULT_KEY_WIDTH = "width"
        const val RESULT_KEY_HEIGHT = "height"
    }

    private lateinit var binding: FragmentImageShareBinding
    private val viewModel: ImageShareViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentImageShareBinding.inflate(inflater, container, false)
        extendRootViewLayout(requireActivity().window)
        setLightStatusBars(false, requireActivity(), binding.root)
        binding.llActionBar.setPadding(0, getStatusBarHeight(), 0, 0)

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("uri", Uri::class.java)
        } else {
            requireArguments().getParcelable("uri") as? Uri
        }
        viewModel.setUri(uri)
        viewModel.navigatePage()

        loadingDialog = makeLoadingDialog()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ssivImage.maxScale = 10f
            ssivImage.setDoubleTapZoomScale(2f)
            ssivImage.setDoubleTapZoomDuration(150)
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            tvSend.setOnClickListener { sendImageChat() }
        }
    }

    private fun sendImageChat() {
        val bitmap = viewModel.bitmap.value ?: return

        setFragmentResult(
            requestKey = REQUEST_KEY,
            result = bundleOf(
                RESULT_KEY_URI to viewModel.uri.value,
                RESULT_KEY_WIDTH to bitmap.width,
                RESULT_KEY_HEIGHT to bitmap.height,
            )
        )
        onBackCallback.handleOnBackPressed()
    }

    private fun applyUriChanges(uri: Uri?) = lifecycleScope.launch {
        viewModel.setLoading(true)
        val bitmap = withContext(Dispatchers.IO) { uri?.toBitmap(requireContext()) }
        viewModel.setBitmap(bitmap)
        viewModel.setLoading(false)
    }

    private fun applyBitmapChanges(bitmap: Bitmap?) = lifecycleScope.launch {
        binding.ssivImage.setImage(ImageSource.bitmap(bitmap ?: return@launch))
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
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.uri.collectLatest(::applyUriChanges) }
            launch { viewModel.bitmap.collectLatest(::applyBitmapChanges) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeRootViewLayout(requireActivity().window)
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