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
		negotiate(environment);
		return application.call(environment);
	}

	private void negotiate(Context<String> environment) {
		String accept = environment.get(HTTP_ACCEPT);
		String preferred = null;
		if (null != accept) {
			String[] options = accept.split(",");
			preferred = options[0];
		}
		if (StringUtils.isBlank(preferred)) preferred = Mime.DEFAULT_MIME_TYPE;
		
		environment.with(Rack.HTTP_ACCEPT, preferred);
	}

}