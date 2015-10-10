package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Payment;

interface PurchaseRequest {

    int getProductId();

    int getPrice();

    Payment getPayment();
}
