package com.m6designer.cookeryBook.models.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.entity.SpringUser;

@Service
public class ToolsServiceImpl implements IToolsService {

	@Override
	@Transactional(readOnly = true)
	public SpringUser getSpringUser(Authentication authentication) {

		SpringUser user = null;

		if (authentication != null) {
			user = (SpringUser) authentication.getPrincipal();
		}

		return user;
	}
}
