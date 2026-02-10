package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.bubble

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.model.chat.AddChallengeChat
import com.clonect.feeltalk.release_domain.model.chat.AnswerChat
import com.clonect.feeltalk.release_domain.model.chat.ChallengeChat
import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.model.chat.DividerChat
import com.clonect.feeltalk.release_domain.model.chat.ImageChat
import com.clonect.feeltalk.release_domain.model.chat.PokeChat
import com.clonect.feeltalk.release_domain.model.chat.QuestionChat
import com.clonect.feeltalk.release_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.release_domain.model.chat.TextChat
import com.clonect.feeltalk.release_domain.model.chat.VoiceChat
import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_presentation.ui.FeeltalkApp
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat.ChatAdapter
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat.ChatViewModel
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.contentsShare.ContentsShareFragment
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.imageDetail.ImageDetailActivity
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.imageShare.ImageShareFragment
import com.clonect.feeltalk.release_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.release_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.release_presentation.ui.util.showOneButtonDialog
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import com.clonect.feeltalk.mvp_presentation.utils.showPermissionRequestDialog
import com.google.android.material.snackbar.Snackbar
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BubbleChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    lateinit var adapter: ChatAdapter

    private var scrollRemainHeight = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        setKeyboardInsets()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallback)
        requireParentFragment().requireParentFragment().apply {
            setFragmentResultListener(ContentsShareFragment.REQUEST_KEY_QUESTION) { _, bundle ->
                val question = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) bundle.getParcelable(
                    ContentsShareFragment.RESULT_KEY_QUESTION, Question::class.java)
                else bundle.getParcelable(ContentsShareFragment.RESULT_KEY_QUESTION) as? Question
                viewModel.sendQuestionChat(question)
            }
            setFragmentResultListener(ContentsShareFragment.REQUEST_KEY_CHALLENGE) { _, bundle ->
                val challenge = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) bundle.getParcelable(
                    ContentsShareFragment.RESULT_KEY_CHALLENGE, Challenge::class.java)
                else bundle.getParcelable(ContentsShareFragment.RESULT_KEY_CHALLENGE) as? Challenge
                viewModel.sendChallengeChat(challenge)
            }
            setFragmentResultListener(ImageShareFragment.REQUEST_KEY) { _, bundle ->
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) bundle.getParcelable(ImageShareFragment.RESULT_KEY_URI, Uri::class.java)
                else bundle.getParcelable(ImageShareFragment.RESULT_KEY_URI) as? Uri
                val width = bundle.getInt(ImageShareFragment.RESULT_KEY_WIDTH, -1)
                val height = bundle.getInt(ImageShareFragment.RESULT_KEY_HEIGHT, -1)
                viewModel.sendImageChat(requireContext(), uri, width, height)
            }
        }
        setRecyclerView()
        return binding.root
    }

    val onBackCallback = object: OnBackPressedCallback(true) {
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

            this.isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivCancel.setOnClickListener { cancel() }

            etTextMessage.addTextChangedListener { viewModel.setTextChat(it?.toString() ?: "") }
            etTextMessage.setOnClickListener { cancel() }
            ivSendTextChat.setOnClickListener { sendTextChat() }
            ivExpansion.setOnClickListener {
                expandChatMedia()
                hideKeyboard()
            }

            ivSetupVoiceChat.setOnClickListener { setupVoiceRecording() }
            ivStartVoiceRecording.setOnClickListener { startVoiceRecording() }
            ivStopVoiceRecording.setOnClickListener { stopVoiceRecording() }
            ivReplayVoiceRecording.setOnClickListener { replayVoiceRecording() }
            ivPauseReplayVoiceRecording.setOnClickListener { pauseVoiceRecordingReplaying() }
            ivSendVoiceChat.setOnClickListener { sendVoiceChat() }

            ivContentsShare.setOnClickListener { navigateToContentsShare() }
            ivAlbum.setOnClickListener { selectAlbumImage() }
            ivCamera.setOnClickListener { selectCameraImage() }
        }

    }

    override fun onResume() {
        super.onResume()
        infoLog("Bubble Resume")
        onBackCallback.isEnabled = true
        FeeltalkApp.setUserInChat(true)
        lifecycleScope.launch {
            viewModel.changeChatRoomState(true)
        }
        if (viewModel.isUserInBottom.value) {
            scrollToBottom()
        }
    }

    override fun onPause() {
        super.onPause()
        infoLog("Bubble Paused")
        cancel()
        FeeltalkApp.setUserInChat(false)
        lifecycleScope.launch {
            viewModel.changeChatRoomState(false)
        }
    }


    private fun navigateToContentsShare() {
        val bundle = bundleOf("fromBubble" to true)
        findNavController()
            .navigate(R.id.action_bubbleChatFragment_to_contentsShareFragment2, bundle)
    }

    private fun navigateToImageDetail(view: View, imageChat: ImageChat) {
        val transformationLayout = view as? TransformationLayout ?: return
        val intent = Intent(requireContext(), ImageDetailActivity::class.java)
        intent.putExtra("imageChat", imageChat.copy())
        TransformationCompat.startActivity(transformationLayout, intent)
    }

    private fun navigateToImageShare(uri: Uri) {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bubbleChatFragment_to_imageShareFragment2, bundleOf("uri" to uri))
    }

    private fun selectCameraImage() {
        cancel()
        val intent = Intent("android.media.action.IMAGE_CAPTURE").apply {
            val file = File(requireContext().cacheDir, Constants.IMAGE_CACHE_FILE_NAME)
            val contentUri = FileProvider.getUriForFile(requireContext(), "com.clonect.feeltalk.fileprovider", file)
            putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        }
        cameraLauncher.launch(intent)
    }

    private fun selectAlbumImage() {
        cancel()
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

        albumLauncher.launch(intent)
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK)
            return@registerForActivityResult

        val file = File(requireContext().cacheDir, Constants.IMAGE_CACHE_FILE_NAME)
        val contentUri = FileProvider.getUriForFile(requireContext(), "com.clonect.feeltalk.fileprovider", file) ?: return@registerForActivityResult

        navigateToImageShare(contentUri)
    }

    private val albumLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val intent = it.data ?: return@registerForActivityResult
        val uri = intent.data ?: return@registerForActivityResult
        navigateToImageShare(uri)
    }

    private fun navigateToAnswer(question: Question) {
//        navViewModel.setShowChatNavigation(false)
//        navViewModel.setAnswerTargetQuestion(question)
//        navViewModel.setShowAnswerSheet(true)
    }

    private fun navigateToAnswer(questionIndex: Long) = lifecycleScope.launch {
        val question = viewModel.getQuestion(questionIndex) ?: return@launch
        navigateToAnswer(question)
    }

    private fun navigateToChallengeDetail(challenge: Challenge) = lifecycleScope.launch {
        val loadedChallenge = viewModel.getChallenge(challenge.index)
            ?: run {
                showOneButtonDialog(
                    title = requireContext().getString(R.string.deleted_challenge_chat_title),
                    body = requireContext().getString(R.string.deleted_challenge_chat_body),
                    onConfirm = {}
                )
                return@launch
            }
        if (loadedChallenge.isCompleted) navigateToCompletedChallengeDetail(loadedChallenge)
        else navigateToOngoingChallengeDetail(loadedChallenge)
    }

    private fun navigateToOngoingChallengeDetail(challenge: Challenge) {
        val bundle = bundleOf("challenge" to challenge)
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bubbleChatFragment_to_ongoingDetailFragment2, bundle)
    }

    private fun navigateToCompletedChallengeDetail(challenge: Challenge) {
        val bundle = bundleOf("challenge" to challenge)
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bubbleChatFragment_to_completedDetailFragment2, bundle)
    }


    private fun cancel(includeVoiceChats: Boolean = true) {
        // 채팅 확장 취소
        viewModel.setExpandChatMedia(false)
        // 보이스 챗 셋업 취소
        viewModel.setVoiceSetupMode(false)
        // 보이스 녹음 취소
        viewModel.cancelVoiceRecordingMode()
        // 보이스 리플레이 취소
        viewModel.stopVoiceRecordingReplay()
        // 보이스 채팅 모두 중단
        if (includeVoiceChats) {
            adapter.resetVoiceChats()
        }

        binding.run {
            ivCancel.visibility = View.GONE
            ivExpansion.visibility = View.VISIBLE

            vvVoiceVisualizer.reset()
        }
    }


    private fun sendTextChat() {
        viewModel.sendTextChat {
            binding.etTextMessage.setText("")
        }
    }


    private fun sendVoiceChat() {
        viewModel.sendVoiceChat(requireContext()) {
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

    // For scrolling auto when new chat arrived
    private val dataObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            val snapshot = adapter.snapshot()
            if (positionStart == 0 && viewModel.isUserInBottom.value) {
                scrollToBottom()
                return
            }
            if (positionStart == 0) return
            if (positionStart >= snapshot.size) return
            val item = adapter.snapshot()[positionStart] ?: return

            val isNextDayItemInserted = snapshot.size - 1 < positionStart + 2 || snapshot[positionStart + 2] == null
            if (item is DividerChat && isNextDayItemInserted) {
                val dividerFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val now = dividerFormat.format(Date())
                if (item.createAt != now) return

                val nextItem = snapshot[positionStart + 1]
                if (nextItem is ImageChat) {
                    // delay for loading image
                    lifecycleScope.launch {
                        delay(200)
                        scrollToBottom()
                    }
                } else {
                    scrollToBottom()
                }
                return
            }

            val shouldScroll = (item.chatSender == "me" && item.sendState == Chat.ChatSendState.Sending)
                    || (viewModel.isUserInBottom.value && item.chatSender == "partner")
            if (shouldScroll) {
                if (item is ImageChat) {
                    // delay for loading image
                    lifecycleScope.launch {
                        delay(200)
                        scrollToBottom()
                    }
                    return
                }
                scrollToBottom()
            }
        }
    }

    private fun setRecyclerView() = binding.run {
        rvChat.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(ChatAdapter.TYPE_TEXT_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_TEXT_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_SIGNAL_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_SIGNAL_PARTNER, 0)
//            setMaxRecycledViews(ChatAdapter.TYPE_VOICE_MINE, 0)
//            setMaxRecycledViews(ChatAdapter.TYPE_VOICE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_IMAGE_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_IMAGE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_ADD_CHALLENGE_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_ADD_CHALLENGE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_COMPLETE_CHALLENGE_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_COMPLETE_CHALLENGE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_CHALLENGE_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_CHALLENGE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_ANSWER_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_ANSWER_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_QUESTION_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_QUESTION_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_POKE_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_POKE_PARTNER, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_RESET_PARTNER_PASSWORD_MINE, 0)
            setMaxRecycledViews(ChatAdapter.TYPE_RESET_PARTNER_PASSWORD_PARTNER, 0)
        })
        rvChat.setItemViewCacheSize(512)
        adapter = ChatAdapter()
        rvChat.adapter = adapter.apply {
            setMyNickname("me")
            setOnClickItem(::onClickChat)
            setOnRetry(::onRetryChat)
            setOnCancel(::onCancelChat)
            registerAdapterDataObserver(dataObserver)
            addLoadStateListener {
                if (it.prepend is LoadState.Error && !viewModel.chatPagingRetryLock.isLocked) {
                    lifecycleScope.launch {
                        viewModel.chatPagingRetryLock.withLock {
                            delay(10000)
                            retry()
                        }
                    }
                }
            }
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

    private fun onClickChat(view: View, chat: Chat) {
        when (chat) {
            is ImageChat -> navigateToImageDetail(view, chat)
            is ChallengeChat -> navigateToChallengeDetail(chat.challenge)
            is AddChallengeChat -> navigateToChallengeDetail(chat.challenge)
            is PokeChat -> navigateToAnswer(chat.questionIndex)
            is AnswerChat -> navigateToAnswer(chat.question)
            is ResetPartnerPasswordChat -> viewModel.resetPartnerPassword(
                index = chat.index,
                onExpired = {
                    showOneButtonDialog(
                        title = requireContext().getString(R.string.partner_reset_help_chat_expired_title),
                        body = requireContext().getString(R.string.partner_reset_help_chat_expired_body),
                        onConfirm = {}
                    )
                },
                onSuccess = {
                    showOneButtonDialog(
                        title = requireContext().getString(R.string.partner_reset_help_chat_success_title),
                        body = requireContext().getString(R.string.partner_reset_help_chat_success_body),
                        onConfirm = {}
                    )
                }
            )
        }
    }

    private fun onRetryChat(chat: Chat) {
        viewModel.cancelFailedChat(chat)
        when (chat) {
            is TextChat -> viewModel.sendTextChat(retryChat = chat)
            is VoiceChat -> viewModel.sendVoiceChat(context = requireContext(), retryChat = chat)
            is ImageChat -> viewModel.sendImageChat(context = requireContext(), retryChat = chat)
            is QuestionChat -> viewModel.sendQuestionChat(question = chat.question)
            is ChallengeChat -> viewModel.sendChallengeChat(challenge = chat.challenge)
        }
    }

    private fun onCancelChat(chat: Chat) {
        viewModel.cancelFailedChat(chat)
    }

    private fun setKeyboardInsets() {
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
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
        val position = adapter.itemCount - 1
        binding.rvChat.scrollToPosition(position)
        if (viewModel.isUserInChat.value == true) {
            viewModel.setUserInBottom(true)
        }
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
            cancel(includeVoiceChats = false)
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

    private fun applyPartnerInfoChanges(partnerInfo: PartnerInfo?) {
        binding.tvNickname.text = partnerInfo?.nickname
        adapter.setPartnerNickname(partnerInfo?.nickname)
    }

    private fun applyPartnerSignalChanges(signal: Signal?) {
        if (signal == null) return
        adapter.setPartnerSignal(signal)
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
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.textChat.collectLatest(::prepareTextChat) }
            launch { viewModel.expandChat.collectLatest(::changeChatMediaView) }
            launch { viewModel.isVoiceSetupMode.collectLatest(::changeVoiceSetupView) }
            launch { viewModel.isVoiceRecordingMode.collectLatest(::changeVoiceRecordingView) }
            launch { viewModel.isVoiceRecordingFinished.collectLatest(::changeVoiceRecordingFinishedView) }
            launch { viewModel.isVoiceRecordingReplayPaused.collectLatest(::changePauseVoiceRecordingReplayingView) }
            launch { viewModel.voiceRecordTime.collectLatest(::changeVoiceRecordingTimeView) }
            launch { viewModel.isKeyboardUp.collectLatest(::applyKeyboardUp) }
            launch { viewModel.partnerInfo.collectLatest(::applyPartnerInfoChanges) }
            launch { viewModel.partnerSignal.collectLatest(::applyPartnerSignalChanges) }
            launch {
                viewModel.pagingChat.collectLatest {
                    adapter.submitData(requireParentFragment().lifecycle, it)
                }
            }
            launch {
                viewModel.isPartnerInChat.collectLatest {
                    if (it != null) {
                        adapter.setPartnerInChat(it)
                    }
                }
            }
        }
    }
}