package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.m6designer.cookeryBook.models.entity.Category;

public interface ICategoryService {

	public List<Category> findAll();

	public Page<Category> findAll(Pageable pageable);

	public void save(Category category);

	public Category findOne(Long id);

	public void delete(Long id);
}
