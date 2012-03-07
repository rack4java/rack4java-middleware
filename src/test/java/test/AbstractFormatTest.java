package test;

import org.rack4java.Rack;
import org.rack4java.middleware.AbstractFormat;

public class AbstractFormatTest extends MiddlewareTestCase {
	public void setUp() {
		super.setUp("abstract-format");
		app = new AbstractFormat(stub);
		assertEquals(0, stub.called);
	}
	
	public void testExampleFromOiginal() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "text/html");
		get("/path/resource.xml");
		assertEquals(1, stub.called);
		assertEquals("application/xml,text/html", stub.request.get(Rack.HTTP_ACCEPT));
	}
	
	public void testExampleFromOiginalWithDefault() throws Exception {
		app = new AbstractFormat(stub).withDefaultFormat("application/json");
		request.with(Rack.HTTP_ACCEPT, "text/html");
		get("/path/resource");
		assertEquals(1, stub.called);
		assertEquals("application/json,text/html", stub.request.get(Rack.HTTP_ACCEPT));
	}
	
	public void testNoAcceptHeader() throws Exception {
		get("/path/resource.xml");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}
	
	public void testAlreadySpecified() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "application/xml");
		get("/path/resource.xml");
		assertEquals(1, stub.called);
		assertEquals("application/xml", stub.request.get(Rack.HTTP_ACCEPT));
	}
	
	public void testUnknownExtension() throws Exception {
		request.with(Rack.HTTP_ACCEPT, "text/html");
		get("/path/resource.???");
		assertEquals(1, stub.called);
		assertEquals("application/octet-stream,text/html", stub.request.get(Rack.HTTP_ACCEPT));
	}
}
