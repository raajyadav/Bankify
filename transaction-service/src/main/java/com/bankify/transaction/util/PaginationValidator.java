package com.bankify.transaction.util;

import com.bankify.transaction.exception.InvalidPaginationException;

public class PaginationValidator {

	private static final int MAX_PAGE_SIZE = 10;
	
	private PaginationValidator() {
		
	}
	
	public static void validate(int page , int size) {
		
		if(page < 0) {
			
			throw new InvalidPaginationException(
				"Page number cannot be less than 0"	
		   );
		}
		
		if(size <= 0) {
			
			throw new InvalidPaginationException(
				"Page size must be greater than 0"	
			);
		}
		
	    if (size > MAX_PAGE_SIZE) {
	        throw new InvalidPaginationException(
	                "Page size cannot be greater than " + MAX_PAGE_SIZE
	        );
	    }
	}
}
