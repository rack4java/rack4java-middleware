package org.rack4java.middleware.format_negotiator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.context.ContextEntry;
import org.rack4java.data.Mime;

public class FormatNegotiator implements Rack {

	private Rack application;
	private List<Map.Entry<String, Double>> supportedFormats;

	public FormatNegotiator(Rack application) {
		this.application = application;
		this.supportedFormats = new LinkedList<Map.Entry<String, Double>>();
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		negotiate(environment);
		return application.call(environment);
	}

	private void negotiate(Context<String> environment) {
		List<Map.Entry<String, Double>> requestedFormats = new LinkedList<Map.Entry<String, Double>>();
		String accept = environment.get(HTTP_ACCEPT);
		if (null != accept) {
			String[] options = accept.split(",");
			for (String option : options) {
				double q = 0;
				String[] detail = option.split(";");
				String type = detail[0].trim();
				if (detail.length == 1) {
					q = 1.0;
				} else {
					q = Double.parseDouble((detail[1].split("="))[1].trim());
				}
				
				insert(requestedFormats, type, q);
			}
		}
		
		String preferred = select(requestedFormats, supportedFormats);
		
		environment.with(Rack.HTTP_ACCEPT, preferred);
	}

	private void insert(List<Map.Entry<String, Double>> formats, String type, double q) {
		boolean inserted = false;
		for (int i = 0; i < formats.size(); ++i) {
			Map.Entry<String, Double> entry = formats.get(i);
			if (q > entry.getValue()) {
				formats.add(i, new ContextEntry<Double>(type, q));
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			formats.add(new ContextEntry<Double>(type, q));
		}
	}

	private String select(List<Map.Entry<String, Double>> requestedFormats, List<Map.Entry<String, Double>> supportedFormats) {
		for (Map.Entry<String, Double> requested : requestedFormats) {
			String type = requested.getKey();
			if (supportedFormats.isEmpty()) return type;
			
			for (Map.Entry<String, Double> supported : supportedFormats) {
				if (type.equals(supported.getKey())) {
					return type;
				}
			}
		}
		return Mime.DEFAULT_MIME_TYPE;
	}

	public FormatNegotiator withSupportedFormat(String format, double q) {
		insert(supportedFormats, format, q);
		return this;
	}

	public FormatNegotiator withSupportedFormat(String format) {
		return withSupportedFormat(format, 1.0);
	}

}