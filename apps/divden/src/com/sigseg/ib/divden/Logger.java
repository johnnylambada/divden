package com.sigseg.ib.divden;

import java.util.Locale;

public class Logger {
    public void out(String format, Object... objects){
        System.out.printf(Locale.US,format,objects);
    }
    public void out(String out){out(out,null);}

    public void err(String format, Object... objects){
        System.err.printf(Locale.US,format,objects);
    }
    public void err(String out){err(out,null);}

    public void wtf(String format, Object... objects){
        System.err.printf(Locale.US,format,objects);
        System.exit(1);
    }
    public void wtf(String out){wtf(out,null);}

}
