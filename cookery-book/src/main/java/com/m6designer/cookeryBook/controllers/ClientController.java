package com.m6designer.cookeryBook.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.m6designer.cookeryBook.models.entity.Client;
import com.m6designer.cookeryBook.models.service.IClientService;
import com.m6designer.cookeryBook.models.service.IToolsService;
import com.m6designer.cookeryBook.models.service.IUploadFileService;
import com.m6designer.cookeryBook.util.paginator.PageRender;

@Controller
@RequestMapping("/user")
@SessionAttributes("client")
public class ClientController {

	/* private static Logger logger = LoggerFactory.getLogger(Client.class); */

	@Autowired
	private IClientService clientService;

	@Autowired
	private IUploadFileService uploadFileService;

	private final static String USERS = "users";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IToolsService toolsService;

	@Secured("ROLE_USER")
	// Adapt the OS
//	@GetMapping(value = "/Users/manolito/uploads/{filename:.+}") // Windows: "/uploads/{filename:.+}")
	@GetMapping(value = "/uploads/{filename:.+}") // Mac: "/Users/manolito/uploads/{filename:.+}")
	public ResponseEntity<Resource> viewPhoto(@PathVariable String filename) {

		Resource resource = null;
		try {
			resource = uploadFileService.load(filename, USERS);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(value = "/view/{id}")
	public String view(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale,
			Authentication authentication, HttpServletRequest request) {

		Long zero = (long) 0;

		if (id.equals(zero) || !request.isUserInRole("ROLE_ADMIN")) {
			Client userAuth = clientService.findByUsername(authentication.getName());

			// User extra info
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));

			model.addAttribute("client", userAuth);
			model.addAttribute("title",
					messageSource.getMessage("client.userDetails", null, locale) + userAuth.getFirstName());
			return "/user/view";
		}

		Client client = clientService.fetchByIdWithRecipes(id); // clientService.findOne(id);
		if (client == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("client.noClient", null, locale));
			return "redirect:/user/";
		}

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		model.addAttribute("client", client);
		model.addAttribute("title",
				messageSource.getMessage("client.userDetails", null, locale) + client.getFirstName());

		return "/user/view";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Locale locale,
			Authentication authentication) {

		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Client> clients = clientService.findAll(pageRequest);

		PageRender<Client> pageRender = new PageRender<Client>("/user/", clients);

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		model.addAttribute("title", messageSource.getMessage("client.list", null, locale));
		model.addAttribute("clients", clients);
		model.addAttribute("page", pageRender);

		return "/user/list";
	}

	@RequestMapping(value = "/form")
	public String add(Model model, Locale locale, Authentication authentication) {

		Client client = new Client();

		// User extra info
		if (toolsService.getSpringUser(authentication) != null) {
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));
		}

		model.addAttribute("client", client);
		model.addAttribute("title", messageSource.getMessage("client.userForm", null, locale));

		return "/user/form";
	}

	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@RequestMapping(value = "/form/{id}")
	public String edit(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale,
			Authentication authentication, HttpServletRequest request) {

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		if (!request.isUserInRole("ROLE_ADMIN")) {
			Client userAuth = clientService.findByUsername(authentication.getName());

			model.addAttribute("client", userAuth);
			model.addAttribute("title", messageSource.getMessage("client.editUser", null, locale));

			return "/user/form";
		}

		Client client = null;
		if (id > 0) {
			client = clientService.findOne(id);
			if (client == null) {
				flash.addFlashAttribute("error", messageSource.getMessage("client.userIdNoExist", null, locale));
				return "redirect:/";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("client.userIdNoZero", null, locale));
			return "redirect:/user/";
		}

		model.addAttribute("client", client);
		model.addAttribute("title", messageSource.getMessage("client.editUser", null, locale));

		return "/user/form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String save(@Valid Client client, BindingResult result, Model model,
			@RequestParam("file") MultipartFile photo, RedirectAttributes flash, SessionStatus status, Locale locale,
			Authentication authentication) {

		if (result.hasErrors()) {
			// User extra info
			if (toolsService.getSpringUser(authentication) != null) {
				model.addAttribute("springUser", toolsService.getSpringUser(authentication));
			}
			model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
			model.addAttribute("title", messageSource.getMessage("client.userForm", null, locale));
			return "/user/form";
		}

		// Check if is a new user, the password is required
		if (client.getId() == null) {
			if (client.getPassword().isEmpty() || client.getPassword().isBlank()) {
				model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
				model.addAttribute("errorPassword", messageSource.getMessage("client.passwordEmpty", null, locale));
				model.addAttribute("title", messageSource.getMessage("client.userForm", null, locale));
				return "/user/form";
			}
		}

		// Check if there is a registered client with the same email
		if (clientService.checkEmailNewUser(client)) {
			// User extra info
			if (toolsService.getSpringUser(authentication) != null) {
				model.addAttribute("springUser", toolsService.getSpringUser(authentication));
			}

			model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
			model.addAttribute("errorEmail", messageSource.getMessage("client.emailExists", null, locale));
			model.addAttribute("title", messageSource.getMessage("client.userForm", null, locale));
			return "/user/form";
		}

		if (!photo.isEmpty()) {
			if (client.getId() != null && client.getId() > 0 && client.getPhoto() != null
					&& client.getPhoto().length() > 0) {
				uploadFileService.delete(client.getPhoto(), USERS);
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(photo, USERS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info",
					messageSource.getMessage("client.uploaded", null, locale) + "'" + uniqueFilename + "'");

			client.setPhoto(uniqueFilename);
		}

		String mensajeFlash = (client.getId() != null) ? messageSource.getMessage("client.userEdited", null, locale)
				: messageSource.getMessage("client.userCreated", null, locale);
		flash.addFlashAttribute("success", mensajeFlash);

		clientService.save(client);
		status.setComplete();

		if (authentication == null) {
			return "redirect:/login";
		} else {
			// User extra info
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));
			return "redirect:/user/";
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale,
			Authentication authentication) {

		Client client = clientService.findOne(id);
		if (client == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("client.noClient", null, locale));

			return "redirect:/user/";
		}

		Client userAuth = clientService.findByUsername(authentication.getName());

		if (userAuth.getId().equals(client.getId())) {
			flash.addFlashAttribute("error", messageSource.getMessage("error.canNotDeleteUser", null, locale));

			return "redirect:/user/";
		}

//		if (uploadFileService.delete(client.getPhoto(), USERS)) {
//			flash.addFlashAttribute("info", messageSource.getMessage("client.alertPhoto1", null, locale)
//					+ client.getPhoto() + messageSource.getMessage("client.alertPhoto2", null, locale));
//		}

		uploadFileService.delete(client.getPhoto(), USERS);
		clientService.delete(id);
		flash.addFlashAttribute("success", messageSource.getMessage("client.userDeleted", null, locale));

		return "redirect:/user/";
	}
}
