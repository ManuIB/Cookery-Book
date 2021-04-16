package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.dao.ICategoryDao;
import com.m6designer.cookeryBook.models.entity.Category;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	ICategoryDao categoryDao;

	@Override
	@Transactional(readOnly = true)
	public List<Category> findAll() {
		return (List<Category>) categoryDao.findAll();
	}

	@Override
	@Transactional
	public void save(Category category) {
		categoryDao.save(category);
	}

	@Override
	@Transactional(readOnly = true)
	public Category findOne(Long id) {
		return categoryDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		categoryDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Category> findAll(Pageable pageable) {
		return categoryDao.findAll(pageable);
	}
}
