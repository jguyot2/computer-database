package com.excilys.formation.java.cdb.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.formation.java.cdb.models.Computer;
import com.excilys.formation.java.cdb.models.Page;
import com.excilys.formation.java.cdb.persistence.daos.CompanyDAO;
import com.excilys.formation.java.cdb.persistence.daos.ComputerDAO;

@Service
public class ComputerService {

	@Autowired
	private ComputerDAO computerDAO;

	@Autowired
	private CompanyDAO companyDAO;

	private static Logger logger = LoggerFactory.getLogger(ComputerService.class);

	@Autowired
	ComputerValidator validator;

	/**
	 * Private constructor.
	 */
	private ComputerService() {
	}

	public int getNumberComputers() {
		return computerDAO.getNumberComputers();
	}

	public List<Computer> getAll() {
		return computerDAO.getAll();
	}

	private boolean isOkayOrderInput(String input) {
		switch (input) {
		case "computerAsc":
		case "computerDesc":
		case "companyAsc":
		case "companyDesc":
		case "introducedAsc":
		case "introducedDesc":
		case "discontinuedAsc":
		case "discontinuedDesc":
			return true;
		default:
			return false;
		}
	}

	public List<Computer> getBySearchAndOrder(String name, String order, Page page) throws IllegalArgumentException {
		if (!isOkayOrderInput(order))
			throw new IllegalArgumentException(); // TODO : pê exception personnalisée
		return computerDAO.getListWithSearchAndOrder(name, order, page);
	}

	/**
	 * Return the list of computers on a given page.
	 *
	 * @param page
	 * @return list of computers
	 */
	public List<Computer> getAllByPage(Page page) {
		return computerDAO.getAllByPage(page);
	}

	/**
	 * Return the computer with the given id.
	 *
	 * @param id
	 * @return computer
	 */
	public Computer findById(Long id) {
		return computerDAO.findById(id).get();
	}

	public Optional<Computer> findByIdOpt(Long id) {
		return computerDAO.findById(id);
	}

	/**
	 * Return the list of computer containing the given name on the given page.
	 *
	 * @param name
	 * @param page
	 * @return list of computers
	 */
	public List<Computer> findByNameByPage(String name, Page page) {
		return computerDAO.findByNameByPage(name, page);
	}

	public int getNumberComputersByName(String name) {
		return computerDAO.getNumberComputersByName(name);
	}

	public void create(Computer computer) throws InvalidComputerException {
		validator.validate(computer);
		computerDAO.create(computer);
	}

	/**
	 * Check if a computer is allowed to be created.
	 *
	 * @param computer
	 * @return true if allowed false if not
	 */
	public boolean allowedToCreateOrEdit(Computer computer) {
		boolean allowed = true;
		if (computer.getName() == null || computer.getName().isEmpty()) {
			logger.error("computer name is required");
			allowed = false;
		} else if (computer.getDiscontinuedDate() != null) {
			if (computer.getIntroducedDate() == null) {
				allowed = false;
				logger.error("introduced date is null");
			} else if (computer.getDiscontinuedDate().isBefore(computer.getIntroducedDate())) {
				allowed = false;
				logger.error("discontinued is before intro");
			}
			if (computer.getCompany() != null) {
				if (!companyDAO.findById(computer.getCompany().getIdCompany()).isPresent()) {
					allowed = false;
					logger.error("company does not exist");
				}
			}
		}
		return allowed;
	}

	public void update(Computer computer) throws InvalidComputerException {
		validator.validate(computer);
		computerDAO.update(computer);
	}

	public void delete(Long id) {
		computerDAO.delete(id);
	}

	public boolean exist(Long id) {
		return computerDAO.findById(id).isPresent();
	}

	public List<Computer> orderBy(Page page, String parameter) {
		return computerDAO.orderBy(page, parameter);
	}

}
