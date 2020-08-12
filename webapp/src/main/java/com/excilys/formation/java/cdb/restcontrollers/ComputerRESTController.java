package com.excilys.formation.java.cdb.restcontrollers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.excilys.formation.java.cdb.dtos.ComputerDTO;
import com.excilys.formation.java.cdb.dtos.PageDTO;
import com.excilys.formation.java.cdb.mappers.ComputerMapper;
import com.excilys.formation.java.cdb.mappers.PageMapper;
import com.excilys.formation.java.cdb.models.Computer;
import com.excilys.formation.java.cdb.models.Page;
import com.excilys.formation.java.cdb.services.ComputerService;
import com.excilys.formation.java.cdb.services.InvalidComputerException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import exceptions.NotFoundInDatabaseException;
 
@RestController
@CrossOrigin
@RequestMapping("computers")
public class ComputerRESTController {

	@Autowired
	private ComputerService computerService;

	@GetMapping(value = { "", "/" }, produces = "application/json")
	public List<ComputerDTO> listComputers() {
		List<Computer> allComputers = computerService.getAll();
		return allComputers.stream().map(c -> ComputerMapper.toComputerDTO(c)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/page" }, produces = "application/json")
	public List<ComputerDTO> listComputersPage(@ModelAttribute PageDTO page) {
		Page p = page == null ? new Page() : PageMapper.toPage(page);
		List<Computer> allComputers = computerService.getAllByPage(p);
		return allComputers.stream().map(c -> ComputerMapper.toComputerDTO(c)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/number" }, produces = "application/json")
	public Integer numberComputers() {
		return computerService.getNumberComputers();
	}

	@GetMapping(value = "/search/{search}", produces = "application/json")
	public List<ComputerDTO> searchComputer(@PathVariable String search, PageDTO pageDTO) {
		List<Computer> allComputers = computerService.findByNameByPage(search, PageMapper.toPage(pageDTO));
		return allComputers.stream().map(c -> ComputerMapper.toComputerDTO(c)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/search/{search}/number" }, produces = "application/json")
	public Integer numberSearchedComputers(@PathVariable String search) {
		return computerService.getNumberComputersByName(search);
	}

	@GetMapping(value = "/searchOrder/{search}/{order}", produces = "application/json")
	public List<ComputerDTO> orderAndSearch(@PathVariable String search, @PathVariable String order, PageDTO page) {
		System.out.println(search);
		PageDTO p = page == null ? new PageDTO() : page;
		try {
			List<ComputerDTO> l = computerService.getBySearchAndOrder(search, order, PageMapper.toPage(p)).stream()
					.map(ComputerMapper::toComputerDTO).collect(Collectors.toList());
			System.out.println(l);
			return l;
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad sort parameter");
		}
	}

	@GetMapping("/orderBy/{orderBy}")
	public List<ComputerDTO> orderComputer(@PathVariable String orderBy, PageDTO pageDTO) {
		List<Computer> allComputers = computerService.orderBy(PageMapper.toPage(pageDTO), orderBy);
		return allComputers.stream().map(c -> ComputerMapper.toComputerDTO(c)).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ComputerDTO getComputer(@PathVariable Long id) {
		Optional<Computer> computerOpt = computerService.findByIdOpt(id);
		if (!computerOpt.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Computer is not found is the database");
		}
		return ComputerMapper.toComputerDTO(computerOpt.get());
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<JsonNode> deleteComputer(@PathVariable Long id) {
        ObjectMapper mapper = new ObjectMapper();

		try {
			computerService.delete(id);
            return ResponseEntity.ok(mapper.createObjectNode().put("ok", true));
		} catch (NotFoundInDatabaseException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapper.createObjectNode().put("error", "The Computer is not found is the database"));
		}
	}

	@PostMapping(value = { "", "/" }, produces = "application/json")
	public ResponseEntity<String> createComputer(@RequestBody ComputerDTO dto) {
		try {
			computerService.create(ComputerMapper.toComputer(dto));
			return ResponseEntity.ok("{ok : true}");
		} catch (InvalidComputerException e) {
			StringBuilder sb = new StringBuilder();
			e.getProblems().stream().forEach(p -> sb.append(p.getExplanation() + "\n"));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{error : \"" + sb.toString() + "\"}");
		}
	}

	@PutMapping(value = { "", "/" }, produces = "application/json")
	public ResponseEntity<String> updateComputer(@RequestBody ComputerDTO dto) {
		try {
			computerService.update(ComputerMapper.toComputer(dto));
			return ResponseEntity.ok("{ok : true}");
		} catch (InvalidComputerException e) {
			StringBuilder sb = new StringBuilder();
			e.getProblems().stream().forEach(p -> sb.append(p.getExplanation() + "\n"));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{error : \"" + sb.toString() + "\"}");
		} catch (NotFoundInDatabaseException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Computer is not found is the database");
		}
	} 
}
