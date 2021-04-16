package com.m6designer.cookeryBook.models.service;

import org.springframework.security.core.Authentication;

import com.m6designer.cookeryBook.models.entity.SpringUser;

public interface IToolsService {

	public SpringUser getSpringUser(Authentication authentication);
}
