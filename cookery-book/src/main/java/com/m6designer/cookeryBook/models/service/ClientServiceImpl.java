package com.m6designer.cookeryBook.models.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.dao.IClientDao;
import com.m6designer.cookeryBook.models.dao.IRecipeDao;
import com.m6designer.cookeryBook.models.entity.Client;
import com.m6designer.cookeryBook.models.entity.Recipe;
import com.m6designer.cookeryBook.models.entity.Role;

@Service
public class ClientServiceImpl implements IClientService {

	@Autowired
	IClientDao clientDao;

	@Autowired
	IRecipeDao recipeDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}

	@Override
	@Transactional
	public void save(Client client) {

		// Check if it is a new client or we are editing an existing one
		if (client.getId() == null) {
			// Photo never can be null
			if (client.getPhoto() == null) {
				client.setPhoto("");
			}
			// Today's date
			client.setCreatedAt(new Date());
			// Enabled = true
			client.setIsEnabled(true);
			// Save encrypted password
			client.setPassword(passwordEncoder.encode(client.getPassword()));
			// Add Role
			Role role = new Role();
			role.setAuthority("ROLE_USER");
			role.setClient(client);
			client.addRoles(role);
		} else {
			// Check if there is a new password
			if (!client.getPassword().isEmpty()) {
				// Save encrypted password
				client.setPassword(passwordEncoder.encode(client.getPassword()));
			} else {
				// Assign the old password
				client.setPassword(findByUsername(client.getEmail()).getPassword());
			}
		}
		// Today's date
		client.setUpdatedAt(new Date());

		clientDao.save(client);
	}

	@Override
	@Transactional(readOnly = true)
	public Client findOne(Long id) {
		return clientDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Client fetchByIdWithRecipes(Long id) {

		return clientDao.fetchByIdWithRecipes(id);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clientDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Client> findAll(Pageable pageable) {
		return clientDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void saveRecipe(Recipe recipe) {
		recipeDao.save(recipe);
	}

	@Override
	@Transactional(readOnly = true)
	public Recipe findRecipeById(String id) {

		return recipeDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteRecipe(String id) {

		recipeDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Recipe fetchRecipeWithClientWithRecipeWithMeasuresWithIngredients(String id) {

		return recipeDao.fetchByIdWithClientWithRecipeWithMeasuresWithIngredients(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Client findByUsername(String username) {

		return clientDao.findByEmail(username);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean checkEmailNewUser(Client client) {

		Client oldClientData = findByUsername(client.getEmail());

		if (oldClientData != null && (client.getId() == null || !client.getId().equals(oldClientData.getId()))) {
			return true;
		} else {
			return false;
		}
	}
}
