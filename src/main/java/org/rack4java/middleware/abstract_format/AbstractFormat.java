package org.rack4java.middleware.abstract_format;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.data.Mime;

public class AbstractFormat implements Rack {

	private Rack application;
	private String dfl = null;

	public AbstractFormat(Rack application) {
		this.application = application;
	}

	public AbstractFormat(Rack application, String dfl) {
		this.application = application;
		this.dfl = dfl;
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		String path = environment.get(PATH_INFO);
		int slash = path.lastIndexOf('/');
		String leaf = (slash > 0) 
			? path.substring(slash+1) 
			: "";
		int dot = leaf.lastIndexOf('.');
		String ext = (dot >= 0) 
			? leaf.substring(dot) 
			: "";
		String type = "".equals(ext)
			? dfl
			: Mime.MIME_TYPES.get(ext);
		String accept = environment.get(HTTP_ACCEPT);
		if (null == accept)	{
			environment.with(HTTP_ACCEPT, type);
		} else if (!accept.equals(type)) {
			environment.with(HTTP_ACCEPT, type + "," + accept);
		}
		return application.call(environment);
	}

	public Rack withDefaultFormat(String string) {
		this.dfl = string;
		return this;
	}

}
