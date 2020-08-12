package com.excilys.formation.java.cdb.services;

import java.util.List;

/**
 * Exception lancée lors de la rencontre d'une instance de Computer invalide. Contient une liste de
 * ComputerInstanceProblems indiquant le ou les problèmes associées à une instance
 *
 * @author jguyot2
 *
 */
public class InvalidComputerException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Liste des problèmes associés à une instance de Computer donnée.
     */
    private List<ComputerProblems> problems;

    /**
     * @param problemList Les problèmes associés à une instance de Computer donnée
     */
    public InvalidComputerException(final List<ComputerProblems> problemList) {
        this.problems = problemList;
    }

    /**
     * @return La liste des problèmes d'une instance donnée.
     */
    public List<ComputerProblems> getProblems() {
        return this.problems;
    }
}
