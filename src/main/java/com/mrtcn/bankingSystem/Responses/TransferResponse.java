package com.mrtcn.bankingSystem.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResponse {
	private String message;
}