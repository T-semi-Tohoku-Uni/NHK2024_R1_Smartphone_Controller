package com.example.nhk2024_r1_smartphone_controller

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class SeedlingHandPos {
    PICKUP, // => 0
    PUTOUTSIDE, // => 1
    PUTINSIDE,// => 2
    RESET; // => 3


    companion object {
        fun fromOrdinal(ordinal: Int): SeedlingHandPos = values().getOrElse(ordinal) { PICKUP }
    }
}

object SeedlingHandPosSerializer: KSerializer<SeedlingHandPos> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SeedlingHandPos", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: SeedlingHandPos) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): SeedlingHandPos {
        val ordinal = decoder.decodeInt()
        return SeedlingHandPos.fromOrdinal(ordinal)
    }
}