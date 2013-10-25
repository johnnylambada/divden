package com.sigseg.ib.divden;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;

import java.util.Locale;
import java.util.Map;

/**
 * Divden implements the divden approach
 */
public class Divden implements EWrapper {

    private static String USAGE =
        "-s symbol\n"+
        "-r riskCurrency\n"+
        "-n numShares\n"+
        "-c cashWin\n"+
        "-x transactionCost\n\n";

    private static double CASH_WIN = 10.00;
    private static int NUM_SHARES = 1;
    private static double RISK_CURRENCY = 10000.00;
    private static String SYMBOL="T"; // AT&T
    private static double TRANSACTION_COST = 1.50;

    private static String ENV_IB_ACCOUNT = "IB_ACCOUNT";

    private String account = null;
    private double cashWin = CASH_WIN;
    private int numShares = NUM_SHARES;
    private double riskCurrency = RISK_CURRENCY;
    private String symbol = SYMBOL;
    private double transactionCost = TRANSACTION_COST;

    private EClientSocket ib = new EClientSocket(this);

	public static void main(String[] args) {
        Divden divden = new Divden();
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-c".equals(arg)){
                try { divden.cashWin = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){err(arg+" requires a parameter");}
                catch (NumberFormatException e ){err("Invalid argument for "+arg);}
            } else if ("-n".equals(arg)){
                try { divden.numShares = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){err(arg+" requires a parameter");}
                catch (NumberFormatException e ){err("Invalid argument for "+arg);}
            } else if ("-r".equals(arg)){
                try { divden.riskCurrency = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){err(arg+" requires a parameter");}
                catch (NumberFormatException e ){err("Invalid argument for "+arg);}
            } else if ("-s".equals(arg)){
                try { divden.symbol = args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){err(arg+" requires a parameter");}
            } else if ("-x".equals(arg)){
                try { divden.transactionCost = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){err(arg+" requires a parameter");}
                catch (NumberFormatException e ){err("Invalid argument for "+arg);}
            }
        }

        Map<String, String> env = System.getenv();
        if (env.containsKey(ENV_IB_ACCOUNT)){
            divden.account = env.get(ENV_IB_ACCOUNT);
        } else {
            err(ENV_IB_ACCOUNT + " must be set");
        }
        divden.report();
		divden.run();
	}

    private static void err(String err){
        System.err.print(USAGE+err + "\n");
        System.exit(1);
    }

    private void report(){
        System.out.printf(Locale.US,"Account:          %s\n",account);
        System.out.printf(Locale.US,"Symbol:           %s\n",symbol);
        System.out.printf(Locale.US,"Casgh Win:        %f\n",cashWin);
        System.out.printf(Locale.US,"Shares:           %d\n",numShares);
        System.out.printf(Locale.US,"Risk Currency:    %f\n",riskCurrency);
        System.out.printf(Locale.US,"Transaction Cost: %f\n",transactionCost);
    }
	private void run() {
		ib.eConnect("localhost", 7496, 0);

        ib.reqAccountSummary(1, "All", "BuyingPower");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }

    @Override public void accountDownloadEnd(String accountName) { }
    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        System.out.printf(Locale.US,"reqId=%d account=%s tag=%s value=%s currency=%s\n",
                reqId,account,tag,value,currency);
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
}
