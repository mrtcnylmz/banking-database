package com.mrtcn.bankingSystem.Interfaces;

import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;

public interface ICreateAccountService {
	
	public AccountCreateResponse createAccount(NewAccountRequest request);
	
}
