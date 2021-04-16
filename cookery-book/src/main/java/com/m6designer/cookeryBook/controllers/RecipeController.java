package com.m6designer.cookeryBook.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.m6designer.cookeryBook.models.entity.Category;
import com.m6designer.cookeryBook.models.entity.Client;
import com.m6designer.cookeryBook.models.entity.Ingredient;
import com.m6designer.cookeryBook.models.entity.Measure;
import com.m6designer.cookeryBook.models.entity.Recipe;
import com.m6designer.cookeryBook.models.entity.Step;
import com.m6designer.cookeryBook.models.service.ICategoryService;
import com.m6designer.cookeryBook.models.service.IClientService;
import com.m6designer.cookeryBook.models.service.IMeasureService;
import com.m6designer.cookeryBook.models.service.IRecipeService;
import com.m6designer.cookeryBook.models.service.IToolsService;
import com.m6designer.cookeryBook.models.service.IUploadFileService;
import com.m6designer.cookeryBook.util.paginator.PageRender;

@Controller
@SessionAttributes("recipe")
public class RecipeController {

	@Autowired
	private IRecipeService recipeService;

	@Autowired
	private IClientService clientService;

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private IMeasureService measureService;

	@Autowired
	private IUploadFileService uploadFileService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IToolsService toolsService;

