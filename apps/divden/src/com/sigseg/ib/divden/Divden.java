package com.sigseg.ib.divden;

import com.ib.client.*;

import java.util.Vector;

/**
 * Divden implements the divden approach
 */
public class Divden implements EWrapper,Constants {

    /* Constants */
    private final static double CASH_WIN = 10.00;
    private final static int NUM_SHARES = 1;
    private final static double RISK_CURRENCY = 10000.00;
    private final static String SYMBOL="T"; // AT&T
    private final static double TRANSACTION_COST = 1.50;
    private final static int TWS_PORT = 7496;

    /* Input parameters */
    private String account = null;
    private double cashWin = CASH_WIN;
    private int numShares = NUM_SHARES;
    private double riskCurrency = RISK_CURRENCY;
    private boolean isShort = false;
    private String symbol = SYMBOL;
    private double transactionCost = TRANSACTION_COST;
    private int twsPort = TWS_PORT;

    private EClientSocket ibServer = new EClientSocket(this);
    private final Logger log;

    static int tradeId = 0;
    private class Trade {
        int id;
        Contract contract;
        public Trade(){
            this.id = ++tradeId;
        }
    }

    public class DivdenException extends Exception{public DivdenException(String m){super(m);}}

    public Divden(Logger logger){
        this.log = logger;
    }

    public void validate() throws DivdenException {
        if (account==null){
            throw new DivdenException("Account cannot be null");
        }
    }

    public void start() {
        report();

        ibServer.eConnect("localhost", twsPort, 0);
        if (ibServer.isConnected()){
            countAndDisconnect(true);
            ibServer.reqAccountSummary(1, "All", "BuyingPower");

            Trade trade = new Trade();
            trade.contract = new Contract(
                0,                      // int p_conId
                symbol,                 // String p_symbol
                "STK",                  // String p_secType
                "",                     // String p_expiry
                0.0,                    // double p_strike
                "",                     // String p_right
                "",                     // String p_multiplier
                "SMART",                // String p_exchange
                "USD",                  // String p_currency
                "",                     // String p_localSymbol
                "",                     // String p_tradingClass
                new Vector<ComboLeg>(), // Vector<ComboLeg> p_comboLegs
                "",                     // String p_primaryExch
                false,                  // boolean p_includeExpired
                "",                     // String p_secIdType
                ""                      // String p_secId
            );

            countAndDisconnect(true);
            ibServer.reqMktData(trade.id, trade.contract, JavaClient.GENERIC_TICKS, false);
        }
    }

    int count=0;
    private void countAndDisconnect(boolean isIncrement){
        synchronized (this){
            if (isIncrement)
                count++ ;
            else {
                count--;
                if (count==0){
                    ibServer.eDisconnect();
                }
            }
        }
    }

    private void report(){
        log.out("Account:          %s", account);
        log.out("Direction:        %s", isShort?"Short":"Long");
        log.out("Symbol:           %s", symbol);
        log.out("Casgh Win:        %f", cashWin);
        log.out("Shares:           %d", numShares);
        log.out("Risk Currency:    %f", riskCurrency);
        log.out("Transaction Cost: %f", transactionCost);
    }

    @Override public void accountDownloadEnd(String accountName) { }
    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        log.out("reqId=%d account=%s tag=%s value=%s currency=%s",
                reqId, account, tag, value, currency);
    }
    @Override public void accountSummaryEnd(int reqId) {
        countAndDisconnect(false);
    }
    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails) { }
    @Override public void commissionReport(CommissionReport commissionReport) { }
    @Override public void connectionClosed() { }
    @Override public void contractDetails(int reqId, ContractDetails contractDetails) { }
    @Override public void contractDetailsEnd(int reqId) { }
    @Override public void currentTime(long time) { }
    @Override public void deltaNeutralValidation(int reqId, UnderComp underComp) { }
    @Override public void error(Exception e) {
        log.err("Exception: %s",e.getMessage());
    }
    @Override public void error(String str) {
        log.err(str);
    }
    @Override public void error(int id, int errorCode, String errorMsg) {
        log.err("id=%d code=%d msg=%s",id,errorCode,errorMsg);
    }
    @Override public void execDetails(int reqId, Contract contract, Execution execution) { }
    @Override public void execDetailsEnd(int reqId) { }
    @Override public void fundamentalData(int reqId, String data) { }
    @Override public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) { }
    @Override public void managedAccounts(String accountsList) { }
    @Override public void marketDataType(int reqId, int marketDataType) { }
    @Override public void nextValidId(int orderId) { }
    @Override public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) { }
    @Override public void openOrderEnd() { }
    @Override public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) { }
    @Override public void position(String account, Contract contract, int pos, double avgCost) { }
    @Override public void positionEnd() { }
    @Override public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) { }
    @Override public void receiveFA(int faDataType, String xml) { }
    @Override public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) { }
    @Override public void scannerDataEnd(int reqId) { }
    @Override public void scannerParameters(String xml) { }
    @Override public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) { }
    @Override public void tickGeneric(int tickerId, int tickType, double value) { }
    @Override public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) { }
    @Override public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        log.out("tickerId=%d field=%d price=%f canAutoExecute=%b",
                tickerId,field,price,canAutoExecute);
    }
    @Override public void tickSize(int tickerId, int field, int size) { }
    @Override public void tickSnapshotEnd(int reqId) {
        countAndDisconnect(false);
    }
    @Override public void tickString(int tickerId, int tickType, String value) { }
    @Override public void updateAccountTime(String timeStamp) { }
    @Override public void updateAccountValue(String key, String value, String currency, String accountName) { }
    @Override public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) { }
    @Override public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {}
    @Override public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) { }
    @Override public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) { }

    // Getters and Setters
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public double getCashWin() { return cashWin; }
    public void setCashWin(double cashWin) { this.cashWin = cashWin; }

    public int getNumShares() { return numShares; }
    public void setNumShares(int numShares) { this.numShares = numShares; }

    public double getRiskCurrency() { return riskCurrency; }
    public void setRiskCurrency(double riskCurrency) { this.riskCurrency = riskCurrency; }

    public boolean getIsShort() { return isShort; }
    public void setIsShort(boolean isShort) { this.isShort = isShort; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getTransactionCost() { return transactionCost; }
    public void setTransactionCost(double transactionCost) { this.transactionCost = transactionCost; }

    public int getTwsPort() { return twsPort; }
    public void setTwsPort(int twsPort) { this.twsPort = twsPort; }

}
