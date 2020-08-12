package com.excilys.formation.java.cdb.restcontrollers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.excilys.formation.java.cdb.dtos.CompanyDTO;
import com.excilys.formation.java.cdb.mappers.CompanyMapper;
import com.excilys.formation.java.cdb.models.Company;
import com.excilys.formation.java.cdb.services.CompanyService;

import exceptions.NotFoundInDatabaseException;

@RestController
@CrossOrigin("*")
@RequestMapping("companies")
public class CompanyRESTController {
    
    @Autowired
    private CompanyService companyService;

    @GetMapping(value = {"", "/"})
    public List<CompanyDTO> listCompanies() {
        List<Company> allCompanies = companyService.getAll();
        return allCompanies.stream().map(c -> CompanyMapper.toCompanyDTO(c)).collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public CompanyDTO getCompany (@PathVariable Long id) {
        try {
            return CompanyMapper.toCompanyDTO(companyService.findById(id));
        } catch (NotFoundInDatabaseException e) {
            throw new ResponseStatusException (HttpStatus.NOT_FOUND, "The company is not found is the database");
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        try {
            companyService.delete(id);
        } catch (NotFoundInDatabaseException e) {
            throw new ResponseStatusException (HttpStatus.NOT_FOUND, "The Company is not found is the database");
        }
    }
   
}
