package com.puntogris.blint.ui.business

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterOnlineBusinessBinding
import com.puntogris.blint.di.App
import com.puntogris.blint.ui.base.BaseFragment

class RegisterOnlineBusinessFragment : BaseFragment<FragmentRegisterOnlineBusinessBinding>(R.layout.fragment_register_online_business),PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient

    override fun initializeViews() {

       // val billingClientLifecycle = (requireActivity().application as App).billingClientLifecycle

        val purchasesUpdateListener =
            PurchasesUpdatedListener { billingResult, purchases ->

            }

        billingClient = BillingClient.newBuilder(requireActivity())
            .setListener(purchasesUpdateListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(listOf("online_business_tier_1"))
                    params.setType(BillingClient.SkuType.SUBS)
                    val test = params.build()
                    billingClient.querySkuDetailsAsync(test
                    ) { p0, p1 ->
                        val flowParams = p1?.get(0)?.let {
                            BillingFlowParams.newBuilder()
                                .setSkuDetails(it)
                                .build()
                        }
                        val responseCode = flowParams?.let {
                            billingClient.launchBillingFlow(
                                requireActivity(),
                                it
                            ).responseCode
                        }
                    }

                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

     //   billingClient.queryPurchasesAsync()

    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
              //  handlePurchase(purchase)
                println("yay")
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            println("cance")
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            println("erro")
            // Handle any other error codes.
        }
    }
}