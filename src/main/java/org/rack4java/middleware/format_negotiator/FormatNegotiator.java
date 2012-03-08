package org.rack4java.middleware.format_negotiator;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.data.Mime;
import org.rack4java.utils.StringUtils;

public class FormatNegotiator implements Rack {

	private Rack application;

	public FormatNegotiator(Rack application) {
		this.application = application;
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		if (StringUtils.isBlank(environment.get(HTTP_ACCEPT))) environment.with(Rack.HTTP_ACCEPT, Mime.DEFAULT_MIME_TYPE);
		
		return application.call(environment);
	}

}