package com.m6designer.cookeryBook.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "recipes")
public class Recipe implements Serializable {

	private static final long serialVersionUID = -1981104672230779345L;

	@Id
	private String id;

	@NotEmpty
	private String name;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;

	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	private Client client;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "rel_recipes_categories", joinColumns = @JoinColumn(name = "fk_recipe_id"), inverseJoinColumns = @JoinColumn(name = "fk_category_id"), uniqueConstraints = {
			@UniqueConstraint(columnNames = { "fk_recipe_id", "fk_category_id" }) })
	private List<Category> categories;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "fk_recipe_id")
	private List<Ingredient> ingredients;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "fk_recipe_id")
	private List<Step> steps;

	private String remark;

	private Integer time;

	private Integer commensals;

	private String videoLink;

	private String photo;

	public Recipe() {
		this.categories = new ArrayList<Category>();
		this.ingredients = new ArrayList<Ingredient>();
		this.steps = new ArrayList<Step>();
	}

	@PrePersist
	public void prePersist() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getCommensals() {
		return commensals;
	}

	public void setCommensals(Integer commensals) {
		this.commensals = commensals;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public void addCategory(Category category) {
		if (this.categories == null) {
			this.categories = new ArrayList<>();
		}

		this.categories.add(category);
	}

	public void addIngredientRecipe(Ingredient ingredient) {
		if (this.ingredients == null) {
			this.ingredients = new ArrayList<>();
		}

		this.ingredients.add(ingredient);
	}

	public void addStepRecipe(Step step) {
		if (this.steps == null) {
			this.steps = new ArrayList<>();
		}

		this.steps.add(step);
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
