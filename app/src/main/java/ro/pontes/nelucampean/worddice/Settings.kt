package ro.pontes.nelucampean.worddice

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity

class Settings(private val activity: FragmentActivity?) {
    fun loadPrefInt(prefKey: Int, defValue: Int): Int {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return defValue
        return sharedPref.getInt(activity.getString(prefKey), defValue)
    }
    fun savePrefInt(prefKey: Int, nr: Int) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(activity.getString(prefKey), nr)
            apply()
        }
    }
    fun loadPrefString(prefKey: Int, defValue: String): String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return defValue
        return sharedPref.getString(activity.getString(prefKey), defValue)
    }
    fun savePrefString(prefKey: Int, defValue: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(activity.getString(prefKey), defValue)
            apply()
        }
    }
    fun loadPrefBoolean(prefKey: Int, defValue: Boolean): Boolean {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return defValue
        return sharedPref.getBoolean(activity.getString(prefKey), defValue)
    }
    fun savePrefBoolean(prefKey: Int, defValue:Boolean) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(activity.getString(prefKey), defValue)
            apply()
        }
    }

}