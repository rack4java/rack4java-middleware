package test;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.context.MapContext;

public class StubRack implements Rack {
	
	public Context<String> request;
	public MapContext<String> response;
	public int called;
	
	public StubRack(Context<String> canned) {
		this.response = new MapContext<String>().with(canned);
		reset();
	}
	
	public StubRack() {
		this(new MapContext<String>());
	}
	
	public StubRack with(String key, Object value) {
		response.with(key, value);
		return this;
	}
	
	public StubRack with(Context<String> context) {
		response.with(context);
		return this;
	}
	
	public Object remove(String key) {
		return response.remove(key);
	}
	
	public void reset() {
		called = 0;
		request = null;
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		this.request = environment;
		++called;
		return response;
	}

}
