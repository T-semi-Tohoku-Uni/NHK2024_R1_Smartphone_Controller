package com.example.nhk2024_r1_smartphone_controller

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class AreaState {
    SEEDLING, // => 0
    BALL; // => 1

    companion object {
        fun fromOrdinal(ordinal: Int): AreaState = values().getOrElse(ordinal) {AreaState.SEEDLING}
    }
}

object AreaStateSerializer: KSerializer<AreaState> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AreaState", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: AreaState) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): AreaState {
        val ordinal = decoder.decodeInt()
        return AreaState.fromOrdinal(ordinal)
    }
}