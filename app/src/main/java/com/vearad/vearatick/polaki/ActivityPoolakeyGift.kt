//package com.simurgh.spy.Activity
//
//import android.app.ProgressDialog
//import android.os.Bundle
//import android.util.Log
//import android.widget.RelativeLayout
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.vearad.vearatick.R
//import ir.cafebazaar.poolakey.Payment
//import ir.cafebazaar.poolakey.config.PaymentConfiguration
//import ir.cafebazaar.poolakey.config.SecurityCheck
//import ir.cafebazaar.poolakey.request.PurchaseRequest
//
//
//class ActivityPoolakeyGift : AppCompatActivity() {
//
//    private val TAG : String = "ActivityPoolakey_log"
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_poolakey_gift)
//
//        val progressDialog = ProgressDialog(this@ActivityPoolakeyGift)
//        progressDialog.setMessage("لطفا شکیبا باشید.")
//        progressDialog.show()
//
//        val btn_buy = findViewById(R.id.rl_purchase_kt_30) as? RelativeLayout
//        val btn_buy_60 = findViewById(R.id.rl_purchase_kt_60) as? RelativeLayout
//        val btn_buy_100 = findViewById(R.id.rl_purchase_kt_100) as? RelativeLayout
//
//        val localSecurityCheck = SecurityCheck.Enable(
//            rsaPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCp3YRwWzg1xPuVxikivkDOJ+xU1QEWoOs1B30dEl36fCy+bvPZiNnfq6Ch8I74h7psd7ZJgYu8bJMB0Sblm7mCfKRb5h/a6cepZgDIRtL4w2nC2qMFL2zgwaL5p0cTfj2VkgqzYSrK+Ag10HsTzfqni/+fsPGC4XADtHJn+tn1B8zFlQoq0soN5n5drtDuE6eaVL9KKLfrgdbrFOKkty8oGDjaoD1J7SvjDwmc8Z8CAwEAAQ=="
//        )
//
//        val paymentConfiguration = PaymentConfiguration(
//            localSecurityCheck = localSecurityCheck
//        )
//
//        val payment = Payment(context = this, config = paymentConfiguration)
//
//        val paymentConnection = payment.connect {
//            connectionSucceed {
//
//                Log.d(TAG, "msg: connectionSucceed")
//                progressDialog.cancel()
////                ...
//            }
//            connectionFailed { throwable ->
////                ...
//                Log.d(TAG, "msg: connectionFailed")
//                Toast.makeText(this@ActivityPoolakeyGift, "ععع پولات کیک بود!!!", Toast.LENGTH_SHORT).show()
//            }
//            disconnected {
////                ...
//                Log.d(TAG, "msg: disconnected")
//                Toast.makeText(this@ActivityPoolakeyGift, "ععع اینترنتت کیک شد!!!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//
//        btn_buy?.setOnClickListener {
//
//
//            val purchaseRequest = PurchaseRequest(
//                productId = "trial_subscription",
//                //productId = "365days",
//                payload = "PAYLOAD"
//            )
//
//
//            payment.purchaseProduct(
//                registry = activityResultRegistry,
//                request = purchaseRequest
//            ) {
//                purchaseFlowBegan {
//    //                    ...
//                    Log.d(TAG, "msg: purchaseFlowBegan")
//
//                }
//                failedToBeginFlow { throwable ->
//    //                    ...
//                    Log.d(TAG, "msg: failedToBeginFlow")
//                }
//                purchaseSucceed { purchaseEntity ->
//    //                    ...
//                    Log.d(TAG, "msg: purchaseSucceed")
//
////                    var real_p = SSSP.getInstance(this@ActivityPoolakeyGift).getInt(moneyKey, 2000)
////                    Log.d(TAG, "real_p 1 : " + real_p)
////
////                    SSSP.getInstance(this@ActivityPoolakeyGift).putInt(moneyKey, real_p + 35000)
//
//                    Log.d(TAG, "msg: "+ purchaseEntity)
//
//                    payment.consumeProduct( purchaseEntity.purchaseToken) {
//                        consumeSucceed {
//    //                    ...
//                        }
//                        consumeFailed { throwable ->
//    //                    ...
//                        }
//                    }
//
//                    finish()
//
//                }
//                purchaseCanceled {
//    //                    ...
//                    Log.d(TAG, "msg: purchaseCanceled")
//                }
//                purchaseFailed { throwable ->
//    //                    ...
//                    Log.d(TAG, "msg: purchaseFailed")
//                }
//            }
//
//
//        }
//
//        btn_buy_60?.setOnClickListener {
//
//
//            val purchaseRequest = PurchaseRequest(
//                productId = "spy60",
//                payload = "PAYLOAD"
//            )
//
//
//            payment.purchaseProduct(
//                registry = activityResultRegistry,
//                request = purchaseRequest
//            ) {
//                purchaseFlowBegan {
////                    ...
//                    Log.d(TAG, "msg: purchaseFlowBegan")
//
//                }
//                failedToBeginFlow { throwable ->
////                    ...
//                    Log.d(TAG, "msg: failedToBeginFlow")
//                }
//                purchaseSucceed { purchaseEntity ->
////                    ...
//                    Log.d(TAG, "msg: purchaseSucceed")
//
////                    var real_p = SSSP.getInstance(this@ActivityPoolakeyGift).getInt(moneyKey, 2000)
////                    Log.d(TAG, "real_p 1 : " + real_p)
////
////                    SSSP.getInstance(this@ActivityPoolakeyGift).putInt(moneyKey, real_p + 60000)
//
//                    Log.d(TAG, "msg: "+ purchaseEntity)
//
//                    payment.consumeProduct( purchaseEntity.purchaseToken) {
//                        consumeSucceed {
////                    ...
//                        }
//                        consumeFailed { throwable ->
////                    ...
//                        }
//                    }
//
//                    finish()
//
//                }
//                purchaseCanceled {
////                    ...
//                    Log.d(TAG, "msg: purchaseCanceled")
//                }
//                purchaseFailed { throwable ->
////                    ...
//                    Log.d(TAG, "msg: purchaseFailed")
//                }
//            }
//
//
//
//
//        }
//
//        btn_buy_100?.setOnClickListener {
//
//
//            val purchaseRequest = PurchaseRequest(
//                productId = "spy100",
//                payload = "PAYLOAD"
//            )
//
//
//            payment.purchaseProduct(
//                registry = activityResultRegistry,
//                request = purchaseRequest
//            ) {
//                purchaseFlowBegan {
////                    ...
//                    Log.d(TAG, "msg: purchaseFlowBegan")
//
//                }
//                failedToBeginFlow { throwable ->
////                    ...
//                    Log.d(TAG, "msg: failedToBeginFlow")
//                }
//                purchaseSucceed { purchaseEntity ->
////                    ...
//                    Log.d(TAG, "msg: purchaseSucceed")
//
////                    var real_p = SSSP.getInstance(this@ActivityPoolakeyGift).getInt(moneyKey, 2000)
////                    Log.d(TAG, "real_p 1 : " + real_p)
////
////                    SSSP.getInstance(this@ActivityPoolakeyGift).putInt(moneyKey, real_p + 100000)
//
//                    Log.d(TAG, "msg: "+ purchaseEntity)
//
//                    payment.consumeProduct( purchaseEntity.purchaseToken) {
//                        consumeSucceed {
////                    ...
//                        }
//                        consumeFailed { throwable ->
////                    ...
//                        }
//                    }
//                    finish()
//                }
//                purchaseCanceled {
////                    ...
//                    Log.d(TAG, "msg: purchaseCanceled")
//                }
//                purchaseFailed { throwable ->
////                    ...
//                    Log.d(TAG, "msg: purchaseFailed")
//                }
//           }
//        }
//    }
///*
//
//    override fun onDestroy() {
//        paymentConnection.disconnect()
//        super.onDestroy()
//    }
//*/
//
//}