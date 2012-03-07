package test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    
    public static TestSuite suite() {
        TestSuite ret = new TestSuite();

        ret.addTestSuite(FunkyCacheTest.class);
        ret.addTestSuite(NaughtyStepTest.class);
        ret.addTestSuite(AbstractFormatTest.class);

        return ret;
    }

}
