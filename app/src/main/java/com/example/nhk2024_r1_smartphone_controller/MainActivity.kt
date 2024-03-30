package com.example.nhk2024_r1_smartphone_controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.InputDevice
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    // TODO: Add GameControllerStateClass
    private lateinit var controllerObject: ControllerObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.controllerObject = ControllerObject()
    }

    // For analog input
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // TODO: Check InputDevice (SOURCE_GAMEPAD, SOURCE_GAMEPAD)

        val axisX = event.getAxisValue(MotionEvent.AXIS_X)   // left stick horizontal
        val axisY = event.getAxisValue(MotionEvent.AXIS_Y)   // left stick vertical
        val axisZ = event.getAxisValue(MotionEvent.AXIS_Z)   // right stick horizontal

        // val axisRZ = event.getAxisValue(MotionEvent.AXIS_RZ) // right stick vertical

        this.controllerObject.setRobotXYVelocity(axisX, axisY)
        this.controllerObject.setAngularVelocity(axisZ)

        // TODO: Send data to Raspberrypi

        return super.onGenericMotionEvent(event)
    }

    // For button press
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(true)
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(true)
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(true)
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(true)
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(true)
            }
            KeyEvent.KEYCODE_BUTTON_L2 -> {
                this.controllerObject.setButtonR1(true)
            }
        }

        // TODO: Send data to Raspberrypi

        return super.onKeyDown(keyCode, event)
    }

    // For button release
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(false)
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(false)
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(false)
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(false)
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(false)
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(false)
            }
        }

        // TODO: Send data to Raspberrypi

        return super.onKeyUp(keyCode, event)
    }
}