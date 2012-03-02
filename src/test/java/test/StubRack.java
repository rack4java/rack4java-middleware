package test;

import java.util.Map;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.context.MapContext;

public class StubRack implements Rack {
	
	public Context<String> canned;
	public int called;
	
	public StubRack(Context<String> canned) {
		this.canned = canned;
		reset();
	}
	
	public StubRack() {
		this(new MapContext<String>());
	}
	
	public StubRack with(String key, Object value) {
		canned.with(key, value);
		return this;
	}
	
	public Object remove(String key) {
		return canned.remove(key);
	}
	
	public void reset() {
		called = 0;
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		for (Map.Entry<String, Object> entry : canned) {
			environment.with(entry.getKey(), entry.getValue());
		}
		++called;
		return environment;
	}

}
