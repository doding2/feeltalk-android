package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.model.signal.SignalResponse

fun SignalResponse.toSignal(): Signal = when (this.signal.toInt()) {
    0 -> Signal.Zero
    25 -> Signal.Quarter
    50 -> Signal.Half
    75 -> Signal.ThreeFourth
    100 -> Signal.One
    else -> throw IllegalStateException("Signal Long value from server is not in boundary")
}