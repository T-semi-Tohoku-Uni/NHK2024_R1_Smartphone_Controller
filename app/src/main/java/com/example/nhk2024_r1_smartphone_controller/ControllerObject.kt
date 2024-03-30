package com.example.nhk2024_r1_smartphone_controller

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// TODO: Convert to JSON

@Serializable
data class ControllerObject(
    @SerialName("v_x") private var vx: Int,
    @SerialName("v_y") private var vy: Int,
    @SerialName("omega") private var omega: Int,
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_a") private var btnA: Boolean,
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_b") private var btnB: Boolean,
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_x") private var btnX: Boolean,
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_y") private var btnY: Boolean,
    // TODO: Check following properties
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_lb") private var btnL1: Boolean,
    @Serializable(with = BooleanAsIntSerializer::class) @SerialName("btn_rb") private var btnR1: Boolean
) {

    fun setRobotXYVelocity(
        coordinateX: Float,
        coordinateY: Float
    ) {
        this.vx = ((this.validateJoyConOutput(coordinateX) + 1) * (255 / 2)).toInt()
        this.vy = ((this.validateJoyConOutput(coordinateY) + 1) * (255 / 2)).toInt()
    }

    fun setAngularVelocity(
        coordinateOmega: Float
    ) {
        this.omega = ((this.validateJoyConOutput(coordinateOmega) + 1) * (255 / 2)).toInt()
    }

    fun setButtonA(
        isPushed: Boolean
    ) {
        this.btnA = isPushed
    }

    fun setButtonB(
        isPushed: Boolean
    ) {
        this.btnB = isPushed
    }

    fun setButtonX(
        isPushed: Boolean
    ) {
        this.btnX = isPushed
    }

    fun setButtonY(
        isPushed: Boolean
    ) {
        this.btnY = isPushed
    }

    fun setButtonL1(
        isPushed: Boolean
    ) {
        this.btnL1 = isPushed
    }

    fun setButtonR1(
        isPushed: Boolean
    ) {
        this.btnR1 = isPushed
    }

    /*
        Control safe area function
        use this function when get joy-con analog value
        wrap between -0.3 and 0.3 to 0
     */
    private fun validateJoyConOutput(
        value: Float
    ): Float {
        return if (-0.3 < value && value < 0.3) {
            0f
        } else {
            value
        }
    }
}

@Serializer(forClass = Boolean::class)
object BooleanAsIntSerializer: KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("BooleanASInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(
            if (value) {
                1
            } else {
                0
            }
        )
    }

    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeInt() != 0
    }
}