package com.zarcode.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.zarcode.shared.model.OpenIdProviderDO;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("register")
public interface RegisterService extends RemoteService {
	String greetServer(String name);
	List<OpenIdProviderDO> getProviders();
	String manualRegister(String emailAddr);
	List<String> setup(String requestUri);
}
