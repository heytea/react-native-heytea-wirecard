package com.heytea.wirecard

/**
 * Package     ：com.heytea.wirecard
 * Description ：
 * Company     ：Heytea
 * Author      ：Created by ChengGuang
 * CreateTime  ：2020/5/19.
 */
data class PayParam(
        var host: String,
        var env: String,
        var merchantAccountId: String,
        var requestId: String,
        var signature: String,
        var currency: String,
        var mantissa: Int,
        var exponent: Int,
        var tokenId: String,
        var maskedAccountNumber: String,
        var transactionType: Int,
        var notificationURL: String,
        var needThreeD: Int,
        var orderId: String,
        var orderDetail: String
)

