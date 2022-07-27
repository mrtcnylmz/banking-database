package com.mrtcn.bankingSystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.IGetAccountService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;

@Component
public class GetAccountService implements IGetAccountService {
	
    @Autowired
    private AccountRepository repository;

	@Override
	public Account getAccount(Long id) {
		
		//Account data from database.
		Account account = this.repository.selectAccountWithNumber(id);
		
		//Checks if account exist or deleted.
		if(account == null || account.isDeleted()) {
			return null;
		}else {
			return account;
		}
	}
}
