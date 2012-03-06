package test;

import java.io.File;

import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.middleware.naughty_step.NaughtyStep;

public class NaughtyStepTest extends MiddlewareTestCase {
	
	public void setUp() {
		super.setUp("naughty_step");
		app = new NaughtyStep(stub, new File("src/test/input/404.html"), new File("src/test/input/500.html"));
		assertEquals(0, stub.called);
	}
	
	public void testNormalResponse() throws Exception {
		get("/whatever");
		assertEquals(1, stub.called);
		assertEquals(200, ret.getObject(Rack.MESSAGE_STATUS));
		assertEquals(RackBody.Type.literal, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testMissingResource() throws Exception {
		stub.with(Rack.MESSAGE_STATUS, 404);
		get("/whatever");
		assertEquals(1, stub.called);
		assertEquals(404, ret.getObject(Rack.MESSAGE_STATUS));
		RackBody body = (RackBody)ret.getObject(Rack.MESSAGE_BODY);
		assertEquals(RackBody.Type.file, body.getType());
		assertEquals("404.html", body.getBodyAsFile().getName());
	}
	
	public void testException() throws Exception {
		stub.remove(Rack.MESSAGE_STATUS);
		get("/whatever");
		assertEquals(1, stub.called);
		assertEquals(500, ret.getObject(Rack.MESSAGE_STATUS));
		RackBody body = (RackBody)ret.getObject(Rack.MESSAGE_BODY);
		assertEquals(RackBody.Type.file, body.getType());
		assertEquals("500.html", body.getBodyAsFile().getName());
		assertTrue(errors.toString().contains("java.lang.NullPointerException"));
	}

}
