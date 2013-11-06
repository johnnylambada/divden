package com.sigseg.ib.divden;

import au.com.ds.ef.*;
import au.com.ds.ef.call.StateHandler;
import com.ib.client.*;

import java.util.concurrent.Executor;

/**
 * Divden implements the divden approach
 */
public class Divden extends StatefulContext implements EWrapper,Constants {

    public class Input {
        private final static double CASH_WIN = 10.00;
        private final static int NUM_SHARES = 1;
        private final static double RISK_CURRENCY = 10000.00;
        private final static String SYMBOL="T"; // AT&T
        private final static double TRANSACTION_COST = 1.50;
        private final static int TWS_PORT = 7496;

        /* Input parameters */
        public String account = null;
        public double cashWin = CASH_WIN;
        public int numShares = NUM_SHARES;
        public double riskCurrency = RISK_CURRENCY;
        public boolean isShort = false;
        public String symbol = SYMBOL;
        public double transactionCost = TRANSACTION_COST;
        public int twsPort = TWS_PORT;
    }
    public Input input = new Input();

    public class Market {
        Double bidPrice = null;
        Double askPrice = null;
        Double lastPrice = null;
        boolean isComplete(){
            return
                bidPrice!=null &&
                askPrice!=null &&
                lastPrice!=null;
        }
    }
    public Market market = new Market();

    private EClientSocket ibServer = new EClientSocket(this);
    private final Logger log;

    // defining states
    private final State<Divden> SHOWING_REPORT = FlowBuilder.state("SHOWING_REPORT");
    private final State<Divden> CONNECTING = FlowBuilder.state("CONNECTING");
    private final State<Divden> REQUESTING_MARKET_DATA = FlowBuilder.state("REQUESTING_MARKET_DATA");
    private final State<Divden> WAITING_FOR_MARKET_DATA = FlowBuilder.state("WAITING_FOR_MARKET_DATA");
    private final State<Divden> CALCULATING_POSITION = FlowBuilder.state("CALCULATING_POSITION");
    private final State<Divden> WAITING_FOR_POSITION_ENTRY = FlowBuilder.state("WAITING_FOR_POSITION_ENTRY");
    private final State<Divden> ENTERING_POSITION = FlowBuilder.state("ENTERING_POSITION");
    private final State<Divden> WAITING_FOR_POSITION_EXIT = FlowBuilder.state("WAITING_FOR_POSITION_EXIT");
    private final State<Divden> EXITING_POSITION = FlowBuilder.state("EXITING_POSITION");
    private final State<Divden> REPORTING_PROFITS = FlowBuilder.state("REPORTING_PROFITS");
    private final State<Divden> ERROR = FlowBuilder.state("ERROR");
    private final State<Divden> COMPLETE = FlowBuilder.state("COMPLETE");

    // defining events
    private final Event<Divden> onReportShown = FlowBuilder.event("onReportShown");
    private final Event<Divden> onConnected = FlowBuilder.event("onConnected");
    private final Event<Divden> onConnectFailed = FlowBuilder.event("onConnectFailed");
    private final Event<Divden> onMarketDataRequested = FlowBuilder.event("onMarketDataRequested");
    private final Event<Divden> onMarketData = FlowBuilder.event("onMarketData");
    private final Event<Divden> onPositionCalculated = FlowBuilder.event("onPositionCalculated");
    private final Event<Divden> onEntryFound = FlowBuilder.event("onEntryFound");
    private final Event<Divden> onPositionEntered = FlowBuilder.event("onPositionEntered");
    private final Event<Divden> onExitFound = FlowBuilder.event("onExitFound");
    private final Event<Divden> onPositionExited = FlowBuilder.event("onPositionExited");
    private final Event<Divden> onProfitsReported = FlowBuilder.event("onProfitsReported");
    private final Event<Divden> onErrorReported = FlowBuilder.event("onErrorReported");



    private EasyFlow<Divden> flow;

    private void initFlow() {
        if (flow != null) {
            return;
        }
        // build our FSM
        flow = FlowBuilder.from(SHOWING_REPORT).transit(
            onReportShown.to(CONNECTING)
        );
        FlowBuilder.from(CONNECTING).transit(
            onConnected.to(REQUESTING_MARKET_DATA),
            onConnectFailed.to(ERROR)
        );
        FlowBuilder.from(REQUESTING_MARKET_DATA).transit(
            onMarketDataRequested.to(WAITING_FOR_MARKET_DATA)
        );
        FlowBuilder.from(WAITING_FOR_MARKET_DATA).transit(
            onMarketData.to(CALCULATING_POSITION)
        );
        FlowBuilder.from(CALCULATING_POSITION).transit(
            onPositionCalculated.to(WAITING_FOR_POSITION_ENTRY)
        );
        FlowBuilder.from(WAITING_FOR_POSITION_ENTRY).transit(
            onEntryFound.to(ENTERING_POSITION)
        );
        FlowBuilder.from(ENTERING_POSITION).transit(
            onPositionEntered.to(WAITING_FOR_POSITION_EXIT)
        );
        FlowBuilder.from(WAITING_FOR_POSITION_EXIT).transit(
            onExitFound.to(EXITING_POSITION)
        );
        FlowBuilder.from(EXITING_POSITION).transit(
            onPositionExited.to(REPORTING_PROFITS)
        );
        FlowBuilder.from(ERROR).transit(
            onErrorReported.finish(COMPLETE)
        );
        FlowBuilder.from(REPORTING_PROFITS).transit(
            onProfitsReported.finish(COMPLETE)
        );

        flow.executor(new Executor() {
            @Override public void execute(Runnable runnable) { runnable.run(); }
        });
    }

