package com.clonect.feeltalk.new_domain.model.signal

import java.io.Serializable

enum class Signal(val raw: String): Serializable {
    Seduce("Seduce"),
    Passion("Passion"),
    Skinship("Skinship"),
    Puzzling("Puzzling"),
    Nope("Nope"),
    Tired("Tired"),
}