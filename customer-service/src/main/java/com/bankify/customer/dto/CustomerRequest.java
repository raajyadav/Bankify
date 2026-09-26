package com.bankify.customer.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRequest {

	@NotBlank(message = "First name is requires")
	private String firstName;
	
	@NotBlank(message = "Last name is required")
	private String lastName;
	
	@NotBlank(message = "Email is requird")
	@Email(message = "Please provide a valid email")
	private String email;
	
	@NotBlank(message = "Phone number is requird")
	@Pattern(
		regexp = "^[0-9]{10}$",
		message = "Phone number must contain exactly 10 digits"
	)
	private String phone;
	
	@NotNull(message = "Date of birth is requird")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;
	
	@NotBlank(message = "Address is requird")
	private String address;
}
