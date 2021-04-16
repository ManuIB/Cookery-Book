package com.m6designer.cookeryBook.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.m6designer.cookeryBook.models.entity.Recipe;

public interface IRecipeDao extends PagingAndSortingRepository<Recipe, String> {

	@Query("select r from Recipe r join fetch r.client c join fetch r.ingredients i join fetch i.measure m where r.id=?1")
	public Recipe fetchByIdWithClientWithRecipeWithMeasuresWithIngredients(String id);
}
