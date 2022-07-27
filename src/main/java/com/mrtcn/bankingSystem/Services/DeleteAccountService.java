package com.mrtcn.bankingSystem.Services;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.IDeleteAccountService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;
import com.mrtcn.bankingSystem.Responses.DeleteResponse;

@Component
public class DeleteAccountService implements IDeleteAccountService {

    @Autowired
    private AccountRepository repository;
	
	@Override
	public DeleteResponse delete(Long id) {
		
		//Account data fetched with account id from database.
		Account account = this.repository.selectAccountWithNumber(id);
		
		//Check if account exists in database, if not, return accordingly.
		if (account == null || account.isDeleted()) {
			return null;
			
		}else {
			
			//Soft delete and account update date send to database.
			this.repository.updateAccountIsDeleted(id, true);
			this.repository.updateAccountLastUpdate(id, ZonedDateTime.now().toInstant());
			
			//Response and its message.
			DeleteResponse response = DeleteResponse.builder()
					.message("Account has been successfully deleted!")
					.build();
			
			return response;
		}
	}
}
