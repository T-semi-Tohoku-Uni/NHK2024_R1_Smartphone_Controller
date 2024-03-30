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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // For analog input
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // TODO: Check InputDevice (SOURCE_GAMEPAD, SOURCE_GAMEPAD)

        val axisX = event.getAxisValue(MotionEvent.AXIS_X)   // left stick horizontal
        val axisY = event.getAxisValue(MotionEvent.AXIS_Y)   // left stick vertical
        val axisZ = event.getAxisValue(MotionEvent.AXIS_Z)   // right stick horizontal
        val axisRZ = event.getAxisValue(MotionEvent.AXIS_RZ) // right stick vertical

        return super.onGenericMotionEvent(event)
    }

    // For button press
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L2 -> {
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    // For button release
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }
}