package org.rack4java.middleware.funky_cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.RackResponse;
import org.rack4java.body.FileRackBody;

/** 
 * a very simplistic page caching system
 * 
 * The first time a GET for a particular path (with no query parameters etc.) is seen
 * a file is created from the response body. On subsequenet requests, the pre-rendered
 * file is returned instead.
 * 
 * Note that if the enclosing server automatically serves static content, for example from
 * a "public" directory, then setting the root file to that directory will mean that cached 
 * files are served directly by the container; only the first request comes through rack4java.
 * 
 * @see https://github.com/tuupola/rack-funky-cache
 */
public class FunkyCache implements Rack {

	private Rack application;
	private File root;

	public FunkyCache(Rack application, File root) {
		this.application = application;
		this.root = root;
	}
	
	@Override public Context<String> call(Context<String> env) throws Exception {
		if (appropriateRequest(env)) {
			File file = getFilePath(env);
			if (file.exists()) {
				env.with(Rack.MESSAGE_BODY, new FileRackBody(file));
			} else {
				env = application.call(env);
				if (appropriateResponse(env)) cache(env, file);
			}
		} else {
			env = application.call(env);
		}
		
		return env;
	}

	private File getFilePath(Context<String> env) {
		String path = env.get(PATH_INFO);
		if (path.endsWith("/")) path += "index";
		path += ".html";
		return new File(root, path);
	}

	private void cache(Context<String> env, File file) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(RackResponse.getBodyAsBytes(env));
		} finally {
			if (null != out) out.close();
		}
	}
	
	private boolean appropriateRequest(Context<String> env) {
		return
			"GET".equalsIgnoreCase(env.get(REQUEST_METHOD)) && 
			"".equals(env.get(QUERY_STRING));
	}

	private boolean appropriateResponse(Context<String> env) {
		RackBody body = (RackBody) env.getObject(MESSAGE_BODY);

		return
			null != body &&
			body.getType() != RackBody.Type.file && 
			env.get(HTTP_CONTENT_TYPE).startsWith("text/html");
	}

}
