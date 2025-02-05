package com.mugiwara.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "FirstName is mandatory")
    @NotBlank(message = "FirstName is mandatory")
    private String firstName;
    @NotEmpty(message = "LastName is mandatory")
    @NotBlank(message = "LastName is mandatory")
    private String lastName;
    @Email(message = "Email is not well Formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be in 8 characters long minimum")
    private String password;

    /* @NotEmpty(message = "FirstName thappu da venna")
    @NotBlank(message = "FirstName thappu da venna")
    private String firstName;
    @NotEmpty(message = "LastName thappu da venna")
    @NotBlank(message = "LastName thappu da venna")
    private String lastName;
    @Email(message = "Email lah olunga eluthuda")
    @NotEmpty(message = "Email thappu da venna")
    @NotBlank(message = "Email thappu da venna")
    private String email;
    @NotEmpty(message = "Password thappu da venna")
    @NotBlank(message = "Password thappu da venna")
    @Size(min = 8, message = "Password minimum 8 char lah eluthuda")
    private String password;*/
}