package com.sigseg.ib.divden;

import com.ib.client.Contract;

/**
 * Tickers track specific transactions within IB
 */
public class Ticker {
    /**
     * Each ticker has an ID that is used to tag results back to a specifc request
     */
    private static int nextId = 0;

    final int id;

    Contract contract;

    public Ticker(){
        this.id = ++nextId;
    }
}
