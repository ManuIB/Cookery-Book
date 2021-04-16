package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.m6designer.cookeryBook.models.entity.Measure;

public interface IMeasureService {

	public List<Measure> findAll();

	public Page<Measure> findAll(Pageable pageable);

	public void save(Measure measure);

	public Measure findOne(Long id);

	public void delete(Long id);

	public List<Measure> findByName(String term);
}
