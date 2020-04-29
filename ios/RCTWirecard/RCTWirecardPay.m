//
//  RCTWirecardPay.m
//  heyteago
//
//  Created by 肖怡宁 on 2019/7/1.
//  Copyright © 2019 marvin. All rights reserved.
//

#import "RCTWirecardPay.h"
#import <WDeCom/WDeCom.h>
#import <WDeComCard/WDeComCard.h>
#import <WDeComApplePay/WDeComApplePay.h>
#define PAY_FAILED (@"Pay Failed")
@implementation RCTWirecardPay
RCT_EXPORT_MODULE()
    
- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}
    
+ (BOOL)requiresMainQueueSetup {
    return YES;
}
    
// 信用卡支付
RCT_EXPORT_METHOD(cardPay:(NSDictionary *)payParam resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    
    NSDictionary *envDict = @{
                              @"TEST":@(WDECEnvironmentTEST),
                              @"SingaporeTEST":@(WDECEnvironmentSingaporeTEST),
                              @"PROD":@(WDECEnvironmentPROD),
                              @"SingaporePROD":@(WDECEnvironmentPROD)
                              };
    NSString *hostName = [payParam objectForKey:@"host"];
    NSString *pEnv = [payParam objectForKey:@"env"];
    NSString *pAccountID = [payParam objectForKey:@"merchantAccountId"];
    NSString *pRequestID = [payParam objectForKey:@"requestId"];
    NSString *pSignature = [payParam objectForKey:@"signature"];
    NSString *currency = [payParam objectForKey:@"currency"];
    NSNumber *mantissa = [payParam objectForKey:@"mantissa"];
    NSNumber *exponent = [payParam objectForKey:@"exponent"];
    NSString *tokenId = [payParam objectForKey:@"tokenId"];
    NSString *maskedAccountNumber = [payParam objectForKey:@"maskedAccountNumber"];
    NSNumber *transactionType = [payParam objectForKey:@"transactionType"];
    NSString *notificationURL = [payParam objectForKey:@"notificationURL"];
    NSNumber *needThreeD = [payParam objectForKey:@"needThreeD"];
    NSString *orderNumber = [payParam objectForKey:@"orderId"];
    NSString *orderDetail = [payParam objectForKey:@"orderDetail"];
    if (pEnv == nil) {
        pEnv = @"PROD";
    }
    
    WDECClient  *client  = nil;
    if (hostName) {
        NSError *err = nil;
        client = [[WDECClient alloc] initWithHostname:hostName error:&err];
    } else {
        WDECEnvironment env = [envDict[pEnv] unsignedIntegerValue];
        client = [[WDECClient alloc] initWithEnvironment:env];
    }
    WDECCardPayment *payment = [[WDECCardPayment alloc] initWithMerchantAccountID:pAccountID requestID:pRequestID signature:pSignature];
    payment.currency = currency;
    payment.locale = WDECLocale_en;
    if (needThreeD && [needThreeD integerValue] == 1) {
           payment.attemptThreeD = WDECBoolYes;
    } else {
           payment.attemptThreeD = WDECBoolNo;
    }
    
    WDECOrder *order = [[WDECOrder alloc] init];
    
    if (orderNumber) {
        order.number = orderNumber;
    }
    
    if (orderDetail) {
        order.detail = orderDetail;
    }
    
    if (tokenId && maskedAccountNumber && tokenId.length > 0 && maskedAccountNumber.length > 0) {
        WDECCardToken *cardToken =  [[WDECCardToken alloc] init];
        cardToken.tokenID = tokenId;
        cardToken.maskedAccountNumber = maskedAccountNumber;
        payment.token = cardToken;
    }
    
    payment.order = order;
    payment.transactionType = (WDECTransactionType)[transactionType unsignedIntegerValue];
    payment.amount = [NSDecimalNumber decimalNumberWithMantissa:[mantissa unsignedLongLongValue] exponent:[exponent shortValue] isNegative:NO];
    
    //成功通知
    WDECNotification *successNotification = [[WDECNotification alloc] init];
    successNotification.transactionState =  WDECTransactionStateSuccess;
    successNotification.URL = [NSURL URLWithString:notificationURL];
    
    //失败通知
    WDECNotification *failedNotification = [[WDECNotification alloc] init];
    failedNotification.transactionState =  WDECTransactionStateFailed;
    failedNotification.URL = [NSURL URLWithString:notificationURL];
    
    payment.notifications = @[successNotification,failedNotification];
    [client makePayment:payment withCompletion:^(WDECPaymentResponse * _Nullable response, NSError * _Nullable error) {
        
        if (error) {
            resolve(@{
                      @"transactionState":@(WDECTransactionStateFailed),
                      @"transactionIdentifier":response.transactionIdentifier?response.transactionIdentifier:PAY_FAILED,
                      @"statusMessage":error.localizedDescription? error.localizedDescription:PAY_FAILED ,
                      });
        }else {
            resolve(@{
                      @"transactionState":@(response.transactionState),
                      @"transactionIdentifier":response.transactionIdentifier,
                      @"statusMessage":response.statusMessage,
                      });
        }
      
    }];
    
}
    
   
// ApplePay
RCT_EXPORT_METHOD(applePay:(NSDictionary *)payParam resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    
    NSDictionary *envDict = @{
                              @"TEST":@(WDECEnvironmentTEST),
                              @"SingaporeTEST":@(WDECEnvironmentSingaporeTEST),
                              @"PROD":@(WDECEnvironmentPROD),
                              @"SingaporePROD":@(WDECEnvironmentPROD)
                              };
    
    NSDictionary *regionDict = @{
                                 @"hk":@(WDECCountryHK), //香港
                                 @"sg":@(WDECCountrySG), //新加坡
                                 @"th":@(WDECCountryTH),  //泰国
                                 @"tw":@(WDECCountryTW), //台湾
                                 @"mo":@(WDECCountryMO), //澳门
                                 @"my":@(WDECCountryMY), //马来西亚
                                 @"jp":@(WDECCountryJP) //日本
                                 };
    
  
    NSString *pEnv = [payParam objectForKey:@"env"];
    NSString *pAccountID = [payParam objectForKey:@"merchantAccountId"];
    NSString *pRequestID = [payParam objectForKey:@"requestId"];
    NSString *pSignature = [payParam objectForKey:@"signature"];
    NSString *pAppleMerchantID = [payParam objectForKey:@"appleMerchantId"];
    id region = [payParam objectForKey:@"region"];
    NSString *currency = [payParam objectForKey:@"currency"];
    NSNumber *mantissa = [payParam objectForKey:@"mantissa"];
    NSNumber *exponent = [payParam objectForKey:@"exponent"];
    NSUInteger merchantCountryEnum = WDECCountryHK;
    
    if (pEnv == nil) {
        pEnv = @"PROD";
    }
    if (region == nil) {
        merchantCountryEnum = WDECCountryHK;
    }else if([region isKindOfClass:[NSString class]]){
        merchantCountryEnum = [regionDict[region] unsignedIntegerValue];
    }else if ([region isKindOfClass:[NSNumber class]]) {
        merchantCountryEnum = (WDECCountry)[region unsignedIntegerValue];
    }
    

    WDECEnvironment env = [envDict[pEnv] unsignedIntegerValue];
    WDECClient  *client = [[WDECClient alloc] initWithEnvironment:env];
    WDECApplePayManagedPayment *applePayment = [[WDECApplePayManagedPayment alloc] initWithMerchantAccountID:pAccountID requestID:pRequestID signature:pSignature];
    applePayment.appleMerchantID = pAppleMerchantID;
    applePayment.appleMerchantCountry = [regionDict[region] unsignedIntegerValue];
    applePayment.currency = currency;
    applePayment.transactionType = WDECTransactionTypePurchase;
    applePayment.amount = [NSDecimalNumber decimalNumberWithMantissa:[mantissa unsignedLongLongValue] exponent:[exponent shortValue] isNegative:NO];
    
    [client makePayment:applePayment withCompletion:^(WDECPaymentResponse * _Nullable response, NSError * _Nullable error) {
        
        if (error) {
            resolve(@{
                      @"transactionState":@(WDECTransactionStateFailed),
                      @"transactionIdentifier":PAY_FAILED,
                      @"statusMessage":PAY_FAILED,
                      });
        }else {
            resolve(@{
                      @"transactionState":@(response.transactionState),
                      @"transactionIdentifier":response.transactionIdentifier,
                      @"statusMessage":response.statusMessage,
                      });
        }
        
    }];
}
@end
