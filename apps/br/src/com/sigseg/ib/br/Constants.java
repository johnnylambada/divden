package com.sigseg.ib.br;

public interface Constants {
    /**
     * These vaues are observed when running the TestJavaClient
     */
    public interface JavaClient {
        /**
         * This comes from OrderDlg
         */
        public final String GENERIC_TICKS = "100,101,104,105,106,107,165,221,225,233,236,258,293,294,295,318";
    }
    public enum SecurityType{
        STK,
        FUT,
        CASH,
        ;
    }
    public enum Exchange{
        SMART,
        ECBOT,
        IDEALPRO,
        GLOBEX
        ;
    }
    public enum Currency{
        USD,
        EUR,
        ;
    }
    public enum Symbol{
        YM(SecurityType.FUT,Exchange.ECBOT,Currency.USD),
        ES(SecurityType.FUT,Exchange.GLOBEX,Currency.USD),
        EUR(SecurityType.CASH,Exchange.IDEALPRO,Currency.USD),
        ;
        SecurityType securityType;
        Exchange exchange;
        Currency currency;
        Symbol(SecurityType securityType, Exchange exchange, Currency currency){
            this.securityType = securityType;
            this.exchange = exchange;
            this.currency = currency;
        }
    }
}
