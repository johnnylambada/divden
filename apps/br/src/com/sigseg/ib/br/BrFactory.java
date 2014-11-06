package com.sigseg.ib.br;

import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Br factor makes a br
 */
public class BrFactory {
    static org.slf4j.Logger log = LoggerFactory.getLogger(BrFactory.class);

    private static String USAGE =
        "-a account\n"+
        "-p twsPort\n"+
        "--short\n"+
        "--long (default)\n"+
        "-s symbol\n"+
        "-d distance (default of 0.25)\n"+
		"-i inPrice (default to lastPrice)"+
        "-m (create a market order)\n"+
        "-n numShares\n"+
        "-v (verbose - more -v's for more verbosity)\n"+
        "-t (transmit)\n"+
        "-w (whatif? -- needs in price)\n"+
        "";

    private static String ENV_IB_ACCOUNT = "IB_ACCOUNT";

    public class BrFactoryException extends Exception{public BrFactoryException(String m){super(m);}}

    private final Br br;

    public static Br make(String[] args, Map<String,String> env) throws Exception {
        BrFactory df = new BrFactory(args,env);
        return df.br;
    }

    private BrFactory(String[] args, Map<String,String> env) throws Exception {
        try{
            br = new Br();
            processEnv(env);
            processArgs(args);
            br.validate();
        } catch (Exception e){
            StringBuilder sb = new StringBuilder();
            sb.append(USAGE).append(e.getMessage());
            throw new BrFactoryException(sb.toString());
        }
    }

    public void processArgs(String[] args) throws BrFactoryException {
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-a".equals(arg)){
                try { br.input.account=args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
            } else if ("-h".equals(arg) || "--help".equals(arg)){
                throw new BrFactoryException("");
			} else if ("-i".equals(arg)){
				try { br.input.inPrice = Double.parseDouble(args[++i]);}
				catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
				catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-d".equals(arg)){
                try { br.input.distance = Double.parseDouble(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-m".equals(arg)){
                br.input.isMarket = true;
            } else if ("-n".equals(arg)){
                try { br.input.numShares = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-p".equals(arg)){
                try { br.input.twsPort = Integer.parseInt(args[++i]);}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
                catch (NumberFormatException e ){badParameter(arg);}
            } else if ("-s".equals(arg)){
                try { br.input.symbol = args[++i];}
                catch (ArrayIndexOutOfBoundsException e ){noParameter(arg);}
            } else if ("--short".equals(arg)){
                br.input.isShort = true;
            } else if ("--long".equals(arg)){
                br.input.isShort = false;
            } else if ("-t".equals(arg)){
                br.input.doTransmit = true;
            } else if ("-w".equals(arg)){
                br.input.isWhatIf = true;
            } else if ("-v".equals(arg)){
				br.input.verbose++;
            } else {
                throw new BrFactoryException("Invalid parameter " + arg);
            }
        }
    }

    private void noParameter(String arg) throws BrFactoryException {
        throw new BrFactoryException(arg+" requires a parameter");
    }

    private void badParameter(String arg) throws BrFactoryException {
        throw new BrFactoryException("Invalid argument for " + arg);
    }

    public void processEnv(Map<String,String> env) {
        if (env.containsKey(ENV_IB_ACCOUNT)){
            br.input.account = env.get(ENV_IB_ACCOUNT);
        }
    }
}
