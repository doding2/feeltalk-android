package com.clonect.feeltalk.release_presentation.ui.mainNavigation.home.signal

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Point
import com.clonect.feeltalk.databinding.FragmentSignalBinding
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.release_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import com.clonect.feeltalk.release_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.release_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.release_presentation.ui.util.measure
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

@AndroidEntryPoint
class SignalFragment : Fragment() {

    private lateinit var binding: FragmentSignalBinding
    private val viewModel: SignalViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private var viewModelJob: Job? = null
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignalBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
        }
        measureSignalView()
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        traceTouchOnTrack()
        collectViewModel()

        binding.run {
            mcvSendSignal.setOnClickListener { sendSignal() }
        }
    }

    private fun initSignal() {
        val angle = when (viewModel.signal.value) {
            Signal.Zero -> 204.77f
            Signal.Quarter -> 147f
            Signal.Half -> 90f
            Signal.ThreeFourth -> 33f
            Signal.One -> 335.22f
            else -> 147f
        }
        viewModel.setAngle(angle)
    }

    private fun sendSignal() {
        viewModel.changeMySignal {
            navViewModel.setShowSignalSheet(false)
        }
    }


    private fun measureSignalView() = lifecycleScope.launch {
        binding.run {
            val pointerWidth = ivPointer.measure { width } / 2
            val signalRadius = ivSignal.measure { width } / 2
            val xPoint = ivSignal.measure { x }
            val yPoint = ivSignal.measure { y }
            val center = Point(
                x = xPoint + signalRadius - pointerWidth,
                y = yPoint + signalRadius - pointerWidth - activity.dpToPx(9f)
            )
            viewModel.setCenterPoint(center)

            val diameter = ivTrack.measure { width } / 2
            viewModel.setDiameter(diameter)

            delay(100)
            initSignal()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun traceTouchOnTrack() = lifecycleScope.launch {
        binding.run {
            clSignal.setOnTouchListener { view, motionEvent ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                if (motionEvent.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }

                val x = motionEvent.x
                val y = motionEvent.y

                val center = viewModel.centerPoint.value ?: return@setOnTouchListener false
                val height = abs(center.x - x)
                val width = abs(center.y - y)
                val tan = width / height
                val rawAngle = (atan(tan) * 180 / Math.PI + 360) % 360
                // 1 사분면
                val angle = if (x > center.x && y <= center.y) {
                    rawAngle
                }
                // 4 사분면
                else if (x > center.x && y > center.y) {
                    360 - rawAngle
                }
                // 3 사분면
                else if (x <= center.x && y > center.y) {
                    rawAngle + 180
                }
                // 2 사분면
                else  {
                    180 - rawAngle
                }
                viewModel.setAngle(angle.toFloat())

                true
            }
        }
    }


    private fun applyAngleChange(angle: Float?) = lifecycleScope.launch {
        binding.run {
            if (angle == null) return@launch
            val center = viewModel.centerPoint.value ?: return@launch
            val diameter = viewModel.diameter.value ?: return@launch

            val discreteAngle = if ((angle > 270 && angle <= 360) || angle == 0f) {
                viewModel.setSignal(Signal.One)
                335.22f
            } else if (angle < 60 && angle >= 0) {
                viewModel.setSignal(Signal.ThreeFourth)
                33f
            } else if (angle < 120 && angle >= 60) {
                viewModel.setSignal(Signal.Half)
                90f
            } else if (angle < 180 && angle > 120) {
                viewModel.setSignal(Signal.Quarter)
                147f
            } else {
                viewModel.setSignal(Signal.Zero)
                204.77f
            }

            val trackWidth = activity.dpToPx(20f)
            val pointerMargin = activity.dpToPx(12f) * 2
            val r = diameter - trackWidth - pointerMargin

            var dx = abs(r * cos(discreteAngle * Math.PI / 180f)).toFloat()
            var dy = abs(r * sin(discreteAngle * Math.PI / 180f)).toFloat()
            // 1 사분면
            if (discreteAngle < 90) {
                dx *= 1
                dy *= -1
            }
            // 2 사분면
            else if (discreteAngle < 180) {
                dx *= -1
                dy *= -1
            }
            // 3 사분면
            else if (discreteAngle < 270) {
                dx *= -1
                dy *= 1
            }
            // 4 사분면
            else {
                dx *= 1
                dy *= 1
            }

            ivPointer.rotation = 360 - (discreteAngle - 90)
            ivPointer.x = center.x + dx
            ivPointer.y = center.y + dy
        }
    }


    private fun applySignalChange(signal: Signal?) = binding.run {
        mcvSignal0.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
        mcvSignal25.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
        mcvSignal50.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
        flSignal75Dot.setBackgroundColor(requireContext().getColor(R.color.gray_400))
        mcvSignal100.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))

        when (signal) {
            Signal.Zero -> {
                tvSignalSubtitle.setText(R.string.signal_subtitle_0)
                tvSignalPercent.setText(R.string.signal_percent_0)
                tvSignalPercent.setTextColor(requireContext().getColor(R.color.signal_0))
                mcvSignalPercent.strokeColor = requireContext().getColor(R.color.signal_0)
                ivSignal.setImageResource(R.drawable.n_image_signal_0)
                mcvSignal0.setCardBackgroundColor(Color.BLACK)
            }
            Signal.Quarter -> {
                tvSignalSubtitle.setText(R.string.signal_subtitle_25)
                tvSignalPercent.setText(R.string.signal_percent_25)
                tvSignalPercent.setTextColor(requireContext().getColor(R.color.signal_25))
                mcvSignalPercent.strokeColor = requireContext().getColor(R.color.signal_25)
                ivSignal.setImageResource(R.drawable.n_image_signal_25)
                mcvSignal25.setCardBackgroundColor(requireContext().getColor(R.color.signal_25))
            }
            Signal.Half -> {
                tvSignalSubtitle.setText(R.string.signal_subtitle_50)
                tvSignalPercent.setText(R.string.signal_percent_50)
                tvSignalPercent.setTextColor(requireContext().getColor(R.color.signal_50))
                mcvSignalPercent.strokeColor = requireContext().getColor(R.color.signal_50)
                ivSignal.setImageResource(R.drawable.n_image_signal_50)
                mcvSignal50.setCardBackgroundColor(requireContext().getColor(R.color.signal_50))
            }
            Signal.ThreeFourth -> {
                tvSignalSubtitle.setText(R.string.signal_subtitle_75)
                tvSignalPercent.setText(R.string.signal_percent_75)
                tvSignalPercent.setTextColor(requireContext().getColor(R.color.signal_75))
                mcvSignalPercent.strokeColor = requireContext().getColor(R.color.signal_75)
                ivSignal.setImageResource(R.drawable.n_image_signal_75)
                flSignal75Dot.background = AppCompatResources.getDrawable(requireContext(), R.drawable.n_background_gradient_signal_75)
            }
            Signal.One -> {
                tvSignalSubtitle.setText(R.string.signal_subtitle_100)
                tvSignalPercent.setText(R.string.signal_percent_100)
                tvSignalPercent.setTextColor(requireContext().getColor(R.color.signal_100))
                mcvSignalPercent.strokeColor = requireContext().getColor(R.color.signal_100)
                ivSignal.setImageResource(R.drawable.n_image_signal_100)
                mcvSignal100.setCardBackgroundColor(requireContext().getColor(R.color.signal_100))
            }
            null -> return@run
        }
    }


    private fun showLoading(isLoading: Boolean) {
//        if (isLoading) {
//            loadingDialog.show()
//        } else {
//            loadingDialog.dismiss()
//        }
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

    private fun applyShowSignalSheetChanges(isShow: Boolean) {
        if (isShow) {
            viewModel.getMySignal {
                initSignal()
            }
        }
    }

    private fun collectViewModel() {
        viewModelJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.isLoading.collectLatest(::showLoading) }
                launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
                launch { viewModel.angle.collectLatest(::applyAngleChange) }
                launch { viewModel.signal.collectLatest(::applySignalChange) }
                launch { navViewModel.showSignalSheet.collectLatest(::applyShowSignalSheetChanges) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelJob?.cancel()
    }
}