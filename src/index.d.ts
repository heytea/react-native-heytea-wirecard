declare module "@heytea/react-native-wirecard" {
    export interface ICardPayParam {
        env: "TEST" | "SingaporeTEST" | "PROD" | "SingaporePROD";
        merchantAccountId: string;
        requestId: string;
        signature: string;
        currency: string;
        mantissa: number;
        exponent: number;
        transactionType: number;
        notificationURL: string;
        orderDetail: string;
        tokenId?: string;
        maskedAccountNumber?: string;
        needThreeD?: number;
        orderId?: number;
        requestedAmount: string;
    }

    export interface ICardPayResponse {
        transactionState: number;
        transactionIdentifier: string;
        statusMessage: string;
    }

    export function cardPay(param: ICardPayParam): Promise<ICardPayResponse>
}
