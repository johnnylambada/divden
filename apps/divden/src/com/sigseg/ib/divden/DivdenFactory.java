package com.sigseg.ib.divden;

import com.ib.client.*;

import java.util.Map;

/**
 * DivdenFactory makes a Divden
 */
public class DivdenFactory {

    private static String USAGE =
        "-s symbol\n"+
        "-r riskCurrency\n"+
        "-n numShares\n"+
        "-c cashWin\n"+
        "-x transactionCost\n\n";

    private static String ENV_IB_ACCOUNT = "IB_ACCOUNT";

    private final Logger log;
    private final Divden divden;

    public static Divden make(String[] args, Map<String,String> env, Logger logger){
        DivdenFactory df = new DivdenFactory(args,env,logger);
        return df.divden;
    }

    private DivdenFactory(String[] args, Map<String,String> env, Logger logger){
        this.log = logger;
        divden = new Divden(logger);
        processArgs(args);
        processEnv(env);
    }

    public void processArgs(String[] args) {
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-c".equals(arg)){
                try { divden.cashWin = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-n".equals(arg)){
                try { divden.numShares = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-r".equals(arg)){
                try { divden.riskCurrency = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            } else if ("-s".equals(arg)){
                try { divden.symbol = args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
            } else if ("-x".equals(arg)){
                try { divden.transactionCost = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){log.wtf(arg + " requires a parameter");}
                catch (NumberFormatException e ){log.wtf("Invalid argument for " + arg);}
            }
        }
    }

    public void processEnv(Map<String,String> env) {
        if (env.containsKey(ENV_IB_ACCOUNT)){
            divden.account = env.get(ENV_IB_ACCOUNT);
        } else {
            log.wtf(ENV_IB_ACCOUNT + " must be set");
        }
    }
}
