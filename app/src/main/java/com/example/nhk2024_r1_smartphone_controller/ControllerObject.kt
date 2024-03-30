package com.example.nhk2024_r1_smartphone_controller

// TODO: Convert to JSON

class ControllerObject {
    private var vx: Int
    private var vy: Int
    private var omega: Int
    private var btnA: Boolean
    private var btnB: Boolean
    private var btnX: Boolean
    private var btnY: Boolean
    private var btnL1: Boolean
    private var btnR1: Boolean

    constructor() {
        this.vx = 127      // 0
        this.vy = 127      // 0
        this.omega = 127   // 0
        this.btnA = false  // Release State
        this.btnB = false  // Release State
        this.btnX = false  // Release State
        this.btnY = false  // Release State
        this.btnL1 = false // Release State
        this.btnR1 = false // Release State
    }

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