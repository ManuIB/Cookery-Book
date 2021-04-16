package com.m6designer.cookeryBook.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {
	private String url;
	private Page<T> page;

	private int totalPages;
	private int numItemsPerPage;
	private int actualPage;

	private List<PageItem> pages;

	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.pages = new ArrayList<PageItem>();

		numItemsPerPage = page.getSize();
		totalPages = page.getTotalPages();
		actualPage = page.getNumber() + 1;

		int from, to;
		if (totalPages <= numItemsPerPage) {
			from = 1;
			to = totalPages;
		} else {
			if (actualPage <= numItemsPerPage / 2) {
				from = 1;
				to = numItemsPerPage;
			} else if (actualPage >= totalPages - numItemsPerPage / 2) {
				from = totalPages - numItemsPerPage + 1;
				to = numItemsPerPage;
			} else {
				from = actualPage - numItemsPerPage / 2;
				to = numItemsPerPage;
			}
		}

		for (int i = 0; i < to; i++) {
			pages.add(new PageItem(from + i, actualPage == from + i));
		}
	}

	public String getUrl() {
		return url;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getActualPage() {
		return actualPage;
	}

	public List<PageItem> getPages() {
		return pages;
	}

	public boolean isFirst() {
		return page.isFirst();
	}

	public boolean isLast() {
		return page.isLast();
	}

	public boolean isHasNext() {
		return page.hasNext();
	}

	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
