package com.heytea.wirecard

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.*
import com.wirecard.ecom.Client
import com.wirecard.ecom.card.model.CardPayment
import com.wirecard.ecom.model.*
import com.wirecard.ecom.model.out.PaymentResponse
import com.wirecard.ecom.util.Observer
import java.math.BigDecimal

/**
 * Package     ：com.heytea.wirecard
 * Description ：
 * Company     ：Heytea
 * Author      ：Created by ChengGuang
 * CreateTime  ：2020/5/19.
 */
class RNWirecardModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), Observer<PaymentResponse> {

    private var promise: Promise? = null;
    private var reactContext: ReactApplicationContext


    companion object {
        private val ModuleName = "WirecardPay"
    }

    init {
        this.reactContext = reactContext
        var receiver = PaymentBroadCastReceiver()
        var filter = IntentFilter();
        filter.addAction(ConstantValues.ACTION_PAYMENT_RESPONSE)
        this.reactContext.registerReceiver(receiver, filter)
    }


    override fun getName(): String {
        return ModuleName
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @ReactMethod
    fun cardPay(payParam: ReadableMap, promise: Promise) {
        this.promise = promise;
        val paymentBuilder: CardPayment.Builder = CardPayment.Builder();
        val cardToken = CardToken()
        var transactionType: Int = 0
        var currency: String = ""
        var signature: String = ""
        var host: String = ""
        var needThreeD: Int = 0
        var notificationURL: String = ""
        var orderNumber: String = ""
        var orderDetail: String = ""

        // 免密支付需要
        if (payParam.hasKey("maskedAccountNumber") && payParam.hasKey("tokenId")) {
            cardToken.tokenId = payParam.getString("tokenId").toString()
            cardToken.maskedAccountNumber = payParam.getString("maskedAccountNumber").toString()
        }

        //调起支付服务的url
        if (payParam.hasKey("host")) {
            host = payParam.getString("host").toString()
        }

        // 商户id
        if (payParam.hasKey("merchantAccountId")) {
            paymentBuilder.setMerchantAccountId(payParam.getString("merchantAccountId").toString())
        }

        // 请求id
        if (payParam.hasKey("requestId")) {
//            paymentBuilder.setRequestId(UUID.randomUUID().toString())
            paymentBuilder.setRequestId(payParam.getString("requestId").toString())
        }


        // 交易类型
        if (payParam.hasKey("transactionType")) {
            transactionType = payParam.getInt("transactionType")
            // 对齐ios transactionType === 1 >>> AUTHORIZATION
            // 对齐ios transactionType === 6 >>> PURCHASE
            if (transactionType == 1) {
                paymentBuilder.setTransactionType(TransactionType.AUTHORIZATION)
            } else if (transactionType == 6) {
                paymentBuilder.setTransactionType(TransactionType.PURCHASE)
            }
        }

        // 签名
        if (payParam.hasKey("signature")) {
            signature = payParam.getString("signature").toString()
            paymentBuilder.setSignature(signature)
        }

        // 货币
        if (payParam.hasKey("currency")) {
            currency = payParam.getString("currency").toString()
            paymentBuilder.setCurrency(currency)
        }

        // 支付金额
        if (payParam.hasKey("mantissa") && payParam.hasKey("exponent")) {
            var scale: Double = Math.pow(10.0, payParam.getDouble("exponent"))
            val amount: BigDecimal = BigDecimal(payParam.getInt("mantissa")).multiply(BigDecimal(scale))
            val result = amount.setScale(2, BigDecimal.ROUND_DOWN)
            // TODO
            paymentBuilder.setAmount(result)
        }

        var payment: CardPayment = paymentBuilder.build()

        if (payParam.hasKey("needThreeD")) {
            needThreeD = payParam.getInt("needThreeD")
            payment.attempt3d = needThreeD != 0
        }

        // 设置通知url 提供给后端用
        if (payParam.hasKey("notificationURL")) {
            notificationURL = payParam.getString("notificationURL").toString()
            var notification = Notification()
            notification.url = notificationURL
            payment.notifications = Notifications(listOf(notification))
        }


        // 订单号
        if (payParam.hasKey("orderNumber")) {
            orderNumber = payParam.getString("orderNumber").toString()
            payment.orderNumber = orderNumber
        }

        // 订单详情
        if (payParam.hasKey("orderDetail")) {
            orderDetail = payParam.getString("orderDetail").toString()
            payment.orderDetail = orderDetail
        }

        payment.requireManualCardBrandSelection = true
        payment.animatedCardPayment = false
        payment.cardToken = cardToken
        payment.isoTransactionType = IsoTransactionType.QUASI_CASH_TRANSACTION  // iso 交易类型


        if (currentActivity is Activity) {
            Client(currentActivity!!, "https://" + host, 1000).startPayment(payment)
//            Client(currentActivity!!, host, 1000).startPayment(payment)

        }
    }


    inner class PaymentBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var bundle = intent.extras
            var response = bundle.get("response") as PaymentResponse
            var payment = response.payment;
            var transactionState: String? = payment?.transactionState
            var code:Int = response.responseCode;
            var map = Arguments.createMap()
            if(transactionState.equals("success")) {
                map.putInt("transactionState",1)
            }else {
                map.putInt("transactionState",0)
            }
            map.putInt("code",code)
            var rnWirecardModule = this@RNWirecardModule //获取外部类的成员变量
            rnWirecardModule.promise?.resolve(map)
            println("~~~~~~~~~广播通知回调~~~~~~~~~~~`")
            println(response)
            println("~~~~~~~~~广播通知回调~~~~~~~~~~~`")

        }
    }


    override fun onObserve(eventMessage: PaymentResponse) {
        promise?.resolve(eventMessage)
    }


}