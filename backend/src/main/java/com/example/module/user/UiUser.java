package com.example.module.user;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class UiUser {

	private Long id;

	private String firstName;

	private String lastName;	

	private OffsetDateTime dateOfBirth;
}
