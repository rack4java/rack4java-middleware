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

	public void testClientHasOnlyOneOptionAppDoesntCare() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "application/json");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/json", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasSequenceAppDoesntCare() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "application/json,application/xml");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/json", stub.request.get(Rack.HTTP_ACCEPT));
	}
}
