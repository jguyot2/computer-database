package com.excilys.formation.java.cdb.persistence.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.formation.java.cdb.mappers.CompanyMapper;
import com.excilys.formation.java.cdb.models.Computer;
import com.excilys.formation.java.cdb.models.Page;

import exceptions.NotFoundInDatabaseException;

@Repository
public class ComputerDAO {

	private static final String SQL_SELECT_ALL = "FROM Computer computer";

	private static final String SQL_COUNT_ALL = "SELECT count(computer) from Computer computer";

	private static final String SQL_SELECT_WITH_ID = "FROM Computer computer WHERE computer.id = :id";

	private static final String SQL_UPDATE = "UPDATE Computer computer SET computer.name = :name, computer.introducedDate = :introduced, "
			+ "computer.discontinuedDate = :discontinued, company.id = :companyId WHERE computer.id = :computerId";

	private static final String SQL_DELETE = "DELETE Computer WHERE id = :id";

    private static final String SQL_SELECT_WITH_NAME = "SELECT computer FROM Computer computer LEFT JOIN computer.company  WHERE computer.name LIKE :search OR computer.company.name LIKE :search ";
    private static final String SQL_COUNT_WITH_NAME = "SELECT count(computer) FROM Computer computer LEFT JOIN computer.company  WHERE computer.name LIKE :search OR computer.company.name LIKE :search ";

	private static final String SQL_PAGE_ORDER_NAME = "SELECT computer FROM Computer computer LEFT JOIN computer.company as company WHERE computer.name LIKE :search OR computer.company.name LIKE :search ORDER BY %s ,computer.id";
	private static final String SQL_PAGE_ORDER = "SELECT computer FROM Computer computer LEFT JOIN computer.company as company ORDER BY %s ,computer.id";
	
	private SessionFactory sessionFactory;

	private static Logger logger = LoggerFactory.getLogger(CompanyMapper.class);

	private String getSqlOrderByClauseFromString(String order) {
		switch (order) { 
		case "computerAsc":
			return "computer.name ASC";
		case "computerDesc":
			return "computer.name DESC";
		case "companyAsc":
			return "company.name ASC";
		case "companyDesc":
			return "company.name DESC";
		case "introducedAsc":
			return "computer.introducedDate ASC";
		case "introducedDesc":
			return "computer.introducedDate DESC";
		case "discontinuedAsc":
			return "computer.discontinuedDate ASC";
		case "discontinuedDesc":
			return "computer.discontinuedDate DESC";
		default:
			throw new IllegalArgumentException();
		}
	}

	public List<Computer> orderBy(Page page, String orderBy) {
		List<Computer> computerList = new ArrayList<Computer>();

		try (Session session = sessionFactory.openSession()) {
			// TODO : mettre dans une chaine + format ?
			String requete = String.format(SQL_PAGE_ORDER, getSqlOrderByClauseFromString(orderBy));
			
			Query<Computer> query = session.createQuery(requete, Computer.class);
			query.setFirstResult(page.getPageFirstLine());
			query.setMaxResults(page.getMaxLine());
			computerList = query.list();
		} catch (HibernateException e) {
			logger.error("error when get all by page:", e);
		} catch (IllegalArgumentException e) {
			logger.warn("Error when ordering computers", e);
		}
		return computerList;
	}

	public List<Computer> getListWithSearchAndOrder(String name, String order, Page page) {
		List<Computer> computerList = new ArrayList<Computer>();
		if ( name == null || name.isEmpty())
			return this.orderBy(page, order);

		if (page.getCurrentPage() > 0) {
			try (Session session = sessionFactory.openSession()) {
				String orderByClause = getSqlOrderByClauseFromString(order);
				String request = String.format(SQL_PAGE_ORDER_NAME, orderByClause);
				Query<Computer> query = session.createQuery(request, Computer.class);
				query.setParameter("search", "%".concat(name.replace("%", "\\%")).concat("%"));
				query.setFirstResult(page.getPageFirstLine());
				query.setMaxResults(page.getMaxLine());
				computerList = query.list();
				return computerList;
			} catch (HibernateException e) {
				logger.error("error when get all by page:", e);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				logger.warn("Error when ordering computers", e);
			}

		}
		return computerList;
	}

	@Autowired
	public ComputerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public int getNumberComputers() {
		int count = -1;
		try (Session session = sessionFactory.openSession()) {
			count = session.createQuery(SQL_COUNT_ALL, Long.class).uniqueResult().intValue();
		} catch (HibernateException e) {
			logger.error("error when getting total number of computers: ", e);
		}
		return count;
	}

