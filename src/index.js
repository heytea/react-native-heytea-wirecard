"use strict";
import { NativeModules } from "react-native";
const { WirecardPay } = NativeModules;

/** wirecard信用卡支付
 * param.env string  [TEST,SingaporeTEST,PROD,SingaporePROD]
 * param.merchantAccountId    string
 * param.requestId string
 * param.signature string
 * param.currency string
 * param.mantissa number
 * param.exponent number
 * param.tokenId string
 * param.maskedAccountNumber string
 * param.transactionType number
 * param.notificationURL string
 * param.needThreeD number
 * param.orderId number
 * param.orderDetail string
 */
 export const cardPay = param =>
  new Promise(resolve => {
    WirecardPay.cardPay(param).then(res => {
      resolve(res);
    });
  });
