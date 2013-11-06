package com.sigseg.ib.divden;

import au.com.ds.ef.EasyFlow;
import com.ib.client.*;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * Divden implements the divden approach
 */
public class Main {
    public static void main(String[] args) {
        // Prescreen args for -v
        for (int i=0; i<args.length; i++){
            String arg = args[i];
            if ("-v".equals(arg)){
                System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "trace");
            }
        }
        Divden divden;
        try {
            divden = DivdenFactory.make(args,System.getenv());
            divden.start();
        } catch (Exception e){
            LoggerFactory.getLogger(Main.class).error(e.getMessage());
        }
    }
}
