package com.excilys.formation.java.cdb.ui;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.excilys.formation.java.cdb.models.Company;
import com.excilys.formation.java.cdb.models.Computer;
import com.excilys.formation.java.cdb.models.Page;
import com.excilys.formation.java.cdb.services.CompanyService;
import com.excilys.formation.java.cdb.services.ComputerService;
import com.excilys.formation.java.cdb.services.InvalidComputerException;
import com.excilys.formation.java.cdb.spring.HibernateConfig;

public class UserInterface {

	ApplicationContext context = new AnnotationConfigApplicationContext(HibernateConfig.class);

	CompanyService companyService = context.getBean(CompanyService.class);

	ComputerService computerService = context.getBean(ComputerService.class);

	private static Scanner scanner = new Scanner(System.in);

	private static Logger logger = LoggerFactory.getLogger(UserInterface.class);

	public void start() {
		logger.debug("start the UI");

		boolean quit = false;

		while (!quit) {

			System.out.println("Computer Database \n");
			System.out.println("Available features :");

			System.out.println("1 - List computers");
			System.out.println("2 - List companies");
			System.out.println("3 - Show computer details");
			System.out.println("4 - Create a computer");
			System.out.println("5 - Update a computer");
			System.out.println("6 - Delete a computer");
			System.out.println("7 - Delete a company");
			System.out.println("8 - Quit");

			System.out.println("");

			System.out.println("Enter your choice: ");
			int featureChoice = scanner.nextInt();
			scanner.nextLine();

			switch (featureChoice) {
			case 1:
				showAllComputers();
				break;

			case 2:
				showAllCompanies();
				break;

			case 3:
				System.out.println("Enter the id of a computer: ");
				Long computerId = scanner.nextLong();

				if (computerService.exist(computerId)) {
					Computer computer = computerService.findById(computerId);
					System.out.println(computer.toString());
				} else {
					System.out.println("The computer with the id " + computerId + " doesn't exit.");
				}

				backToMenu();

				break;

			case 4:
				boolean askId = false;
				Computer computer = inputComputer(askId);

				if (computerService.allowedToCreateOrEdit(computer)) {
					try {
						computerService.create(computer);
					} catch (InvalidComputerException e) {
						e.printStackTrace();
					}
					System.out.println("Creation OK.");
				} else {
					System.out.println("Creation impossible.");
				}
				break;

			case 5:
				boolean needId = true;
				Computer computerUpd = inputComputer(needId);

				if (computerService.exist(computerUpd.getIdComputer())) {
					try {
						computerService.update(computerUpd);
					} catch (InvalidComputerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Update OK.");
				} else { 
					System.out.println("Update impossible.");
				}
				break;

			case 6:
				System.out.println("Enter the id of the computer to delete: ");
				Long idComputerToDelete = scanner.nextLong();
				boolean computerExist = computerService.exist(idComputerToDelete);

				while (!computerExist) {
					System.out.println("The id doesn't exist.");
					System.out.println("Enter the id of an existing computer : ");
					idComputerToDelete = scanner.nextLong();
				}
				scanner.nextLine();
				computerService.delete(idComputerToDelete);
				System.out.println("Delete OK.");
				break;
			case 7:
				Long idCompanyToDelete = (long) -1;
				boolean companyExist = false;
				while (!companyExist || idCompanyToDelete == -1) {
					System.out.println("Enter the id of the company to delete: ");
					try {
						idCompanyToDelete = scanner.nextLong();
						companyExist = companyService.exist(idCompanyToDelete);
						if (!companyExist) {
							System.out.println("The id doesn't exist.");
						}
					} catch (InputMismatchException e) {
						scanner.nextLine();
						System.out.println("invalid id");
					}
				}
				scanner.nextLine();
				companyService.delete(idCompanyToDelete);
				System.out.println("Delete OK.");
				break;
			case 8:
				quit = true;
				break;

			default:
				System.out.println("This feature doesn't exist.");
			}
		}

		scanner.close();
	}

	public Computer inputComputer(boolean needId) {
		Computer computer = new Computer();

		if (needId) {
			System.out.println("Enter the id of the computer: ");
			String id = scanner.nextLine();
			while (id.equals("")) {
				System.out.println("The id cannot be empty.");
				System.out.println("Enter the id of the computer : ");
				id = scanner.nextLine();
			}
			computer.setIdComputer(Long.valueOf(id));
		}

		System.out.println("Enter the name of the computer: ");
		String name = scanner.nextLine();
		while (name.equals("")) {
			System.out.println("The name cannot be empty.");
			System.out.println("Enter the name of the computer : ");
			name = scanner.nextLine();
		}
		computer.setName(name);

		System.out
				.println("Enter the introduced date of the computer in the format YYYY-MM-DD: (press <Enter> to skip)");
		String introduced = scanner.nextLine();

		if (!introduced.equals("")) {
			computer.setIntroducedDate(LocalDate.parse(introduced));
			System.out.println(
					"Enter the discontinued date of the computer in the format YYYY-MM-DD: (press <Enter> to skip)");
			String discontinued = scanner.nextLine();
			if (!discontinued.equals("")) {
				computer.setDiscontinuedDate(LocalDate.parse(discontinued));
			}
		}
		System.out.println("Enter the manufacturer company id:(press <Enter> to skip)");
		String companyId = scanner.nextLine();
		if (!companyId.equals("")) {
			computer.setCompany(new Company.Builder().setIdCompany(Long.valueOf(companyId)).build());
		}

		return computer;
	}

	public void showAllCompanies() {
		Page currentPage = new Page();
		boolean quit = false;
		List<Company> allCompanies = companyService.getAll();
		int numberOfCompanies = allCompanies.size();

		do {
			List<Company> allCompaniesbyPage = companyService.getAllByPage(currentPage);
			allCompaniesbyPage.forEach(cp -> System.out.println(cp.toString()));
			System.out.println(
					"Page " + currentPage.getCurrentPage() + "/" + currentPage.getTotalPages(numberOfCompanies));
			System.out.println("(Enter 'q' to quit, 'p' to go to the previous page, 'n' to go to the next page.)");

			String input = scanner.nextLine();

			switch (input.toLowerCase()) {
			case "p":
				if (currentPage.hasPreviousPage()) {
					currentPage.previousPage();
				}
				break;
			case "n":
				if (currentPage.hasNextPage(numberOfCompanies)) {
					currentPage.nextPage();
				}
				break;
			case "q":
				quit = true;
				break;
			}
		} while (!quit);
	}

	public void showAllComputers() {
		Page currentPage = new Page();
		boolean quit = false;
		int numberOfComputers = computerService.getNumberComputers();

		do {
			List<Computer> allComputersbyPage = computerService.getAllByPage(currentPage);
			allComputersbyPage.forEach(cp -> System.out.println(cp.toString()));
			System.out.println(
					"Page " + currentPage.getCurrentPage() + "/" + currentPage.getTotalPages(numberOfComputers));
			System.out.println("(Enter 'q' to quit, 'p' to go to the previous page, 'n' to go to the next page.)");

			String input = scanner.nextLine();

			switch (input.toLowerCase()) {
			case "p":
				if (currentPage.hasPreviousPage()) {
					currentPage.previousPage();
				}
				break;
			case "n":
				if (currentPage.hasNextPage(numberOfComputers)) {
					currentPage.nextPage();
				}
				break;
			case "q":
				quit = true;
				break;
			}
		} while (!quit);
	}

	public void backToMenu() {
		System.out.println("(Enter 'q' to quit this page and go to main page.)");
		boolean quit = false;
		do {
			String input = scanner.nextLine();
			if (input.toLowerCase().equals("q")) {
				quit = true;
			}
		} while (!quit);
	}
}