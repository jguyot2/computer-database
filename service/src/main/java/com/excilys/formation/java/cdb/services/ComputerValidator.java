package com.excilys.formation.java.cdb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.excilys.formation.java.cdb.models.Company;
import com.excilys.formation.java.cdb.models.Computer;

@Component
public class ComputerValidator {
    private static Logger logger = LoggerFactory.getLogger(ComputerValidator.class);

    public void validate(final Computer instance) throws InvalidComputerException {
        checkComputerValidity(instance);
    }

    private void checkCompany(final Computer c, final List<ComputerProblems> problems) {
        Company company = c.getCompany();
        if (company != null) {
            if (company.getIdCompany() <= 0) {
                problems.add(ComputerProblems.INVALID_COMPANY_ID);
            }
        }
    }

    private void checkComputerValidity(final Computer computer) throws InvalidComputerException {
        if (computer == null) {
            throw new InvalidComputerException(Arrays.asList(ComputerProblems.NULL_COMPUTER));
        }

        List<ComputerProblems> problems = getComputerInstanceProblems(computer);
        if (problems.size() > 0) {
            logger.debug("Détection d'une instance de Computer invalide : " + computer);
            throw new InvalidComputerException(problems);
        }
    }
 
    private void checkDates(final Computer computer, final List<ComputerProblems> problems) {
        if (computer.getIntroducedDate() != null) {
            if (computer.getIntroducedDate().getYear() < 1970 || computer.getIntroducedDate().getYear() > 2037) {
                problems.add(ComputerProblems.OUT_OF_RANGE_INTRO_DATE);
            }
            if (computer.getDiscontinuedDate() != null 
                    && computer.getIntroducedDate().compareTo(computer.getDiscontinuedDate()) > 0) {
                if (computer.getDiscontinuedDate().getYear() < 1970 || computer.getDiscontinuedDate().getYear() > 2037) {
                    problems.add(ComputerProblems.OUT_OF_RANGE_DISCO_DATE);
                }
                problems.add(ComputerProblems.INVALID_DISCONTINUATION_DATE);
            }
        } else if (computer.getDiscontinuedDate() != null) {
            problems.add(ComputerProblems.NULL_INTRO_WITH_NOT_NULL_DISCONTINUATION);
        }
    }

    /**
     * @param computer l'instance de Computer à tester.
     *
     * @return Une liste contenant la liste des problèmes sur l'instance de Computer passée en
     *         paramètre
     */
    private List<ComputerProblems> getComputerInstanceProblems( final Computer computer) {
        List<ComputerProblems> problems = new ArrayList<>(); 
        if (computer.getName() == null || computer.getName().trim().isEmpty()) {
            problems.add(ComputerProblems.INVALID_NAME);
        }
        checkDates(computer, problems);
        checkCompany(computer, problems);
        return problems;
    }
}
