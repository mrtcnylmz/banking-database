package com.mrtcn.bankingSystem.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.mrtcn.bankingSystem.Interfaces.ICreateAccountService;
import com.mrtcn.bankingSystem.Interfaces.IDeleteAccountService;
import com.mrtcn.bankingSystem.Interfaces.IDepositService;
import com.mrtcn.bankingSystem.Interfaces.IGetAccountService;
import com.mrtcn.bankingSystem.Interfaces.ILogService;
import com.mrtcn.bankingSystem.Interfaces.ITransferService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.DepositRequest;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import com.mrtcn.bankingSystem.Responses.DeleteResponse;
import com.mrtcn.bankingSystem.Responses.LogResponse;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

@RestController
public class BankingController {
	
	@Autowired
	private ICreateAccountService createAccountService;
	
	@Autowired
	private IGetAccountService getAccountService;
	
	@Autowired
	private IDepositService depositService;
	
	@Autowired
	private ITransferService transferService;
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IDeleteAccountService deleteAccountService;

	@Autowired
	private KafkaTemplate<String,String> producer;

	//1
	//A service to create a user account.
	@RequestMapping(path = "account/register", method = RequestMethod.POST)
	public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody NewAccountRequest request){
		
		AccountCreateResponse response = this.createAccountService.createAccount(request);
		
		if(response.getMessage().equals("Account Created")) {
            return ResponseEntity
                    .ok()
                    .header("content-type","application/json")
                    .body(response);
		}else 
            return ResponseEntity
                    .badRequest()
                    .header("content-type","application/json")
                    .body(response);
	}

	//2
	//A service to get account information from account number.
	@RequestMapping(path = "account/{id}", method = RequestMethod.GET)
	public ResponseEntity<Account> getAccount(@PathVariable Long id){
		
		Account account = this.getAccountService.getAccount(id);
		
		if(account != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.lastModified(account.getLastUpdate())
					.body(account);
		}else
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();

	}

	//3
	//A service that takes an account number and handles monetary deposits accordingly.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.POST)
	public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody DepositRequest request) {
		
		Account account = this.depositService.deposit(id, request, producer);
		
		if(account != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.lastModified(account.getLastUpdate())
					.body(account);
		}else
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();
	}

	//4
	//A service to make balance transfers between accounts.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<TransferResponse> transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
		
		TransferResponse response = this.transferService.transfer(id, request, producer);
		
		if (response == null) {
			return ResponseEntity
					.notFound()
					.header("content-type", "application/json")
					.build();
		}else if(response.getMessage().equals("Transferred Successfully.")) {
			return ResponseEntity
					.ok()
					.header("content-type", "application/json")
					.body(response);
			
		}else{
			return ResponseEntity
					.badRequest()
					.header("content-type", "application/json")
					.body(response);
			
		}
	}

	//5
	//A service to access logs received and logged by kafka.
	@CrossOrigin(origins = {"http://localhost:6162"})
	@RequestMapping(path = "account/{id}/logs", method = RequestMethod.GET)
	public ResponseEntity<List<LogResponse>> getLog(@PathVariable Long id){
		
		List<LogResponse> response = this.logService.getLog(id);
		
		if(response != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.body(response);
			
		}else
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();
	}
	
	//6
	//A service to delete(soft delete) an account.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<DeleteResponse> delete(@PathVariable Long id){
		
		DeleteResponse response = this.deleteAccountService.delete(id);
		
		if (response != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.body(response);
		}else {
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();
		}
	}
}
