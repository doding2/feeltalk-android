package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import android.annotation.SuppressLint
import android.app.Dialog
import com.clonect.feeltalk.R
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.FragmentImageDetailBinding
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.clonect.feeltalk.new_presentation.ui.util.stateFlow
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.otaliastudios.zoom.ZoomLayout
import com.skydoves.transformationlayout.TransformationLayout
import com.skydoves.transformationlayout.onTransformationEndContainer
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/10/25.
 */
@AndroidEntryPoint
class ImageDetailFragment : Fragment() {

    companion object {
        const val TAG = "ImageDetailFragment"
    }

    private lateinit var binding: FragmentImageDetailBinding
    private val viewModel: ImageDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.ivImage.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            binding.llActionBar.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(false, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.black), false)
        }
        viewModel.imageChat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("imageChat", ImageChat::class.java)
        } else {
            requireArguments().getParcelable("imageChat") as? ImageChat
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transformationParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("TransformationParams", TransformationLayout.Params::class.java)
        } else {
            arguments?.getParcelable("TransformationParams")
        }
        onTransformationEndContainer(transformationParams)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivDownload.setOnClickListener { downloadImage() }

            zlImage.registerDoubleTouchReset()
        }
    }

    private fun downloadImage() {
        viewModel.downloadImage {
            onBackCallback.handleOnBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ZoomLayout.registerDoubleTouchReset() {
        val detector = GestureDetector(requireContext(), GestureDetector.SimpleOnGestureListener())
        detector.setOnDoubleTapListener(object: GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTap(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTapEvent(p0: MotionEvent): Boolean {
                engine.zoomTo(1f, true)
                return true
            }
        })

        setOnTouchListener { view, event ->
            detector.onTouchEvent(event)
            false
        }
    }


    private fun applyImageChatChanges(imageChat: ImageChat?) = binding.run {
        ivImage.setImageBitmap(imageChat?.bitmap)
        tvNickname.text = imageChat?.chatSender
        tvDate.text = imageChat?.createAt
        root.transitionName = imageChat?.index?.toString()
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
            launch { viewModel::imageChat.stateFlow.collectLatest(::applyImageChatChanges) }
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