package com.excilys.formation.java.cdb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.excilys.formation.java.cdb.dtos.CompanyDTO;
import com.excilys.formation.java.cdb.dtos.ComputerDTO;
import com.excilys.formation.java.cdb.mappers.CompanyMapper;
import com.excilys.formation.java.cdb.mappers.ComputerMapper;
import com.excilys.formation.java.cdb.models.Company;
import com.excilys.formation.java.cdb.models.Computer;
import com.excilys.formation.java.cdb.services.CompanyService;
import com.excilys.formation.java.cdb.services.ComputerService;
import com.excilys.formation.java.cdb.services.InvalidComputerException;
import com.excilys.formation.java.cdb.validators.ComputerValidator;

@Controller
@RequestMapping("/AddComputer")
public class AddComputerController {

    private static Logger logger = LoggerFactory.getLogger(AddComputerController.class);

    private CompanyService companyService;

    private ComputerService computerService;
 
    public AddComputerController (CompanyService companyService, ComputerService computerService) {
        this.companyService = companyService;
        this.computerService = computerService;
    }

    @GetMapping
    public ModelAndView showInfo() {
        List<Company> allCompanies = companyService.getAll();
        List<CompanyDTO> allCompanyDTOs = new ArrayList<CompanyDTO>();
        allCompanyDTOs = allCompanies.stream().map(c -> CompanyMapper.toCompanyDTO(c)).collect(Collectors.toList());
        ModelAndView modelAndView = new ModelAndView("addComputer");
        modelAndView.addObject("companies", allCompanyDTOs);
        return modelAndView;
    }

    @PostMapping
    public ModelAndView addComputer(ComputerDTO computerDTO, CompanyDTO companyDTO) throws InvalidComputerException {
        if (companyDTO.getIdCompany() != null && !companyDTO.getIdCompany().equals("0")){
            computerDTO.setCompanyDTO(companyDTO);
        }
        if (ComputerValidator.dateFormatValidator(computerDTO.getIntroducedDate())
                && ComputerValidator.dateFormatValidator(computerDTO.getDiscontinuedDate())) {
            Computer computer = ComputerMapper.toComputer(computerDTO);
            if (computerService.allowedToCreateOrEdit(computer)) {
                computerService.create(computer);
                logger.info("computer creation ok");
            } else {
                logger.error("computer creation not allowed");
            }
        }
        return new ModelAndView("redirect:/ListComputers");
    }
}
