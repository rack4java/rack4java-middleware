package test;


import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.middleware.funky_cache.FunkyCache;

public class FunkyCacheTest extends MiddlewareTestCase {
	
	public void setUp() {
		super.setUp("funky_cache");
		app = new FunkyCache(stub, root);
		assertEquals(0, stub.called);
	}

	public void testServeFileIfItAlreadyExists() throws Exception {
		createFile("ugh.html");
		get("ugh");
		
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testQueryBypassesCache() throws Exception {
		createFile("ugh.html");
		get("ugh", "a=b");
		
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testPostBypassesCache() throws Exception {
		createFile("ugh.html");
		post("ugh", "a=b");
		
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testFileWithDefaultLeafAlreadyExists() throws Exception {
		createFile("ugh/index.html");
		get("ugh/");

		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testFileCreatedAndUsedTheNextTime() throws Exception {
		get("ugh");
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
		
		stub.reset();

		get("ugh");
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)response.getObject(Rack.MESSAGE_BODY)).getType());
	}
}
