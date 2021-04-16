package com.m6designer.cookeryBook.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	// private final Logger log = LoggerFactory.getLogger(getClass());

	// Directorio absoluto donde guardaremos las imagenes
//	private final static String UPLOADS_FOLDER = "/Users/manolito/uploads/"; // Mac
	private final static String UPLOADS_FOLDER = "/uploads/"; // Windows

	// Directorio absoluto donde guardaremos las imagenes de lo susuarios
//	private final static String UPLOADS_USER_FOLDER = "/Users/manolito/uploads/users/"; // Mac
	private final static String UPLOADS_USER_FOLDER = "/uploads/users/"; // Windows

	// Directorio absoluto donde guardaremos las imagenes de las recetas
//	private final static String UPLOADS_RECIPE_FOLDER = "/Users/manolito/uploads/recipes/"; // Mac
	private final static String UPLOADS_RECIPE_FOLDER = "/uploads/recipes/"; // Windows

	private final static String USERS = "users";
	private final static String RECIPES = "recipes";

	@Override
	public Resource load(String filename, String type) throws MalformedURLException {

		Path pathFoto = getPath(filename, type);
		Resource resource = null;
		resource = new UrlResource(pathFoto.toUri());
		if (!resource.exists() || !resource.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
		}

		return resource;
	}

	@Override
	public String copy(MultipartFile file, String type) throws IOException {

		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPath(uniqueFilename, type);
		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename, String type) {

		Path rootPath = getPath(filename, type);
		File file = rootPath.toFile();

		if (file.exists() && file.canRead()) {
			if (file.delete()) {
				return true;
			}
		}

		return false;
	}

	public Path getPath(String filename, String type) {

		if (type.equals(USERS)) {
			return Paths.get(UPLOADS_USER_FOLDER).resolve(filename).toAbsolutePath();
		} else if (type.equals(RECIPES)) {
			return Paths.get(UPLOADS_RECIPE_FOLDER).resolve(filename).toAbsolutePath();
		} else {
			return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
		}
	}

	@Override
	public void deleteAll() {

		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	@Override
	public void init() throws IOException {

		Files.createDirectories(Paths.get(UPLOADS_FOLDER));
		Files.createDirectories(Paths.get(UPLOADS_USER_FOLDER));
		Files.createDirectories(Paths.get(UPLOADS_RECIPE_FOLDER));
	}
}
