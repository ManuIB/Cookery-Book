package com.m6designer.cookeryBook.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Principal principal,
			RedirectAttributes flash, Locale locale) {

		if (principal != null) {
			flash.addFlashAttribute("info", messageSource.getMessage("client.userNotLoginAgain", null, locale));
			return "redirect:/";
		}

		if (error != null) {
			model.addAttribute("error", messageSource.getMessage("login.error1", null, locale));
		}

		if (logout != null) {
			model.addAttribute("success", messageSource.getMessage("login.successLogout", null, locale));
		}

		model.addAttribute("title", messageSource.getMessage("login.signIn", null, locale));

		return "login";
	}

//	@GetMapping("/logout")
//	public String logoutPage(HttpServletRequest request, HttpServletResponse response, Model model,
//			Authentication authentication, RedirectAttributes flash, Locale locale) {
//
//		if (authentication != null) {
//			new SecurityContextLogoutHandler().logout(request, response, authentication);
//		}
//		
//		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//		localeResolver.setDefaultLocale(new Locale("en", "US"));
//
//		model.addAttribute("success", messageSource.getMessage("login.successLogout", null, locale));
//		model.addAttribute("title", messageSource.getMessage("login.signIn", null, locale));
//		return "login";
//	}
}
