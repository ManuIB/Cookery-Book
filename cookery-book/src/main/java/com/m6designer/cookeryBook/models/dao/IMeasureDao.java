package com.m6designer.cookeryBook.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.m6designer.cookeryBook.models.entity.Measure;

public interface IMeasureDao extends PagingAndSortingRepository<Measure, Long> {

	@Query("select p from Measure p where p.name like %?1%")
	public List<Measure> findByName(String term);
}
