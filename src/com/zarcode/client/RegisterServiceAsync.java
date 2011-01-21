package com.zarcode.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.zarcode.shared.model.OpenIdProviderDO;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface RegisterServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback);
	void getProviders(AsyncCallback<List<OpenIdProviderDO>> callback);
	void manualRegister(String emailAddr, AsyncCallback<String> callback);
	void setup(String requestUri, AsyncCallback<List<String>> callback);
}
