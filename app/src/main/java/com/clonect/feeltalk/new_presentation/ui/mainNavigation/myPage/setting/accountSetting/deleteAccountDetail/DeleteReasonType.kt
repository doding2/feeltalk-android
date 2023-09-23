package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccountDetail

sealed class DeleteReasonType(val raw: String) {
    object BreakUp: DeleteReasonType("breakUp")
    object NoFunctionality: DeleteReasonType("noFunctionality")
    object BugOrError: DeleteReasonType("bugOrError")
    object Etc: DeleteReasonType("etc")
}
