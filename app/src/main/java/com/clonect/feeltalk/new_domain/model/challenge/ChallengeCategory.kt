package com.clonect.feeltalk.new_domain.model.challenge

enum class ChallengeCategory(val raw: String) {
    Place("place"),
    Toy("toy"),
    Pose("pose"),
    Clothes("clothes"),
    Whip("whip"),
    Handcuffs("handcuffs"),
    VideoCall("videoCall"),
    Porn("porn");

    companion object {
        fun fromString(value: String): ChallengeCategory {
            return when (value.lowercase()) {
                Place.raw.lowercase() -> Place
                Toy.raw.lowercase() -> Toy
                Pose.raw.lowercase() -> Pose
                Clothes.raw.lowercase() -> Clothes
                Whip.raw.lowercase() -> Whip
                Handcuffs.raw.lowercase() -> Handcuffs
                VideoCall.raw.lowercase() -> VideoCall
                Porn.raw.lowercase() -> Porn
                else -> Place
            }
        }
    }
}