    private void bindFlow() {
        SHOWING_REPORT.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
            log.out("Account:          %s", input.account);
            log.out("Direction:        %s", input.isShort ? "Short" : "Long");
            log.out("Symbol:           %s", input.symbol);
            log.out("Casgh Win:        %f", input.cashWin);
            log.out("Shares:           %d", input.numShares);
            log.out("Risk Currency:    %f", input.riskCurrency);
            log.out("Transaction Cost: %f", input.transactionCost);
            onReportShown.trigger(context);
            }
        });
        CONNECTING.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                ibServer.eConnect("localhost", input.twsPort, 0);
                if (ibServer.isConnected()){
                    ibServer.reqAccountSummary(1, "All", "BuyingPower");
                    onConnected.trigger(context);
                } else {
                    onConnectFailed.trigger(context);
                }
            }
        });
        REQUESTING_MARKET_DATA.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                Ticker ticker = new Ticker();
                Contract c = ticker.contract = new Contract();

                String[] symbolParts = input.symbol.split(":");
                c.m_symbol = symbolParts[0].toUpperCase();
                if (symbolParts.length>1)
                    c.m_expiry = symbolParts[1];

                try {
                    Symbol s = Symbol.valueOf(c.m_symbol);
                    c.m_secType = s.securityType.name();
                    c.m_exchange = s.exchange.name();
                    c.m_currency = s.currency.name();
                } catch (IllegalArgumentException e){
                    c.m_secType = SecurityType.STK.name();
                    c.m_exchange = Exchange.SMART.name();
                    c.m_currency = Currency.USD.name();
                }

                ibServer.reqMktData(ticker.id, ticker.contract, JavaClient.GENERIC_TICKS, false);
                onMarketDataRequested.trigger(context);
            }
        });
        CALCULATING_POSITION.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                log.out("CALCULATING_POSITION bid=%f last=%f ask=%f",
                    market.bidPrice,
                    market.askPrice,
                    market.lastPrice);

                onPositionCalculated.trigger(context);
            }
        });
        WAITING_FOR_POSITION_ENTRY.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                log.out("We're not that advanced yet");
                onEntryFound.trigger(context);
            }
        });
        ENTERING_POSITION.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                onPositionEntered.trigger(context);
            }
        });
        WAITING_FOR_POSITION_EXIT.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                onExitFound.trigger(context);
            }
        });
        EXITING_POSITION.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                onPositionExited.trigger(context);
            }
        });
        REPORTING_PROFITS.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                onProfitsReported.trigger(context);
            }
        });
        ERROR.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                log.err("ERROR EXIT");
            }
        });
        COMPLETE.whenEnter(new StateHandler<Divden>() {
            @Override
            public void call(State<Divden> state, Divden context) throws Exception {
                log.out("That's all folks!");
            }
        });
    }

    public class DivdenException extends Exception{public DivdenException(String m){super(m);}}

    public Divden(Logger logger){
        this.log = logger;
        initFlow();
        bindFlow();
    }

    public void validate() throws DivdenException {
        if (input.account==null){
            throw new DivdenException("Account cannot be null");
        }
    }

    public void start() {
        flow.start(this);
    }

    @Override public void accountDownloadEnd(String accountName) { }
    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        log.out("reqId=%d account=%s tag=%s value=%s currency=%s",
                reqId, account, tag, value, currency);
    }
    @Override public void accountSummaryEnd(int reqId) { }
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
//        log.out("tickerId=%d field=%s price=%f canAutoExecute=%b",
//                tickerId,TickType.getField(field),price,canAutoExecute);
        boolean newData = true;
        switch (field){
            case TickType.BID: market.bidPrice = price; break;
            case TickType.ASK: market.askPrice = price; break;
            case TickType.LAST: market.lastPrice = price; break;
            default: newData = false;
        }
        if (newData && market.isComplete()){
            onMarketData.trigger(this);
        }
    }
    @Override public void tickSize(int tickerId, int field, int size) { }
    @Override public void tickSnapshotEnd(int reqId) { }
    @Override public void tickString(int tickerId, int tickType, String value) { }
    @Override public void updateAccountTime(String timeStamp) { }
    @Override public void updateAccountValue(String key, String value, String currency, String accountName) { }
    @Override public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) { }
    @Override public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {}
    @Override public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) { }
    @Override public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) { }

}
