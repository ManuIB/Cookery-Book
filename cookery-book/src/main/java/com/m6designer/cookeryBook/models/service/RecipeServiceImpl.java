package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.dao.IRecipeDao;
import com.m6designer.cookeryBook.models.entity.Recipe;

@Service
public class RecipeServiceImpl implements IRecipeService {

	@Autowired
	IRecipeDao recipeDao;

	@Override
	@Transactional(readOnly = true)
	public List<Recipe> findAll() {
		return (List<Recipe>) recipeDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Recipe findOne(String id) {
		return recipeDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Recipe> findAll(Pageable pageable) {
		return recipeDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void delete(String id) {
		recipeDao.deleteById(id);
	}
}
