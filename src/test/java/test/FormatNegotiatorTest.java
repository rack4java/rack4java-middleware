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

	public void testClientHasSequenceWithPrioritiesAppDoesntCare() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "application/json;q=0.6,application/xml;q=0.9");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasSequenceWithPartialPrioritiesAppDoesntCare() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "application/json;q=0.6,text/html,application/xml;q=0.9");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("text/html", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasSequenceAppIsPicky() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml");
		request.with(Rack.HTTP_ACCEPT, "application/json,application/xml");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasSequenceAppHasSequence() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("text/plain").withSupportedFormat("application/xml");
		request.with(Rack.HTTP_ACCEPT, "application/json,application/xml,text/html");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasSequenceAppHasPriorities() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml", 0.6).withSupportedFormat("text/html");
		request.with(Rack.HTTP_ACCEPT, "application/json,application/xml,text/html");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasWildcardAppHasSequence() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml").withSupportedFormat("text/html").withSupportedFormat("text/plain");
		request.with(Rack.HTTP_ACCEPT, "application/json,text/*");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("text/html", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testClientHasWildcardAppHasPriorities() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml").withSupportedFormat("text/html", 0.8).withSupportedFormat("text/plain");
		request.with(Rack.HTTP_ACCEPT, "application/json,text/*");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("text/plain", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testLeadingWildcard() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml").withSupportedFormat("text/html").withSupportedFormat("text/plain");
		request.with(Rack.HTTP_ACCEPT, "application/json,*/plain");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("text/plain", stub.request.get(Rack.HTTP_ACCEPT));
	}

	public void testFullWildCard() throws Exception {
		((FormatNegotiator)app).withSupportedFormat("application/xml").withSupportedFormat("text/html").withSupportedFormat("text/plain");
		request.with(Rack.HTTP_ACCEPT, "application/json,*/*");
		get("/lala");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}
}
