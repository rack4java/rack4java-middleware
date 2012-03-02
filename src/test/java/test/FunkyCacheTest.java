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
			.with(Rack.MESSAGE_BODY, new StringRackBody("hello"))
			.with(Rack.HTTP_CONTENT_TYPE, "text/html");
		root = new File("output/funky-cache");
		clear(root);
		app = new FunkyCache(stub, root);
		assertEquals(0, stub.called);
	}

	private void clear(File root) {
		if (null == root) return;
		File[] files = root.listFiles();
		if (null != files && files.length > 0) for (File child : files) {
			String name = child.getName();
			if (name.equals(".") || name.equals("..")) continue;
			if (child.isDirectory()) {
				clear(child);
			}
			child.delete();
		}
	}

	protected Context<String> call(String method, String path, String query, String body) throws Exception {
		return app.call(new MapContext<String>()
				.with(Rack.REQUEST_METHOD, method)
				.with(Rack.SCRIPT_NAME, "stub")
				.with(Rack.PATH_INFO, path)
				.with(Rack.QUERY_STRING, query)
				.with(Rack.MESSAGE_BODY, new StringRackBody(body))
			);
	}

	protected Context<String> get(String path) throws Exception {
		return call("GET", path, "", "");
	}

	protected Context<String> get(String path, String query) throws Exception {
		return call("GET", path, query, "");
	}
	
	protected Context<String> post(String path, String body) throws Exception {
		return call("POST", path, "", body);
	}

	private void createFile(String name) throws IOException {
		File file = new File(root, name);
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		out.write("hello".getBytes());
		out.close();
	}
	
	
	public void testServeFileIfItAlreadyExists() throws Exception {
		createFile("ugh.html");
		ret = get("ugh");
		
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testQueryBypassesCache() throws Exception {
		createFile("ugh.html");
		ret = get("ugh", "a=b");
		
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testPostBypassesCache() throws Exception {
		createFile("ugh.html");
		ret = post("ugh", "a=b");
		
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testFileWithDefaultLeafAlreadyExists() throws Exception {
		createFile("ugh/index.html");
		ret = get("ugh/");

		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
	
	public void testFileCreatedAndUsedTheNextTime() throws Exception {
		ret = get("ugh");
		assertEquals(1, stub.called);
		assertEquals(RackBody.Type.literal, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
		
		stub.reset();

		ret = get("ugh");
		assertEquals(0, stub.called);
		assertEquals(RackBody.Type.file, ((RackBody)ret.getObject(Rack.MESSAGE_BODY)).getType());
	}
}
