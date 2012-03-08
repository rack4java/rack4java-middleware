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
		double bestq = 0;
		if (null != accept) {
			String[] options = accept.split(",");
			for (String option : options) {
				double q = 0;
				String[] detail = option.split(";");
				String type = detail[0];
				if (detail.length == 1) {
					q = 1.0;
				} else {
					q = Double.parseDouble((detail[1].split("="))[1].trim());
				}
				if (q > bestq) {
					bestq = q;
					preferred = type;
				}
			}
		}
		if (StringUtils.isBlank(preferred)) preferred = Mime.DEFAULT_MIME_TYPE;
		
		environment.with(Rack.HTTP_ACCEPT, stripq(preferred));
	}

	private String stripq(String option) {
		int semicolon = option.indexOf(';');
		if (semicolon >= 0) option = option.substring(0, semicolon);
		return option;
	}

}