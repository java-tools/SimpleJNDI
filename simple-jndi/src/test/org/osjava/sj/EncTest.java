package org.osjava.sj;

import javax.naming.*;
import java.util.*;
import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class EncTest extends TestCase {

    private InitialContext initContext;

    public EncTest(String name) {
        super(name);
    }

    public void setUp() {
        System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory");
        System.setProperty("org.osjava.sj.root", "file://src/test/config/enc-test");
        System.setProperty("org.osjava.sj.delimiter", "/");
        System.setProperty("org.osjava.sj.space", "java:/comp/env");
        try {
            initContext = new InitialContext();
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void tearDown() {
        this.initContext = null;
    }

    public void testSystemPropertyContext() throws NamingException {
        String dsString = "bing::::foofoo::::Boo";
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/myoracle");
        assertEquals( dsString, ds.toString() );
    }
}