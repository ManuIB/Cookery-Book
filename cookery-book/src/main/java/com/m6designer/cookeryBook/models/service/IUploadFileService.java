package com.m6designer.cookeryBook.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public Resource load(String filename, String type) throws MalformedURLException;

	public String copy(MultipartFile file, String type) throws IOException;

	public boolean delete(String filename, String type);

	public void deleteAll();

	public void init() throws IOException;
}
