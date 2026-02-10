package com.clonect.feeltalk.release_data.mapper

import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.model.signal.MySignalResponse
import com.clonect.feeltalk.release_domain.model.signal.PartnerSignalResponse

fun MySignalResponse.toSignal(): Signal = when (this.mySignal.toInt()) {
    0 -> Signal.Zero
    25 -> Signal.Quarter
    50 -> Signal.Half
    75 -> Signal.ThreeFourth
    100 -> Signal.One
    else -> throw IllegalStateException("Signal Long value from server is not in boundary")
}
fun PartnerSignalResponse.toSignal(): Signal = when (this.partnerSignal.toInt()) {
    0 -> Signal.Zero
    25 -> Signal.Quarter
    50 -> Signal.Half
    75 -> Signal.ThreeFourth
    100 -> Signal.One
    else -> throw IllegalStateException("Signal Long value from server is not in boundary")
}