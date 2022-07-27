package com.mrtcn.bankingSystem.Interfaces;

import com.mrtcn.bankingSystem.Responses.DeleteResponse;

public interface IDeleteAccountService {
	
	public DeleteResponse delete(Long id);
	
}
