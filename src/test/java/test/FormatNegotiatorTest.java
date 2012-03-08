package test;

import org.rack4java.Rack;
import org.rack4java.middleware.format_negotiator.FormatNegotiator;


public class FormatNegotiatorTest extends MiddlewareTestCase {
	public void setUp() {
		super.setUp("format-negotiator");
		app = new FormatNegotiator(stub);
		assertEquals(0, stub.called);
	}

	public void testNeitherSideHasAPreference() throws Exception {
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/octet-stream", stub.request.get(Rack.HTTP_ACCEPT));
	}
}
