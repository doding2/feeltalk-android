package com.clonect.feeltalk.presentation.ui.chat

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
//
//    private lateinit var binding: FragmentChatBinding
//    private val viewModel: ChatViewModel by viewModels()
//    @Inject
//    lateinit var adapter: ChatAdapter
//    private lateinit var onBackCallback: OnBackPressedCallback
//
//    private var scrollRemainHeight = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        binding = FragmentChatBinding.inflate(inflater, container, false)
//        preventScreenCapture()
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initActionBar()
//        initRecyclerView()
//
//        collectQuestionDetail()
//        collectPartnerInfo()
//        collectQuestion()
//        collectChatList()
//        collectScrollPosition()
//        collectMyProfileImageUrl()
//        collectPartnerProfileImageUrl()
//        collectIsPartnerAnswered()
//        collectIsScrollBottom()
//
//        binding.btnSendChat.setOnClickListener { sendChat() }
//
//        binding.btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
//
//        binding.fabScrollBottom.setOnClickListener {
//            scrollToBottom()
//            viewModel.setScrollBottom(true)
//        }
//    }
//
//    private fun preventScreenCapture() {
//        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
//    }
//
//
//    private fun sendChat() {
//        binding.etChat.text.toString()
//            .takeIf { it.isNotEmpty() }
//            ?.let {
//                viewModel.sendChat(it) {
//                    scrollToBottom()
//                }
//                binding.etChat.setText("")
//            }
//    }
//
//
//    private fun collectQuestionDetail() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.questionDetail.collectLatest {
//                if (it == null) return@collectLatest
//
//                binding.layoutQuestionContent.removeAllViewsInLayout()
//                binding.textContentDecoration.text = it.header
//                it?.body?.reassembleTextView(R.layout.text_view_question_content)
//            }
//        }
//    }
//    private fun collectPartnerInfo() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.partnerInfo.collectLatest {
//                adapter.setPartnerNickname(it.nickname)
//            }
//        }
//    }
//
//    private fun collectQuestion() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.question.collectLatest {
////                reassembleQuestionTitle(it)
//            }
//        }
//    }
//
//    private fun collectChatList() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.chatList.collectLatest {
//                adapter.differ.submitList(it) {
//                    if (viewModel.isScrollBottom.value) {
//                        scrollToBottom()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun collectMyProfileImageUrl() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            launch {
//                viewModel.myProfileImageUrl.collectLatest {
//                    adapter.setMyProfileUrl(it)
//                }
//            }
//        }
//    }
//
//    private fun collectPartnerProfileImageUrl() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            launch {
//                viewModel.partnerProfileImageUrl.collectLatest {
//                    adapter.setPartnerProfileUrl(it)
//                    binding.ivPartnerProfile.setProfileImageUrl(it)
//                }
//            }
//        }
//    }
//
//    private fun collectIsPartnerAnswered() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.isPartnerAnswered.collectLatest { isAnswered ->
//                binding.apply {
//                    btnSendChat.isEnabled = isAnswered
//                    etChat.isEnabled = isAnswered
//
//                    if (isAnswered) {
//                        btnSendChat.setImageResource(R.drawable.ic_send_chat)
//                        etChat.setHint(R.string.chat_text_field_enabled_hint)
//                    } else {
//                        btnSendChat.setImageResource(R.drawable.ic_waiting_chat)
//                        etChat.setHint(R.string.chat_text_field_disabled_hint)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun collectIsScrollBottom() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.isScrollBottom.collectLatest {
//                if (it) {
//                    binding.fabScrollBottom.hide()
//                } else {
//                    binding.fabScrollBottom.show()
//                }
//            }
//        }
//    }
//
//    private fun collectScrollPosition() = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.scrollPositionState.collectLatest {
//                if (it != null) {
//                    binding.rvChat.scrollToPosition(it)
//                }
//            }
//        }
//    }
//
//
//
//
//    private fun initActionBar() = binding.apply {
//        tvTitle.text = viewModel.partnerInfo.value.nickname
//    }
//
//    private fun initRecyclerView() {
//        binding.rvChat.adapter = adapter
////        binding.rvChat.itemAnimator = null
//        // adjust scroll y when keboard up/down
//        binding.rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
//            if (bottom == oldBottom) return@addOnLayoutChangeListener
//
//            val rangeMoved = oldBottom - bottom
//            if (bottom < oldBottom) {
//                binding.rvChat.scrollBy(0, rangeMoved)
//                scrollRemainHeight = computeRemainScrollHeight()
//                return@addOnLayoutChangeListener
//            }
//
//            if (scrollRemainHeight < -rangeMoved) {
//                binding.rvChat.scrollBy(0, -scrollRemainHeight)
//                return@addOnLayoutChangeListener
//            }
//
//            binding.rvChat.scrollBy(0, rangeMoved)
//        }
//
//        // detect scroll reaches bottom
//        binding.rvChat.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//
//                val isScrollBottom = !recyclerView.canScrollVertically(10)
//                viewModel.setScrollBottom(isScrollBottom)
//                if (!isScrollBottom)
//                    viewModel.updateScrollPosition(null)
//            }
//        })
//    }
//
//
//    private fun ImageView.setProfileImageUrl(url: String?) {
//        Glide.with(this)
//            .load(url)
//            .circleCrop()
//            .fallback(R.drawable.image_my_default_profile)
//            .error(R.drawable.image_my_default_profile)
//            .into(this)
//    }
//
//
//    private fun computeRemainScrollHeight(): Int {
//        return binding.rvChat.run {
//            computeVerticalScrollRange() - computeVerticalScrollOffset() - computeVerticalScrollExtent()
//        }
//    }
//
//    private fun scrollToBottom() {
//        scrollRemainHeight -= computeRemainScrollHeight()
//        val position = adapter.itemCount - 1
//        viewModel.updateScrollPosition(position)
//    }
//
//
//    private fun reassembleQuestionTitle(question: Question) {
//        binding.layoutQuestionContent.removeAllViewsInLayout()
//
////        question.contentPrefix.reassembleTextView(R.layout.text_view_question_content_prefix)
//        question.question.reassembleTextView(R.layout.text_view_question_content)
////        question.contentSuffix.reassembleTextView(R.layout.text_view_question_content_suffix)
//    }
//
//
//    private fun String.reassembleTextView(@LayoutRes resource: Int) {
//        val wordList = this.split(" ")
//
//        wordList.forEachIndexed { index, word ->
//            val textView = LayoutInflater.from(requireContext()).inflate(resource, null) as TextView
//
//            textView.text =
//                if (index == wordList.lastIndex && word != " ") word else "$word "
//
//            binding.layoutQuestionContent.addView(textView)
//        }
//    }
//
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        onBackCallback = object: OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().popBackStack()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        onBackCallback.remove()
//    }
}