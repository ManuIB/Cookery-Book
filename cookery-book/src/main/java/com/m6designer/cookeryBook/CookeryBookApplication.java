package com.m6designer.cookeryBook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.m6designer.cookeryBook.models.service.IUploadFileService;

@SpringBootApplication
public class CookeryBookApplication implements CommandLineRunner {

	@Autowired
	IUploadFileService uploadFileService;

	public static void main(String[] args) {

		SpringApplication.run(CookeryBookApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Has to be done on the first boot, remove it once it is running!
		uploadFileService.deleteAll();
		uploadFileService.init();
	}
}
