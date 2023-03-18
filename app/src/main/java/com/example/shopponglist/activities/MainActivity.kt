package com.example.shopponglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.shopponglist.R
import com.example.shopponglist.fragments.billing.BillingManager
import com.example.shopponglist.databinding.ActivityMainBinding
import com.example.shopponglist.dialogs.NewListDialog
import com.example.shopponglist.fragments.FragmentManager
import com.example.shopponglist.fragments.NoteFragment
import com.example.shopponglist.fragments.ShopListNamesFragment
import com.example.shopponglist.settings.SettingsActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currentMenuItemId = R.id.shop_list
    private var currentTheme = ""
    private var iAd: InterstitialAd? = null
    private var adShowCounter = 0
    private var adShowCounterMax = 2
    private lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences((this))
        currentTheme = defPref.getString("theme_key", "grey").toString()
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        setBottomNawListener()
        if(!pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false))loadInterAd()
    }


    private fun loadInterAd(){
        val request = com.google.android.gms.ads.AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.inter_ad_id), request, object : InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                iAd = ad
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                iAd = null
            }
        })
    }

    private fun showInterAd(adListener: AdListener){
        if (iAd != null && adShowCounter > adShowCounterMax){
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    iAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                }
            }

            adShowCounter
            iAd?.show(this)
        } else {
            adShowCounter++
            adListener.onFinish()
        }
    }

    private fun setBottomNawListener(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings ->{
                    showInterAd(object : AdListener {
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }
                    })
                }
                R.id.notes ->{
                    showInterAd(object : AdListener {
                        override fun onFinish() {
                            currentMenuItemId = R.id.notes
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity)
                        }

                    })

                }
                R.id.shop_list ->{
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.new_item ->{
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "grey") != currentTheme) recreate()

    }

    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "grey") == "grey"){
            R.style.Theme_ShoppingListGrey
        } else {
            R.style.Theme_ShoppingListPink
        }
    }

    override fun onClick(name: String) {
        Log.d("MyLog", "Name: $name")
    }

    interface AdListener{
        fun onFinish()
    }
}