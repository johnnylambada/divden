package com.sigseg.ib.divden;

import com.ib.client.*;

import java.util.Locale;
import java.util.Map;

/**
 * Divden implements the divden approach
 */
public class Main {

	public static void main(String[] args) {
        Divden divden = new Divden();
        divden.processArgs(args);
        divden.processEnv(System.getenv());
        divden.start();
    }
}
