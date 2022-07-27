package com.mrtcn.bankingSystem.Services;

import com.mrtcn.bankingSystem.Interfaces.ICreateAccountService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class CreateAccountService implements ICreateAccountService{

    @Autowired
    private AccountRepository repository;
	
	@Override
    public AccountCreateResponse createAccount(NewAccountRequest request){

        //Account type checks made here.
        if (!(request.getType().equals("TL") || request.getType().equals("Dolar") || request.getType().equals("Altın"))){
            AccountCreateResponse response = AccountCreateResponse.builder()
                    .message("Invalid Account Type: " + request.getType())
                    .build();
            return response;
        }

        //10-Digit Random number for account number.
        long randomNumber = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        //Account object gets build.
        Account acc = Account.builder()
                .number(randomNumber)
                .type(request.getType())
                .email(request.getEmail())
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .isDeleted(false)
                .lastUpdate(ZonedDateTime.now().toInstant())
                .build();

        //Account gets inserted into database.
        this.repository.insertAccountWithNumber(acc);
        
        //A response for json response body.
        AccountCreateResponse response = AccountCreateResponse.builder()
                .message("Account Created")
                .accountNumber(randomNumber)
                .build();

        return response;
    }
}
