package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.kafka.core.KafkaTemplate;

import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.DepositRequest;

public interface IDepositService {
	
	public Account deposit(Long id, DepositRequest request, KafkaTemplate<String,String> producer);
	
}
