package com.m6designer.cookeryBook.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.m6designer.cookeryBook.models.entity.Client;

public interface IClientDao extends PagingAndSortingRepository<Client, Long> {

	@Query("select c from Client c left join fetch c.recipes r where c.id=?1")
	public Client fetchByIdWithRecipes(Long id);

	public Client findByEmail(String username);
}
