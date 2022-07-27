package com.mrtcn.bankingSystem.Services;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mrtcn.bankingSystem.Interfaces.ITransferService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

@Component
public class TransferService implements ITransferService{

    @Autowired
    private AccountRepository repository;
	
    @Transactional(isolation =Isolation.SERIALIZABLE)
	@Override
	public TransferResponse transfer(Long id, TransferRequest request, KafkaTemplate<String, String> producer) {
		
		//Response init.
		TransferResponse response = TransferResponse.builder().build();

		//Account numbers.
		long senderAccountNumber = id;
		long receiverAccountNumber = request.getTransferredAccountNumber();
		
		//Accounts.
		Account senderAccount = this.repository.selectAccountWithNumber(senderAccountNumber);
		Account receiverAccount = this.repository.selectAccountWithNumber(receiverAccountNumber);
		
		//Checks if accounts exists or deleted.
		if (senderAccount == null || receiverAccount == null || senderAccount.isDeleted() || receiverAccount.isDeleted()) {
			return null;
		}
		
		//Balances and amounts.
		int senderBalance = senderAccount.getBalance();
		int receiverBalance = receiverAccount.getBalance();
		int transferAmount = request.getAmount();
		
		//Checks if sender account has enough balance to send required amount.
		if (senderBalance < transferAmount || transferAmount <= 0) {
			response.setMessage("Insufficient/Invalid balance amount.");
			return response;
		}
		
		//If sender and receiver accounts belong to different account types then CurrencyExchange service is necessary to convert between currency types.
		if (!(senderAccount.getType().equals(receiverAccount.getType()))){
			//Database processes on sender account.
			int senderNewBalance = senderAccount.getBalance() - transferAmount;
			this.repository.updateAccountBalance(senderAccountNumber, senderNewBalance);
			this.repository.updateAccountLastUpdate(senderAccountNumber, ZonedDateTime.now().toInstant());
			
			//Database processes on receiver account.
			int receiverNewBalance = receiverBalance + (int)(transferAmount * (new CurrencyExchange().Exchange(senderAccount,receiverAccount)));
			this.repository.updateAccountBalance(receiverAccountNumber, receiverNewBalance);
			this.repository.updateAccountLastUpdate(receiverAccountNumber, ZonedDateTime.now().toInstant());
			
			String log =
					senderAccountNumber + " " +
					"transfer_amount:" + " " +
					transferAmount + " " + 
					"transferred_account:" + " " +
					receiverAccountNumber + " " +
					senderAccount.getType();

			//Log sent to kafka.
			producer.send("logs", log);

			response.setMessage("Transferred Successfully.");
			return response;
			
		}else {	//If accounts are same type.
			
			//Database processes on sender account.
			int senderNewBalance = senderBalance - request.getAmount();
			this.repository.updateAccountBalance(senderAccountNumber, senderNewBalance);
			this.repository.updateAccountLastUpdate(senderAccountNumber, ZonedDateTime.now().toInstant());
			
			//Database processes on receiver account.
			int receiverNewBalance = receiverBalance + request.getAmount();
			this.repository.updateAccountBalance(receiverAccountNumber, receiverNewBalance);
			this.repository.updateAccountLastUpdate(receiverAccountNumber, ZonedDateTime.now().toInstant());
			
			String log =
					senderAccountNumber + " " +
					"transfer_amount:" + " " +
					transferAmount + " " + 
					"transferred_account:" + " " +
					receiverAccountNumber + " " +
					senderAccount.getType();

			//Log sent to kafka.
			producer.send("logs", log);

			response.setMessage("Transferred Successfully.");
			return response;
		}
	}

}
