package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.kafka.core.KafkaTemplate;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

public interface ITransferService {
	
	public TransferResponse transfer(Long id, TransferRequest request, KafkaTemplate<String,String> producer);
	
}
