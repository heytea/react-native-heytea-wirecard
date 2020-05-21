package com.heytea.wirecard.providers;

import com.wirecard.ecom.card.model.CardBundle;
import com.wirecard.ecom.card.model.CardFieldPayment;
import com.wirecard.ecom.card.model.CardPayment;
import com.wirecard.ecom.model.AccountHolder;
import com.wirecard.ecom.model.CardToken;
import com.wirecard.ecom.model.Notification;
import com.wirecard.ecom.model.Notifications;
import com.wirecard.ecom.model.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class PaymentObjectProvider {
    OptionalFieldsProvider optionalFieldsProvider;

    public PaymentObjectProvider(OptionalFieldsProvider optionalFieldsProvider){
        this.optionalFieldsProvider = optionalFieldsProvider;
    }



    public CardPayment getCardPaymentWithOptionalData(){
        return optionalFieldsProvider.appendCardOptionalData(getCardPayment(false, false));
    }

    public CardPayment getCardPayment(boolean isAnimated){
        return this.getCardPayment(isAnimated, true);
    }

    public CardPayment getCardPayment(boolean isAnimated, boolean append3dsV2Fields) {
        String merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1";
        String secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(0.01);
        String currency = "EUR";


        CardPayment cardPayment = new CardPayment.Builder()
                .setSignature("")
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .build();
        cardPayment.setRequireManualCardBrandSelection(true);
        cardPayment.setAnimatedCardPayment(isAnimated);
        if(append3dsV2Fields)
            return (CardPayment) optionalFieldsProvider.appendThreeDSV2Fields(cardPayment);
        else return cardPayment;
    }

    public CardPayment getCardTokenPayment(){
        String merchantID = "9105bb4f-ae68-4768-9c3b-3eda968f57ea";
        String secretKey = "d1efed51-4cb9-46a5-ba7b-0fdc87a66544";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(0.01);
        String currency = "EUR";

        CardPayment cardPayment = new CardPayment.Builder()
                .setSignature("")
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .build();
        cardPayment.setRequireManualCardBrandSelection(true);

        CardToken cardToken = new CardToken();
        cardToken.setTokenId("4304509873471003");
        cardToken.setMaskedAccountNumber("401200******1003");

        cardPayment.setCardToken(cardToken);

        return cardPayment;
    }

    public CardFieldPayment getCardFormPayment(CardBundle cardBundle) {
        String merchantID = "cad16b4a-abf2-450d-bcb8-1725a4cef443";
        String secretKey = "b3b131ad-ea7e-48bc-9e71-78d0c6ea579d";
        String requestID = UUID.randomUUID().toString();
        TransactionType transactionType = TransactionType.PURCHASE;
        BigDecimal amount = new BigDecimal(0.01);
        String currency = "EUR";

        CardFieldPayment cardFieldPayment = new CardFieldPayment.Builder()
                .setSignature("")
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .setCardBundle(cardBundle)
                .build();

        cardFieldPayment.setAttempt3d(true);

        AccountHolder accountHolder = new AccountHolder("John", "Doe");
        cardFieldPayment.setAccountHolder(accountHolder);

        return (CardFieldPayment) optionalFieldsProvider.appendThreeDSV2Fields(cardFieldPayment);
    }

}
