package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {
    void  savePaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getpaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd);
    public void sendPaymentResult(PaymentInfo paymentInfo,String result);
    boolean checkPayment(PaymentInfo paymentInfo);
    void sendDelayPaymentResult(String outTradeNo,int delaySec ,int checkCount);

    void closePayment(String id);
}
