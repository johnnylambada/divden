package com.sigseg.ib.divden;

import au.com.ds.ef.EasyFlow;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * DivdenFactory makes a Divden
 */
public class DivdenFactory {
    static org.slf4j.Logger log = LoggerFactory.getLogger(DivdenFactory.class);

    private static String USAGE =
        "-a account\n"+
        "-p twsPort\n"+
        "--short\n"+
        "--long (default)\n"+
        "-s symbol\n"+
        "-r riskCurrency\n"+
        "-m (create a market order)\n"+
        "-n numShares\n"+
        "-c cashWin\n"+
        "-v (verbose)\n"+
        "-x transactionCost\n\n";

    private static String ENV_IB_ACCOUNT = "IB_ACCOUNT";

    public class DivdenFactoryException extends Exception{public DivdenFactoryException(String m){super(m);}}

    private final Divden divden;

    public static Divden make(String[] args, Map<String,String> env) throws Exception {
        DivdenFactory df = new DivdenFactory(args,env);
        return df.divden;
    }

    private DivdenFactory(String[] args, Map<String,String> env) throws Exception {
        try{
            divden = new Divden();
            processEnv(env);
            processArgs(args);
            divden.validate();
        } catch (Exception e){
            StringBuilder sb = new StringBuilder();
            sb.append(USAGE).append(e.getMessage());
            throw new DivdenFactoryException(sb.toString());
        }
    }

    public void processArgs(String[] args) throws DivdenFactoryException {
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-a".equals(arg)){
                try { divden.input.account=args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
            } else if ("-h".equals(arg) || "--help".equals(arg)){
                throw new DivdenFactoryException("");
            } else if ("-c".equals(arg)){
                try { divden.input.cashWin = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-m".equals(arg)){
                divden.input.isMarket = true;
            } else if ("-n".equals(arg)){
                try { divden.input.numShares = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-p".equals(arg)){
                try { divden.input.twsPort = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-r".equals(arg)){
                try { divden.input.riskCurrency = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-s".equals(arg)){
                try { divden.input.symbol = args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
            } else if ("--short".equals(arg)){
                divden.input.isShort = true;
            } else if ("--long".equals(arg)){
                divden.input.isShort = false;
            } else if ("-x".equals(arg)){
                try { divden.input.transactionCost = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-v".equals(arg)){
                // This is taken care of in Main
            } else {
                throw new DivdenFactoryException("Invalid parameter " + arg);
            }
        }
    }

    private void noParameter(String arg) throws DivdenFactoryException {
        throw new DivdenFactoryException(arg+" requires a parameter");
    }

    private void badParameter(String arg) throws DivdenFactoryException {
        throw new DivdenFactoryException("Invalid argument for " + arg);
    }

    public void processEnv(Map<String,String> env) {
        if (env.containsKey(ENV_IB_ACCOUNT)){
            divden.input.account = env.get(ENV_IB_ACCOUNT);
        }
    }
}
