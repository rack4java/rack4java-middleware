package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.body.StringRackBody;
import org.rack4java.context.MapContext;

public abstract class MiddlewareTestCase extends TestCase {
	protected ByteArrayOutputStream errors;
	protected StubRack stub;
	protected Context<String> request;
	protected Context<String> response;
	protected Rack app;
	protected File root;
	
	protected Context<String> commonEnvironment = new MapContext<String>()
	    .with(Rack.RACK_VERSION, Arrays.asList(0, 2))
	    .with(Rack.RACK_ERRORS, (errors = new ByteArrayOutputStream()))
	    .with(Rack.RACK_MULTITHREAD, true)
	    .with(Rack.RACK_MULTIPROCESS, true)
	    .with(Rack.RACK_RUN_ONCE, false);
	
	public void setUp(String name) {
		root = new File("output/" + name);
		clear(root);
		
		request = new MapContext<String>()
			.with(commonEnvironment);

		stub = new StubRack()
			.with(Rack.MESSAGE_STATUS, 200)
			.with(Rack.MESSAGE_BODY, new StringRackBody("hello"))
			.with(Rack.HTTP_CONTENT_TYPE, "text/html");
	}

	protected void clear(File root) {
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

	protected Context<String> call(String method, String path, String query, String body)
			throws Exception {
				response = app.call(request
						.with(Rack.REQUEST_METHOD, method)
						.with(Rack.SCRIPT_NAME, "stub")
						.with(Rack.PATH_INFO, path)
						.with(Rack.QUERY_STRING, query)
						.with(Rack.MESSAGE_BODY, new StringRackBody(body))
					);
				return response;
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

	protected void createFile(String name) throws IOException {
		File file = new File(root, name);
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		out.write("hello".getBytes());
		out.close();
	}

}
