package com.sigseg.ib.divden;

import com.ib.client.*;

import java.util.Map;

/**
 * Divden implements the divden approach
 */
public class DivdenFactory {

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

    private final Logger log;

    public DivdenFactory(Logger logger){
        this.log = logger;
    }

    public void processArgs(String[] args) {
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-c".equals(arg)){
                try { cashWin = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-n".equals(arg)){
                try { numShares = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-r".equals(arg)){
                try { riskCurrency = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-s".equals(arg)){
                try { symbol = args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
            } else if ("-x".equals(arg)){
                try { transactionCost = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            }
        }
    }

    public void processEnv(Map<String,String> env) {
        if (env.containsKey(ENV_IB_ACCOUNT)){
            account = env.get(ENV_IB_ACCOUNT);
        } else {
            log.wtf(ENV_IB_ACCOUNT + " must be set");
        }
    }
}
