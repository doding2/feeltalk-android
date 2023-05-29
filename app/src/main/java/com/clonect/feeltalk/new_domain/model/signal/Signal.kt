package com.clonect.feeltalk.new_domain.model.signal

import java.io.Serializable

enum class Signal(raw: String): Serializable {
    Seduce("seduce"),
    Passion("passion"),
    Skinship("skinship"),
    Puzzling("puzzling"),
    Nope("nope"),
    Tired("tired")
}