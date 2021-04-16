package com.m6designer.cookeryBook.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.m6designer.cookeryBook.models.entity.Category;

public interface ICategoryDao extends PagingAndSortingRepository<Category, Long> {

}