	public List<Computer> getAll() {
		List<Computer> computerList = new ArrayList<Computer>();
		try (Session session = sessionFactory.openSession()) {
			computerList = session.createQuery(SQL_SELECT_ALL, Computer.class).list();
		} catch (HibernateException e) {
			logger.error("error when getting all computers", e);
		}
		return computerList;
	}

	/**
	 * Get all the computers on a given page.
	 *
	 * @param page
	 * @return list of the computers
	 */
	public List<Computer> getAllByPage(Page page) {
		List<Computer> computerList = new ArrayList<Computer>();

		if (page == null) {
			logger.error("the page is null");
		} else if (page.getCurrentPage() > 0) {
			try (Session session = sessionFactory.openSession()) {
				Query<Computer> query = session.createQuery(SQL_SELECT_ALL, Computer.class);
				query.setFirstResult(page.getPageFirstLine());
				query.setMaxResults(page.getMaxLine());
				computerList = query.list();
			} catch (HibernateException e) {
				logger.error("error when get all by page:", e);
			}
		}
		return computerList;
	}

	public Optional<Computer> findById(Long id) {
		Optional<Computer> result = Optional.empty();
		if (id != null) {
			try (Session session = sessionFactory.openSession()) {
				Query<Computer> query = session.createQuery(SQL_SELECT_WITH_ID, Computer.class);
				query.setParameter("id", id);
				result = Optional.ofNullable(query.uniqueResult());
			} catch (HibernateException e) {
				logger.error("error when finding computer by id", e);
			}
		} else {
			logger.error("company id to find is null");
		}
		return result;
	}

	public List<Computer> findByNameByPage(String name, Page page) {
		if(name==null)
			return this.getAllByPage(page);
		List<Computer> computerList = new ArrayList<Computer>();
		if (page.getCurrentPage() > 0) {
			try (Session session = sessionFactory.openSession()) {
				Query<Computer> query = session.createQuery(SQL_SELECT_WITH_NAME, Computer.class);
				query.setParameter("search", "%".concat(name.replace("%", "\\%")).concat("%"));
				query.setFirstResult(page.getPageFirstLine());
				query.setMaxResults(page.getMaxLine());
				computerList = query.list();
			} catch (HibernateException e) {
				logger.error("error when get all by page:", e);
			}
		}
		return computerList;
	}

	public int getNumberComputersByName(String name) {
		int count = -1;
		try (Session session = sessionFactory.openSession()) {
			count = session.createQuery(SQL_COUNT_WITH_NAME, Long.class)
					.setParameter("search", "%".concat(name).concat("%")).uniqueResult().intValue();
		} catch (HibernateException e) {
			logger.error("error when getting total number of computers: ", e);
		}
		return count;
	}

	/**
	 * Add a computer in the database.
	 *
	 * @param computer
	 */
	public void create(Computer computer) {
		if (computer != null) {
			try (Session session = sessionFactory.openSession()) {
				session.save(computer);
			} catch (HibernateException e) {
				logger.error("sql error when creating a computer", e);
			}
		} else {
			logger.error("the computer is null");
		}
	}

	/**
	 * Update an existing Computer.
	 *
	 * @param computer
	 */
	public void update(Computer computer) {
		int nbRows = 0;
		if (computer != null) {
			try (Session session = sessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				nbRows = session.createQuery(SQL_UPDATE).setParameter("name", computer.getName())
						.setParameter("introduced", computer.getIntroducedDate())
						.setParameter("discontinued", computer.getDiscontinuedDate())
						.setParameter("companyId",
								computer.getCompany() == null ? null : computer.getCompany().getIdCompany())
						.setParameter("computerId", computer.getIdComputer()).executeUpdate();
				transaction.commit();
				if (nbRows != 1) {
					logger.error("%d rows affected when updating computer", nbRows);
					throw new NotFoundInDatabaseException("Computer not found.");
				}
			} catch (IllegalStateException | RollbackException | HibernateException e) {
				logger.error("sql error when creating a computer", e);
			}
		} else {
			logger.error("the computer is null");
		}
	}

	/**
	 * Delete an existing Computer.
	 *
	 * @param id
	 */
	public void delete(Long id) {
		int nbRows = 0;
		if (id != null) {
			try (Session session = sessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				nbRows = session.createQuery(SQL_DELETE).setParameter("id", id).executeUpdate();
				transaction.commit();
				if (nbRows != 1) {
					logger.error("%d rows affected when deleting computer", nbRows);
					throw new NotFoundInDatabaseException("Computer not  found.");
				}
			} catch (IllegalStateException | PersistenceException e) {
				logger.error("error when deleting computer", e);
			}
		} else {
			logger.error("the id of the computer to delete is null");
		}
	}

}
