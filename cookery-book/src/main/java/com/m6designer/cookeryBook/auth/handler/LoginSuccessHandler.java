package com.m6designer.cookeryBook.auth.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import com.m6designer.cookeryBook.models.dao.IClientDao;
import com.m6designer.cookeryBook.models.entity.Client;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	private IClientDao clientDao;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();

		FlashMap flashMap = new FlashMap();

		Locale locale = localeResolver.resolveLocale(request);

		Client client = clientDao.findByEmail(authentication.getName());

		flashMap.put("success", String.format(messageSource.getMessage("login.hiStart", null, locale))
				+ client.getFirstName() + String.format(messageSource.getMessage("login.hiEnd", null, locale)));

		flashMapManager.saveOutputFlashMap(flashMap, request, response);

		if (authentication != null) {
			logger.info(String.format(messageSource.getMessage("login.userLoggedIn", null, locale))
					+ authentication.getName()
					+ String.format(messageSource.getMessage("login.userLoginEnd", null, locale)));
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
