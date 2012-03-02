package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.body.StringRackBody;
import org.rack4java.context.MapContext;
import org.rack4java.middleware.funky_cache.FunkyCache;

public class FunkyCacheTest extends TestCase {
	StubRack stub;
	File root;
	FunkyCache app;
	Context<String> ret;
	
	public void setUp() {
		stub = new StubRack()
			.with(Rack.RESPONSE_BODY, new StringRackBody("hello"))
			.with(Rack.HTTP_CONTENT_TYPE, "text/html");
		root = new File("output/funky-cache");
		clear(root);
		app = new FunkyCache(stub, root);
		assertEquals(0, stub.called);
	}

	private void clear(File root) {
		for (File child : root.listFiles()) {
			String name = child.getName();
			if (name.equals(".") || name.equals("..")) continue;
			if (child.isDirectory()) {
				clear(child);
			}
			child.delete();
		}
	}

	private Context<String> call(String path) throws Exception {
		return app.call(new MapContext<String>()
				.with(Rack.REQUEST_METHOD, "GET")
				.with(Rack.SCRIPT_NAME, "stub")
				.with(Rack.PATH_INFO, path)
				.with(Rack.QUERY_STRING, "")
			);
	}

	private void createFile(String name) throws IOException {
		File file = new File(root, name);
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		out.write("hello".getBytes());
		out.close();
	}
	
	
	public void testFileAlreadyExists() throws Exception {
		createFile("ugh.html");
		ret = call("ugh");
		
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.RESPONSE_BODY)).getType());
	}
	
	public void testFileWithDefaultLeafAlreadyExists() throws Exception {
		createFile("ugh/index.html");
		ret = call("ugh/");

		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.RESPONSE_BODY)).getType());
	}
	
	public void testFileCreatedAndUsedTheNextTime() throws Exception {
		ret = call("ugh");
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)ret.getObject(Rack.RESPONSE_BODY)).getType());
		
		stub.reset();

		ret = call("ugh");
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.RESPONSE_BODY)).getType());
	}
}
