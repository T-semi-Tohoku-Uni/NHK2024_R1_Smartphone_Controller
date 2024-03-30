package com.example.nhk2024_r1_smartphone_controller

class ControllerObject {
    // TODO: create ValueObject of RobotMtion
    private val vx: Int
    private val vy: Int
    private val omega: Int
    private val btnA: Int
    private val btnB: Int
    private val btnX: Int
    private val btnY: Int

    constructor(
        v_x: Int,
        v_y: Int,
        omega: Int,
        btnA: Int,
        btnB: Int,
        btnX: Int,
        btnY: Int
    ) {
        this.vx = v_x
        this.vy = v_y
        this.omega = omega
        this.btnA = btnA
        this.btnB = btnB
        this.btnX = btnX
        this.btnY = btnY
    }


}