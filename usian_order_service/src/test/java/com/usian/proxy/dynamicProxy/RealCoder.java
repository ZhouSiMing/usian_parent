package com.usian.proxy.dynamicProxy;

public class RealCoder implements Coder {
    @Override
    public void signContract() {
        System.out.println("RealCode.signContract");
    }

    @Override
    public void code() {
        System.out.println("大数据.coding");
    }

    @Override
    public void collectMoney() {
        System.out.println("RealCode.collectMoney");
    }
}
