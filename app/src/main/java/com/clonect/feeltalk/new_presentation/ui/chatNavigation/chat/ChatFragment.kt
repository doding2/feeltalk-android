package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.insertSeparators
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.new_domain.model.chat.DividerChat
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.presentation.utils.infoLog
import com.clonect.feeltalk.presentation.utils.showPermissionRequestDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    @Inject
    lateinit var adapter: ChatAdapter

    private var scrollRemainHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        setKeyboardInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        viewModel.cancelJob()
        viewModel.setJob(
            collectViewModel()
        )

        binding.run {
            ivCancel.setOnClickListener { cancel() }

            etTextMessage.addTextChangedListener { viewModel.setTextChat(it?.toString() ?: "") }
            ivSendTextChat.setOnClickListener { sendTextChat() }
            ivExpansion.setOnClickListener { expandChatMedia() }

            ivSetupVoiceChat.setOnClickListener { setupVoiceRecording() }
            ivStartVoiceRecording.setOnClickListener { startVoiceRecording() }
            ivStopVoiceRecording.setOnClickListener { stopVoiceRecording() }
            ivReplayVoiceRecording.setOnClickListener { replayVoiceRecording() }
            ivPauseReplayVoiceRecording.setOnClickListener { pauseVoiceRecordingReplaying() }
            ivSendVoiceChat.setOnClickListener { sendVoiceChat() }

            ivContentsShare.setOnClickListener { navigateToContentsShare() }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isUserInBottom.value) {
            scrollToBottom()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            viewModel.changeChatRoomState(false)
        }
    }


    private fun navigateToContentsShare() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_contentsShareFragment)
    }


    private fun cancel() {
        // 채팅 확장 취소
        viewModel.setExpandChatMedia(false)
        // 보이스 챗 셋업 취소
        viewModel.setVoiceSetupMode(false)
        // 보이스 녹음 취소
        viewModel.cancelVoiceRecordingMode()
        // 보이스 리플레이 취소
        viewModel.stopVoiceRecordingReplay()
        // 보이스 채팅 모두 중단
        adapter.resetVoiceChats()

        binding.run {
            ivCancel.visibility = View.GONE
            ivExpansion.visibility = View.VISIBLE

            vvVoiceVisualizer.reset()
        }
    }


    private fun sendTextChat() {
        viewModel.sendTextChat(
            onStart =  {
                binding.etTextMessage.setText("")
            }
        )
    }


    private fun sendVoiceChat() {
        viewModel.sendVoiceChat {
            cancel()
        }
    }

    private fun setupVoiceRecording() {
        cancel()
        hideKeyboard()
        viewModel.setVoiceSetupMode(true)
    }

    private fun startVoiceRecording() {
        checkAudioPermission { isGranted ->
            if (isGranted) {
                viewModel.setVoiceSetupMode(false)
                viewModel.startVoiceRecording(requireContext(), binding.vvVoiceVisualizer)
            }
        }
    }

    private fun stopVoiceRecording() {
        viewModel.finishVoiceRecording()
    }

    private fun replayVoiceRecording() {
        if (viewModel.isVoiceRecordingReplaying.value && viewModel.isVoiceRecordingReplayPaused.value) {
            viewModel.resumeVoiceRecordingReplay()
        } else {
            viewModel.startVoiceRecordingReplay(requireContext(), binding.vvVoiceVisualizer)
        }
    }

    private fun pauseVoiceRecordingReplaying() {
        viewModel.pauseVoiceRecordingReplay()
    }


    private fun expandChatMedia() = lifecycleScope.launch {
        cancel()
        if (viewModel.isKeyboardUp.value) {
            hideKeyboard()
            delay(100)
        }
        viewModel.toggleExpandChatMedia()
    }


    private fun setRecyclerView() = binding.run {
        rvChat.adapter = adapter.apply {
            setMyNickname("me")
            setPartnerNickname("partner")
        }
        rvChat.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val isInBottom = !recyclerView.canScrollVertically(10)

                if (viewModel.isUserInChat.value == true) {
                    viewModel.setUserInBottom(isInBottom)
                }
            }
        })
    }

    private fun setKeyboardInsets() {
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.systemWindowInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                return@setOnApplyWindowInsetsListener insets

            if (imeHeight == 0) {
                binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, 0, 0, imeHeight)
            }

            insets
        }

        binding.rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom == oldBottom) return@addOnLayoutChangeListener

            val rangeMoved = oldBottom - bottom
            if (bottom < oldBottom) {
                binding.rvChat.scrollBy(0, rangeMoved)
                scrollRemainHeight = computeRemainScrollHeight()
                return@addOnLayoutChangeListener
            }

            if (scrollRemainHeight < -rangeMoved) {
                binding.rvChat.scrollBy(0, -scrollRemainHeight)
                return@addOnLayoutChangeListener
            }

            binding.rvChat.scrollBy(0, rangeMoved)
        }
    }

    private fun computeRemainScrollHeight(): Int {
        return binding.rvChat.run {
            computeVerticalScrollRange() - computeVerticalScrollOffset() - computeVerticalScrollExtent()
        }
    }

    private fun scrollToBottom() {
        scrollRemainHeight -= computeRemainScrollHeight()
        var position = adapter.itemCount - 1
        binding.rvChat.scrollToPosition(position)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etTextMessage.windowToken, 0)
    }


    // audio recording permission
    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        infoLog("audio permission is granted: $isGranted")
        if (!isGranted) {
            showPermissionRequestDialog(
                title = "알림 권한 설정",
                message = "푸쉬 알림을 활성화 하려면 알림 권한을 설정해주셔야 합니다."
            )
        }
    }

    private fun checkAudioPermission(onCompleted: (Boolean) -> Unit) {
        val permission = Manifest.permission.RECORD_AUDIO

        val isAlreadyGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isAlreadyGranted) {
            onCompleted(true)
            return
        }

        audioPermissionLauncher.launch(permission)
    }


    private fun applyKeyboardUp(isUp: Boolean) {
        if (isUp) {
            binding.etTextMessage.requestFocus()
            cancel()
        } else {
            binding.etTextMessage.clearFocus()
        }
    }

    private fun prepareTextChat(message: String) = binding.run {
        val isTextChatMode = message.isNotEmpty()
        if (isTextChatMode) {
            ivSetupVoiceChat.visibility = View.GONE
            ivSendTextChat.visibility = View.VISIBLE
        } else {
            ivSetupVoiceChat.visibility = View.VISIBLE
            ivSendTextChat.visibility = View.GONE
        }
    }

    private fun changeChatMediaView(isExpanded: Boolean) = binding.run {
        llMediaContainer.visibility =
            if (isExpanded) View.VISIBLE
            else View.GONE

        ivExpansion.visibility =
            if (isExpanded) View.GONE
            else View.VISIBLE

        ivCancel.visibility =
            if (isExpanded) View.VISIBLE
            else View.GONE
    }

    private fun changeVoiceSetupView(isSetup: Boolean) = binding.run {
        if (isSetup) {
            ivCancel.visibility = View.VISIBLE
            ivExpansion.visibility = View.GONE

            mcvDefaultBottomBar.visibility = View.GONE
            mcvVoiceSetupBottomBar.visibility = View.VISIBLE
        } else {
            ivCancel.visibility = View.GONE
            ivExpansion.visibility = View.VISIBLE

            mcvDefaultBottomBar.visibility = View.VISIBLE
            mcvVoiceSetupBottomBar.visibility = View.GONE
        }
    }


    private fun changeVoiceRecordingView(isRecording: Boolean) = binding.run {
        if (isRecording) {
            ivCancel.visibility = View.VISIBLE
            ivExpansion.visibility = View.GONE

            mcvDefaultBottomBar.visibility = View.GONE
            mcvVoiceSetupBottomBar.visibility = View.GONE
            mcvVoiceRecordingBottomBar.visibility = View.VISIBLE
        } else {
            ivCancel.visibility = View.GONE
            ivExpansion.visibility = View.VISIBLE

            mcvDefaultBottomBar.visibility = View.VISIBLE
            mcvVoiceSetupBottomBar.visibility = View.GONE
            mcvVoiceRecordingBottomBar.visibility = View.GONE

            viewModel.finishVoiceRecording()
        }
    }

    private fun changeVoiceRecordingFinishedView(isFinished: Boolean) = binding.run {
        if (isFinished) {
            ivStopVoiceRecording.visibility = View.GONE
            ivReplayVoiceRecording.visibility = View.VISIBLE
            ivSendVoiceChat.visibility = View.VISIBLE
        } else {
            ivStopVoiceRecording.visibility = View.VISIBLE
            ivReplayVoiceRecording.visibility = View.GONE
            ivSendVoiceChat.visibility = View.GONE
        }
    }

    private fun changeVoiceRecordingTimeView(recordingTime: Long) = binding.run {
        val totalSeconds = recordingTime / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
        val secondsStr = if (seconds < 10) "0$seconds" else seconds.toString()
        tvRecordTime.text = "$minutesStr:$secondsStr"
    }

    private fun changePauseVoiceRecordingReplayingView(isPaused: Boolean) = binding.run {
        // 리플레이 상태가 아님
        if (!viewModel.isVoiceRecordingReplaying.value) {
            // 리플레이 재생이 끝남
            if (viewModel.isVoiceRecordingReplayCompleted.value) {
                ivReplayVoiceRecording.visibility = View.VISIBLE
            }
            else if (!viewModel.isVoiceRecordingFinished.value){
                ivReplayVoiceRecording.visibility = View.GONE
            } else {
//                ivReplayVoiceRecording.visibility = View.GONE
            }
            ivPauseReplayVoiceRecording.visibility = View.GONE
            return@run
        }
        
        // 리플레이 재생중
        if (!isPaused) {
            ivReplayVoiceRecording.visibility = View.GONE
            ivPauseReplayVoiceRecording.visibility = View.VISIBLE
        }
        // 리플레이 중단됨
        if (isPaused) {
            ivReplayVoiceRecording.visibility = View.VISIBLE
            ivPauseReplayVoiceRecording.visibility = View.GONE
        }
    }



    private fun setBackCallback(isChatShown: Boolean) {
        if (isChatShown) {
            onBackCallback = object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.expandChat.value) {
                        cancel()
                        return
                    }
                    if (viewModel.isVoiceSetupMode.value) {
                        cancel()
                        return
                    }
                    if (viewModel.isVoiceRecordingMode.value) {
                        cancel()
                        return
                    }
                    if (navViewModel.showChatNavigation.value) {
                        navViewModel.toggleShowChatNavigation()
                        cancel()
                        return
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, onBackCallback)
        } else if (::onBackCallback.isInitialized) {
            onBackCallback.remove()
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.textChat.collectLatest(::prepareTextChat) }
            launch { viewModel.expandChat.collectLatest(::changeChatMediaView) }
            launch { viewModel.isVoiceSetupMode.collectLatest(::changeVoiceSetupView) }
            launch { viewModel.isVoiceRecordingMode.collectLatest(::changeVoiceRecordingView) }
            launch { viewModel.isVoiceRecordingFinished.collectLatest(::changeVoiceRecordingFinishedView) }
            launch { viewModel.isVoiceRecordingReplayPaused.collectLatest(::changePauseVoiceRecordingReplayingView) }
            launch { viewModel.voiceRecordTime.collectLatest(::changeVoiceRecordingTimeView) }
            launch { viewModel.isKeyboardUp.collectLatest(::applyKeyboardUp) }

            launch {
                viewModel.scrollToBottom.collectLatest {
                    if (it) scrollToBottom()
                }
            }
            launch {
                viewModel.pagingChat.collectLatest {
                    adapter.submitData(viewLifecycleOwner.lifecycle,
                        it.insertSeparators { before, after ->
                            val beforeCreate = before?.createAt?.substringBefore("T")
                            val afterCreate = after?.createAt?.substringBefore("T")

                            return@insertSeparators if (beforeCreate.isNullOrBlank() && !afterCreate.isNullOrBlank()) {
                                DividerChat(afterCreate)
                            } else if (!beforeCreate.isNullOrBlank() && !afterCreate.isNullOrBlank() && beforeCreate != afterCreate) {
                                DividerChat(afterCreate)
                            } else {
                                null
                            }
                        }
                    )
                }
            }
            launch {
                viewModel.isPartnerInChat.collectLatest {
                    if (it != null) {
                        adapter.setPartnerInChat(it)
                    }
                }
            }
            launch {
                navViewModel.showChatNavigation.collectLatest { isShown ->
                    setBackCallback(isShown)
                    if (isShown) {
                        viewModel.changeChatRoomState(true)
                        if (viewModel.isUserInBottom.value) {
                            scrollToBottom()
                        }
                    }
                    else {
                        hideKeyboard()
                        cancel()
                        viewModel.changeChatRoomState(false)
                    }
                }
            }
        }
    }

}