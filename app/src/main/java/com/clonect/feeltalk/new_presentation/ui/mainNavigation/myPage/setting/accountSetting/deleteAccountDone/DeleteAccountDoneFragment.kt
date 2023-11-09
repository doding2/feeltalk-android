package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccountDone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentDeleteAccountDoneBinding
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by doding2 on 2023/09/23.
 */
@AndroidEntryPoint
class DeleteAccountDoneFragment : Fragment() {

    private lateinit var binding: FragmentDeleteAccountDoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDeleteAccountDoneBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            mcvConfirm.setOnClickListener {
//                navigateToSignUp()
                restartApplication()
            }
        }
    }

    private fun navigateToSignUp() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_deleteAccountDoneFragment_to_signUpFragment)
    }

    private fun restartApplication() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

}