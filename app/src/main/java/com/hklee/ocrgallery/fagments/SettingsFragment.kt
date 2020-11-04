package com.hklee.ocrgallery.fagments

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.hklee.ocrgallery.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if ("license" == preference?.key) {
            activity?.let {
                val intent = Intent(it, OssLicensesMenuActivity::class.java)
                it.startActivity(intent)
            }
            return true
        }

        return super.onPreferenceTreeClick(preference)
    }
}