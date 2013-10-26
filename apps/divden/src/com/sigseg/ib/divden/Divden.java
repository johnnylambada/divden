package com.sigseg.ib.divden;

import com.ib.client.*;

/**
 * Divden implements the divden approach
 */
public class Divden implements EWrapper {

    private final static double CASH_WIN = 10.00;
    private final static int NUM_SHARES = 1;
    private final static double RISK_CURRENCY = 10000.00;
    private final static String SYMBOL="T"; // AT&T
    private final static double TRANSACTION_COST = 1.50;
    private final static int TWS_PORT = 7496;

    public class DivdenException extends Exception{public DivdenException(String m){super(m);}}

    private String account = null;
    private double cashWin = CASH_WIN;
    private int numShares = NUM_SHARES;
    private double riskCurrency = RISK_CURRENCY;
    private boolean isShort = false;
    private String symbol = SYMBOL;
    private double transactionCost = TRANSACTION_COST;
    private int twsPort = TWS_PORT;

    private EClientSocket ib = new EClientSocket(this);

    private final Logger log;

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

        ib.eConnect("localhost", twsPort, 0);
        if (ib.isConnected()){
            ib.reqAccountSummary(1, "All", "BuyingPower");
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
        ib.eDisconnect();
    }
    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails) { }
    @Override public void commissionReport(CommissionReport commissionReport) { }
    @Override public void connectionClosed() { }
    @Override public void contractDetails(int reqId, ContractDetails contractDetails) { }
    @Override public void contractDetailsEnd(int reqId) { }
    @Override public void currentTime(long time) { }
    @Override public void deltaNeutralValidation(int reqId, UnderComp underComp) { }
    @Override public void error(Exception e) { }
    @Override public void error(String str) { }
    @Override public void error(int id, int errorCode, String errorMsg) { }
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
    @Override public void tickPrice(int tickerId, int field, double price, int canAutoExecute) { }
    @Override public void tickSize(int tickerId, int field, int size) { }
    @Override public void tickSnapshotEnd(int reqId) { }
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
