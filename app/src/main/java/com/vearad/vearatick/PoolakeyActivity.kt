package com.vearad.vearatick

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vearad.vearatick.databinding.ActivityPoolakeyBinding
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.request.PurchaseRequest


class PoolakeyActivity : AppCompatActivity() {

    private var snackbar: Snackbar? = null

    private val TAG: String = "ActivityPoolakey_log"
    lateinit var binding: ActivityPoolakeyBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoolakeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBck.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        snackbar = Snackbar.make(binding.root, "عدم اتصال به اینترنت!", Snackbar.LENGTH_INDEFINITE)
            .setAction("اتصال") {}
        snackbar?.view?.visibility = View.INVISIBLE

        if (networkInfo != null && networkInfo.isConnected) {
            // Internet connection is available, you can perform network operations.\
            snackbar?.dismiss() // بستن Snackbar
        } else {
            // No internet connection, you might want to notify the user.
            snackbar?.show() // بستن Snackbar
        }

        val progressDialog = ProgressDialog(this@PoolakeyActivity)
        progressDialog.setMessage("لطفا شکیبا باشید.")
        progressDialog.show()

        val localSecurityCheck = SecurityCheck.Enable(
            rsaPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCp3YRwWzg1xPuVxikivkDOJ+xU1QEWoOs1B30dEl36fCy+bvPZiNnfq6Ch8I74h7psd7ZJgYu8bJMB0Sblm7mCfKRb5h/a6cepZgDIRtL4w2nC2qMFL2zgwaL5p0cTfj2VkgqzYSrK+Ag10HsTzfqni/+fsPGC4XADtHJn+tn1B8zFlQoq0soN5n5drtDuE6eaVL9KKLfrgdbrFOKkty8oGDjaoD1J7SvjDwmc8Z8CAwEAAQ=="
        )

        val paymentConfiguration = PaymentConfiguration(
            localSecurityCheck = localSecurityCheck
        )

        val payment = Payment(context = this, config = paymentConfiguration)

        val paymentConnection = payment.connect {
            connectionSucceed {

                Log.d(TAG, "msg: connectionSucceed")
                progressDialog.cancel()
//                ...
            }
            connectionFailed { throwable ->
//                ...
                Log.d(TAG, "msg: connectionFailed")
                Toast.makeText(this@PoolakeyActivity, "ععع پولات کیک بود!!!", Toast.LENGTH_SHORT)
                    .show()
            }
            disconnected {
//                ...
                Log.d(TAG, "msg: disconnected")
                Toast.makeText(this@PoolakeyActivity, "ععع اینترنتت کیک شد!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnBuy.setOnClickListener {


            val purchaseRequest = PurchaseRequest(
                //productId = "trial_subscription",
                productId = "365days",
                payload = "PAYLOAD"
            )

            payment.purchaseProduct(
                registry = activityResultRegistry,
                request = purchaseRequest
            ) {
                purchaseFlowBegan {
//                    ...
                    Log.d(TAG, "msg: purchaseFlowBegan")

                }
                failedToBeginFlow { throwable ->
//                    ...
                    Log.d(TAG, "msg: failedToBeginFlow")
                }
                purchaseSucceed { purchaseEntity ->
//                    ...
                    Log.d(TAG, "msg: purchaseSucceed")

                    val sharedPreferences =
                        getSharedPreferences(SHAREDVEARATICK, Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean(CHEKBUY, true).apply()

                    Log.d(TAG, "msg: " + purchaseEntity)

                    payment.consumeProduct(purchaseEntity.purchaseToken) {
                        consumeSucceed {
//                    ...
                        }
                        consumeFailed { throwable ->
//                    ...
                        }
                    }

                    finish()

                }
                purchaseCanceled {
//                    ...
                    Log.d(TAG, "msg: purchaseCanceled")
                }
                purchaseFailed { throwable ->
//                    ...
                    Log.d(TAG, "msg: purchaseFailed")
                }
            }


        }


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /*

        override fun onDestroy() {
            paymentConnection.disconnect()
            super.onDestroy()
        }
    */

}