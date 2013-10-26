package com.sigseg.ib.divden;

import com.ib.client.*;

import java.util.Locale;
import java.util.Map;

/**
 * Divden implements the divden approach
 */
public class Main {

	public static void main(String[] args) {
        Logger log = new Logger();
        Divden divden = DivdenFactory.make(args,System.getenv(),log);
        divden.start();
    }
}
