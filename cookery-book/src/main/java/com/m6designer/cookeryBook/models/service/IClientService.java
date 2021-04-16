package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.m6designer.cookeryBook.models.entity.Client;
import com.m6designer.cookeryBook.models.entity.Recipe;

public interface IClientService {

	public List<Client> findAll();

	public Page<Client> findAll(Pageable pageable);

	public void save(Client client);

	public Client findOne(Long id);

	public Client fetchByIdWithRecipes(Long id);

	public void delete(Long id);

	public void saveRecipe(Recipe recipe);

	public Recipe findRecipeById(String id);

	public void deleteRecipe(String id);

	public Recipe fetchRecipeWithClientWithRecipeWithMeasuresWithIngredients(String id);

	public Client findByUsername(String name);

	public boolean checkEmailNewUser(Client client);
}
