package com.nlinterface.utility

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.os.LocaleList
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.nlinterface.R
import com.nlinterface.activities.SettingsActivity
import java.util.Locale


open class GlobalParameters protected constructor() {
    var visualImpairment: VisualImpairment = VisualImpairment.BLIND
    var colorChoice: ColorChoice = ColorChoice.DEFAULT
    var layoutSwitch : LayoutSwitch = LayoutSwitch.OFF
    var voiceCommandTrigger: VoiceCommandTrigger = VoiceCommandTrigger.BUTTON
    var keepScreenOnSwitch : KeepScreenOn = KeepScreenOn.NO
    var themeChoice: ThemeChoice = ThemeChoice.SYSTEM_DEFAULT

    lateinit var locale: Locale

    // order of the items needs to be the same as in the respective dropdown menus

    enum class VisualImpairment {
        BLIND,
        PARTIALLY,
        COLOR
    }

    enum class ColorChoice {
        DEFAULT,
        RED_GREEN,
        HIGH_CONTRAST
    }

    enum class LayoutSwitch {
        OFF,
        ON
    }

    enum class VoiceCommandTrigger {
        BUTTON,
        VOICE
    }

    enum class KeepScreenOn{
        NO,
        YES
    }

    enum class ThemeChoice {
        SYSTEM_DEFAULT,
        LIGHT,
        DARK
    }

    // make it a Singleton
    companion object {
        private var mInstance: GlobalParameters? = null

        @get:Synchronized
        val instance: GlobalParameters?
            get() {
                if (null == mInstance) {
                    mInstance = GlobalParameters()
                }
                return mInstance
            }
    }

    fun loadPreferences(context: Context) {
        // get saved data from SharedPreferences or create SharedPreferences
        // and set the respective GlobalParameters to the saved data
        val sharedPref: SharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.settings_preferences_key),
            Context.MODE_PRIVATE
        ) ?: return

        val prefImpairment = sharedPref.getString(
            context.resources.getString(R.string.settings_impairment_key),
            GlobalParameters.VisualImpairment.BLIND.toString()
        )
        GlobalParameters.instance!!.visualImpairment =
            GlobalParameters.VisualImpairment.valueOf(prefImpairment!!)

        val prefColor = sharedPref.getString(
            context.resources.getString(R.string.settings_color_key),
            GlobalParameters.ColorChoice.DEFAULT.toString()
        )
        GlobalParameters.instance!!.colorChoice = GlobalParameters.ColorChoice.valueOf(prefColor!!)

        val prefLayout = sharedPref.getString(
            context.resources.getString(R.string.settings_layout_key),
            GlobalParameters.LayoutSwitch.OFF.toString())
        GlobalParameters.instance!!.layoutSwitch = GlobalParameters.LayoutSwitch.valueOf(prefLayout!!)

        val prefVoiceTrigger = sharedPref.getString(
            context.resources.getString(R.string.settings_voice_command_key),
            GlobalParameters.VoiceCommandTrigger.BUTTON.toString()
        )
        GlobalParameters.instance!!.voiceCommandTrigger =
            GlobalParameters.VoiceCommandTrigger.valueOf(prefVoiceTrigger!!)

        val prefKeepScreenOn = sharedPref.getString(
            context.resources.getString(R.string.settings_keep_screen_on_key),
            GlobalParameters.KeepScreenOn.NO.toString())
        GlobalParameters.instance!!.keepScreenOnSwitch = GlobalParameters.KeepScreenOn.valueOf(prefKeepScreenOn!!)

        val prefTheme = sharedPref.getString(
            context.resources.getString(R.string.settings_theme_key),
            GlobalParameters.ThemeChoice.SYSTEM_DEFAULT.toString()
        )
        GlobalParameters.instance!!.themeChoice = GlobalParameters.ThemeChoice.valueOf(prefTheme!!)
    }
    
    fun updateTheme() {
        when (instance!!.themeChoice) {
            ThemeChoice.SYSTEM_DEFAULT -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            ThemeChoice.LIGHT -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            ThemeChoice.DARK -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
    }


}