	private final static String RECIPES = "recipes";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Locale locale,
			Authentication authentication) {

		Pageable pageRequest = PageRequest.of(page, 6);
		Page<Recipe> recipes = recipeService.findAll(pageRequest);

		PageRender<Recipe> pageRender = new PageRender<Recipe>("/", recipes);

		// User extra info
		if (toolsService.getSpringUser(authentication) != null) {
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));
		}

		model.addAttribute("title", messageSource.getMessage("recipe.list", null, locale));
		model.addAttribute("recipes", recipes);
		model.addAttribute("page", pageRender);

		return "/recipe/list";
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable(value = "id") String id, Model model, RedirectAttributes flash, Locale locale,
			Authentication authentication) {

		Recipe recipe = clientService.fetchRecipeWithClientWithRecipeWithMeasuresWithIngredients(id); // clientService.findRecipeById(id);
		if (recipe == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("recipe.noRecipe", null, locale));

			return "redirect:/";
		}

		// User extra info
		if (toolsService.getSpringUser(authentication) != null) {
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));
			// model.addAttribute("springUserIsAdmin",
			// toolsService.getSpringUser(authentication).isAdmin());
		}

		String allCategories = "";

		if (recipe.getCategories().size() > 0) {
			for (Category category : recipe.getCategories()) {
				allCategories += category.getName() + ", ";
			}
			// Delete the last 2 characters ", "
			allCategories = allCategories.substring(0, allCategories.length() - 2);
		}
		model.addAttribute("allCategories", allCategories);
		model.addAttribute("title", recipe.getName());
		model.addAttribute("recipe", recipe);

		return "/recipe/view";
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(value = "/form")
	public String add(Model model, RedirectAttributes flash, Locale locale, Authentication authentication) {

		Client client = clientService.findByUsername(authentication.getName());

		Recipe recipe = new Recipe();
		recipe.setClient(client);

		List<Category> categories = categoryService.findAll();
		List<Measure> measures = measureService.findAll();

		Long[] ingredientId = new Long[0];
		Long[] select_measures = new Long[0];
		String[] ingredientNames = new String[0];
		Integer[] quantity = new Integer[0];

		// Ingredient data
		model.addAttribute("categories", categories);
		model.addAttribute("measures", measures);
		model.addAttribute("ingredientId", ingredientId);
		model.addAttribute("selectMeasures", select_measures);
		model.addAttribute("ingredientNames", ingredientNames);
		model.addAttribute("quantity", quantity);

		Long[] stepId = new Long[0];
		String[] stepDescription = new String[0];

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		// Step data
		model.addAttribute("stepId", stepId);
		model.addAttribute("stepDescription", stepDescription);

		// Ingredient data
		model.addAttribute("lastIdForIngredients", 0);
		model.addAttribute("lastIdForSteps", 0);

		model.addAttribute("recipe", recipe);
		model.addAttribute("title", messageSource.getMessage("client.newRecipe", null, locale));

		return "recipe/form";
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@RequestMapping(value = "/form/{id}")
	public String edit(@PathVariable(value = "id") String id, Model model, RedirectAttributes flash, Locale locale,
			Authentication authentication, HttpServletRequest request) {

		Recipe recipe = new Recipe();
		if (!id.isBlank() && id != null) {
			recipe = recipeService.findOne(id);
			if (recipe == null) {
				flash.addFlashAttribute("error", messageSource.getMessage("recipe.recipeIdNoExist", null, locale));
				return "redirect:/";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("recipe.recipeIdNoZero", null, locale));
			return "redirect:/";
		}

		if (recipe.getClient() != null && recipe.getClient().getEmail() != null) {
			Client client = clientService.findByUsername(recipe.getClient().getEmail());
			recipe.setClient(client);
		}

		long idIngLength = recipe.getIngredients().size();
		long idStepLength = recipe.getSteps().size();

		Long[] select_measures = new Long[(int) idIngLength];
		String[] ingredientNames = new String[(int) idIngLength];
		Integer[] quantity = new Integer[(int) idIngLength];
		String[] stepDescription = new String[(int) idStepLength];

		List<Long> newIngredientId = new ArrayList<Long>();
		// New ingredient id list
		for (Long idLong = 0L; idLong < idIngLength; idLong++) {
			newIngredientId.add(idLong);
			ingredientNames[idLong.intValue()] = recipe.getIngredients().get(idLong.intValue()).getName();
			quantity[idLong.intValue()] = recipe.getIngredients().get(idLong.intValue()).getQuantity();
			select_measures[idLong.intValue()] = recipe.getIngredients().get(idLong.intValue()).getMeasure().getId();
		}

		List<Long> newStepId = new ArrayList<Long>();
		// New step id list
		for (Long idLong = 0L; idLong < idStepLength; idLong++) {
			newStepId.add(idLong);
			stepDescription[idLong.intValue()] = recipe.getSteps().get(idLong.intValue()).getDescription();
		}

		List<Category> categories = categoryService.findAll();
		List<Measure> measures = measureService.findAll();

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		// Ingredient & Step data
		model.addAttribute("lastIdForIngredients", recipe.getIngredients().size());
		model.addAttribute("lastIdForSteps", recipe.getSteps().size());

		model.addAttribute("categories", categories);
		model.addAttribute("measures", measures);
		model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures, ingredientNames,
				quantity, newStepId, stepDescription, authentication);
		model.addAttribute("title", messageSource.getMessage("recipe.editRecipe", null, locale));

		return "recipe/form";
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String save(@Valid Recipe recipe, BindingResult result, Model model,
			@RequestParam(name = "ingredient_id[]", required = false) Long[] ingredientId,
			@RequestParam(name = "select_measures[]", required = false) Long[] select_measures,
			@RequestParam(name = "ingredientName", required = false) String[] ingredientNames,
			@RequestParam(name = "quantity[]", required = false) Integer[] quantity,
			@RequestParam(name = "step_id[]", required = false) Long[] stepId,
			@RequestParam(name = "stepDescription", required = false) String[] stepDescription,
			@RequestParam("file") MultipartFile photo, RedirectAttributes flash, SessionStatus status, Locale locale,
			Authentication authentication) {

		long idIngLength = (ingredientId == null || ingredientId.length == 0) ? 0 : ingredientId.length;
		long idStepLength = (stepId == null || stepId.length == 0) ? 0 : stepId.length;

		List<Long> newIngredientId = new ArrayList<Long>();
		// New ingredient id list
		for (Long id = 0L; id < idIngLength; id++) {
			newIngredientId.add(id);
		}

		List<Long> newStepId = new ArrayList<Long>();
		// New step id list
		for (Long id = 0L; id < idStepLength; id++) {
			newStepId.add(id);
		}

		if (result.hasErrors()) {

			model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
					ingredientNames, quantity, newStepId, stepDescription, authentication);

			// User extra info
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));

			model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
			model.addAttribute("title", messageSource.getMessage("client.newRecipe", null, locale));

			return "/recipe/form";
		}

		if (newIngredientId == null || newIngredientId.size() == 0) {

			model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
					ingredientNames, quantity, newStepId, stepDescription, authentication);

			model = beforeReturnErrors(recipe, model, authentication, locale, "recipe.errorAddIngredient");

			return "/recipe/form";
		}

		for (int i = 0; i < newIngredientId.size(); i++) {

			if (ingredientNames == null || ingredientNames.length == 0 || ingredientNames[i] == null
					|| ingredientNames[i].isBlank() || ingredientNames[i].isEmpty()) {

				model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
						ingredientNames, quantity, newStepId, stepDescription, authentication);

				model = beforeReturnErrors(recipe, model, authentication, locale, "recipe.errorAddIngredientName");

				return "/recipe/form";
			}

			if (quantity == null || quantity.length == 0 || quantity[i] == null) {

				model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
						ingredientNames, quantity, newStepId, stepDescription, authentication);

				model = beforeReturnErrors(recipe, model, authentication, locale, "recipe.errorAddIngredientQuantity");

				return "/recipe/form";
			}

			if (select_measures == null || select_measures.length == 0 || select_measures[i] == null) {

				model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
						ingredientNames, quantity, newStepId, stepDescription, authentication);

				model = beforeReturnErrors(recipe, model, authentication, locale, "recipe.errorAddMeasure");

				return "/recipe/form";
			}

			Measure measure = measureService.findOne(select_measures[i]);

			String ingredient_id = "";

			if (recipe.getId() == null) {
				String isoDatePattern = "yyyyMMddHHmmss";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
				String dateString = simpleDateFormat.format(new Date());
				ingredient_id = dateString + "_" + recipe.getClient().getId() + "_" + String.valueOf(i);
			} else {
				ingredient_id = recipe.getId() + "_" + recipe.getClient().getId() + "_" + String.valueOf(i);
			}

			Ingredient ingredient = new Ingredient();
			ingredient.setId(ingredient_id);
			ingredient.setName(ingredientNames[i]);
			ingredient.setQuantity(quantity[i]);
			ingredient.setMeasure(measure);
			// Today's date
			ingredient.setCreatedAt(new Date());
			ingredient.setUpdatedAt(new Date());

			recipe.addIngredientRecipe(ingredient);
		}

		if (newStepId == null || newStepId.size() == 0) {

			model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
					ingredientNames, quantity, newStepId, stepDescription, authentication);
			model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
			if (recipe.getId() == null) {
				model.addAttribute("title", messageSource.getMessage("recipe.createRecipe", null, locale));
			} else {
				model.addAttribute("title", messageSource.getMessage("recipe.editRecipe", null, locale));
			}
			model.addAttribute("errorStep", messageSource.getMessage("recipe.errorAddStep", null, locale));

			// User extra info
			model.addAttribute("springUser", toolsService.getSpringUser(authentication));

			return "/recipe/form";
		}

		for (int i = 0; i < newStepId.size(); i++) {

			if (stepDescription == null || stepDescription.length == 0 || stepDescription[i] == null
					|| stepDescription[i].isBlank() || stepDescription[i].isEmpty()) {

				model = afterErrors(recipe, model, idIngLength, idStepLength, newIngredientId, select_measures,
						ingredientNames, quantity, newStepId, stepDescription, authentication);
				model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
				if (recipe.getId() == null) {
					model.addAttribute("title", messageSource.getMessage("recipe.createRecipe", null, locale));
				} else {
					model.addAttribute("title", messageSource.getMessage("recipe.editRecipe", null, locale));
				}
				model.addAttribute("errorStep",
						messageSource.getMessage("recipe.errorAddStepDescription", null, locale));

				// User extra info
				model.addAttribute("springUser", toolsService.getSpringUser(authentication));

				return "/recipe/form";
			}

			Step step = new Step();
			step.setDescription(stepDescription[i]);
			// Today's date
			step.setCreatedAt(new Date());
			step.setUpdatedAt(new Date());

			recipe.addStepRecipe(step);
		}

		if (!photo.isEmpty()) {
			if (recipe.getId() != null && !recipe.getId().isBlank() && recipe.getPhoto() != null
					&& recipe.getPhoto().length() > 0) {
				uploadFileService.delete(recipe.getPhoto(), RECIPES);
			}

			String uniqueFilename = null;

			try {
				uniqueFilename = uploadFileService.copy(photo, RECIPES);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info",
					messageSource.getMessage("client.uploaded", null, locale) + "'" + uniqueFilename + "'");

			recipe.setPhoto(uniqueFilename);
		}

		// Today's date
		recipe.setCreatedAt(new Date());
		recipe.setUpdatedAt(new Date());

		if (recipe.getId() == null) {
			String isoDatePattern = "yyMMddHHmmss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
			String recipe_id = simpleDateFormat.format(new Date());
			recipe.setId(recipe_id);
		}
		clientService.saveRecipe(recipe);
		status.setComplete();
		flash.addFlashAttribute("success", messageSource.getMessage("recipe.recipeCreated", null, locale));

		return "redirect:/user/view/" + recipe.getClient().getId();
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping(value = "/add-measure/{term}", produces = { "application/json" })
	public @ResponseBody List<Measure> loadMeasures(@PathVariable String term) {

		return measureService.findByName(term);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable(value = "id") String id, RedirectAttributes flash, Locale locale,
			Authentication authentication, HttpServletRequest request) {

		Recipe recipe = recipeService.findOne(id);
		if (recipe == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("recipe.noRecipe", null, locale));

			return "redirect:/";
		}

		Client userAuth = clientService.findByUsername(authentication.getName());

		if (!request.isUserInRole("ROLE_ADMIN") && !userAuth.getId().equals(recipe.getClient().getId())) {
			flash.addFlashAttribute("error", messageSource.getMessage("error.canNotDeleteRecipe", null, locale));

			return "redirect:/";
		}

		clientService.deleteRecipe(id);
		flash.addFlashAttribute("success", messageSource.getMessage("client.recipeDeleted", null, locale));

		return "redirect:/";
	}

	private Model afterErrors(Recipe recipe, Model model, long idIngLength, long idStepLength,
			List<Long> newIngredientId, Long[] select_measures, String[] ingredientNames, Integer[] quantity,
			List<Long> newStepId, String[] stepDescription, Authentication authentication) {

		List<Category> categories = categoryService.findAll();
		List<Measure> measures = measureService.findAll();

		// Ids for ingredients and steps
		model.addAttribute("ingredientId", idIngLength);
		model.addAttribute("stepId", idStepLength);

		// Ingredient data
		model.addAttribute("categories", categories);
		model.addAttribute("measures", measures);
		model.addAttribute("ingredientId", newIngredientId);
		model.addAttribute("lastSelectMeasures", select_measures);
		model.addAttribute("ingredientNames", ingredientNames);
		model.addAttribute("quantity", quantity);

		// Step data
		model.addAttribute("stepId", newStepId);
		model.addAttribute("stepDescription", stepDescription);

		// Remove ingredients and steps
		recipe.setIngredients(new ArrayList<Ingredient>());
		recipe.setSteps(new ArrayList<Step>());

		// Recipe data
		model.addAttribute("recipe", recipe);

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		// Ingredient & Step data
		model.addAttribute("lastIdForIngredients", newIngredientId.size());
		model.addAttribute("lastIdForSteps", newStepId.size());

		return model;
	}

	private Model beforeReturnErrors(Recipe recipe, Model model, Authentication authentication, Locale locale,
			String errorMessageToSend) {

		model.addAttribute("error", messageSource.getMessage("error.formError", null, locale));
		if (recipe.getId() == null) {
			model.addAttribute("title", messageSource.getMessage("recipe.createRecipe", null, locale));
		} else {
			model.addAttribute("title", messageSource.getMessage("recipe.editRecipe", null, locale));
		}
		model.addAttribute("errorIngredient", messageSource.getMessage(errorMessageToSend, null, locale));

		// User extra info
		model.addAttribute("springUser", toolsService.getSpringUser(authentication));

		return model;
	}
}
