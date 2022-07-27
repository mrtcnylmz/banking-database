package com.mrtcn.bankingSystem.Services;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.IDepositService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;
import com.mrtcn.bankingSystem.Requests.DepositRequest;

@Component
public class DepositService implements IDepositService {

    @Autowired
    private AccountRepository repository;
	
	@Override
	public Account deposit(Long id, DepositRequest request, KafkaTemplate<String,String> producer) {
		
		//Account data from database.
		Account account = this.repository.selectAccountWithNumber(id);
		
		//Check if account exist or deleted.
		if (account == null || account.isDeleted()) {
			return null;
			
		}else {
			//New amount.
			int amount = account.getBalance() + request.getAmount();
			
			//Database updates.
			this.repository.updateAccountBalance(id, amount);
			this.repository.updateAccountLastUpdate(id, ZonedDateTime.now().toInstant());
			
			String log =
					account.getNumber() + " " +
					"deposit" + " " + "amount:" + " " +
					request.getAmount() + " " +
					account.getType();

				//Log send to kafka.
				producer.send("logs",log);
				account.setBalance(amount);
			return account;
		}
	}
}
