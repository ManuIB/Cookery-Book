package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.m6designer.cookeryBook.models.entity.Recipe;

public interface IRecipeService {

	public List<Recipe> findAll();

	public Page<Recipe> findAll(Pageable pageable);

	public Recipe findOne(String id);

	public void delete(String id);
}
