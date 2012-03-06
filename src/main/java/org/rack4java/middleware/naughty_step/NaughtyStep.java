package org.rack4java.middleware.naughty_step;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.body.FileRackBody;
import org.rack4java.context.MapContext;


public class NaughtyStep implements Rack {
	private Rack application;
	private File missing;
	private File error;
	
	public NaughtyStep(Rack application, File missing, File error) {
		this.application = application;
		this.missing = missing;
		this.error = error;
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		Context<String> ret;
		try {
			ret = application.call(environment);
			int status = (Integer)ret.getObject(MESSAGE_STATUS);
			if (404 == status) ret.with(MESSAGE_BODY, new FileRackBody(missing));
		} catch (Exception e) {
			OutputStream stream = (OutputStream) environment.getObject(RACK_ERRORS);
			PrintWriter writer = new PrintWriter(stream);
			e.printStackTrace(writer);
			writer.flush();
			ret = new MapContext<String>()
				.with(MESSAGE_STATUS, 500)
				.with(MESSAGE_BODY, new FileRackBody(error));
		}
		return ret;
	}

}
