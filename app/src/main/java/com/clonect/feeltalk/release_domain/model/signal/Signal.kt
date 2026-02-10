package com.clonect.feeltalk.release_domain.model.signal

import java.io.Serializable

enum class Signal(val raw: String): Serializable {
    Zero("Zero"),
    Quarter("Quarter"),
    Half("Half"),
    ThreeFourth("ThreeFourth"),
    One("One"),
}