package com.m6designer.cookeryBook.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.dao.IClientDao;
import com.m6designer.cookeryBook.models.entity.Client;
import com.m6designer.cookeryBook.models.entity.Role;
import com.m6designer.cookeryBook.models.entity.SpringUser;

@Service("jpaClientDetailsService")
public class JPpaClientDetailsService implements UserDetailsService {

	@Autowired
	private IClientDao clientDao;

	private Logger logger = LoggerFactory.getLogger(JPpaClientDetailsService.class);

	// username = email
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Client client = clientDao.findByEmail(username);

		boolean isAdmin = false;

		if (client == null) {
			logger.error("Error login: no existe el usuario '" + username + "'");
			throw new UsernameNotFoundException("Username " + username + " no existe en el sistema!");
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Role role : client.getRoles()) {
			logger.info("Role: ".concat(role.getAuthority()));
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
			if (role.getAuthority().equals("ROLE_ADMIN")) {
				isAdmin = true;
			}
		}

		if (authorities.isEmpty()) {
			logger.error("Error login: usuario '" + username + "' no tiene roles asignados!");
			throw new UsernameNotFoundException("Error login: usuario '" + username + "' no tiene roles asignados!");
		}

		return new SpringUser(client.getEmail(), client.getPassword(), client.getIsEnabled(), true, true, true,
				authorities, client.getId(), client.getFirstName(), isAdmin);
	}
}
