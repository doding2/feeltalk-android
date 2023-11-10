package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.model.signal.SignalResponse

fun SignalResponse.toSignal(): Signal = when (this.signal.toInt()) {
    1 -> Signal.Zero
    2 -> Signal.Quarter
    3 -> Signal.Half
    4 -> Signal.ThreeFourth
    5 -> Signal.One
    else -> throw IllegalStateException("Signal Long value from server is not in boundary")
}