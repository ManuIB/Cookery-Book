package com.m6designer.cookeryBook.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m6designer.cookeryBook.models.dao.IMeasureDao;
import com.m6designer.cookeryBook.models.entity.Measure;

@Service
public class MeasureServiceImpl implements IMeasureService {

	@Autowired
	IMeasureDao measureDao;

	@Override
	@Transactional(readOnly = true)
	public List<Measure> findAll() {
		return (List<Measure>) measureDao.findAll();
	}

	@Override
	@Transactional
	public void save(Measure measure) {
		measureDao.save(measure);
	}

	@Override
	@Transactional(readOnly = true)
	public Measure findOne(Long id) {
		return measureDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		measureDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Measure> findAll(Pageable pageable) {
		return measureDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Measure> findByName(String term) {
		return measureDao.findByName(term);
	}
}
