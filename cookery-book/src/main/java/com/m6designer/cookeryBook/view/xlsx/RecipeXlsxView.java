package com.m6designer.cookeryBook.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.m6designer.cookeryBook.models.entity.Recipe;

@Component("recipe/view.xlsx")
public class RecipeXlsxView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Recipe recipe = (Recipe) model.get("recipe");
		Sheet sheet = workbook.createSheet();

		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);

		cell.setCellValue("Client data");
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(recipe.getClient().getFirstName() + " " + recipe.getClient().getLastName());

		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(recipe.getClient().getEmail());

		sheet.createRow(3).createCell(0).setCellValue("Recipe data");
		sheet.createRow(4).createCell(0).setCellValue("Recipe: " + " " + recipe.getId());
		sheet.createRow(6).createCell(0).setCellValue("Date: " + " " + recipe.getCreatedAt());
	}

}
