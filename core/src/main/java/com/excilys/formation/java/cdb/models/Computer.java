package com.excilys.formation.java.cdb.models;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "computer")
public class Computer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idComputer;
    private String name;

    @Column(name = "introduced")
    private LocalDate introducedDate;

    @Column(name = "discontinued")
    private LocalDate discontinuedDate;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Long getIdComputer() {
        return idComputer;
    }

    public void setIdComputer(Long idComputer) {
        this.idComputer = idComputer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIntroducedDate() {
        return introducedDate;
    }

    public void setIntroducedDate(LocalDate introducedDate) {
        this.introducedDate = introducedDate;
    }

    public LocalDate getDiscontinuedDate() {
        return discontinuedDate;
    }

    public void setDiscontinuedDate(LocalDate discontinuedDate) {
        this.discontinuedDate = discontinuedDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        String companyString = "";
        if (company != null) {
            if (company.getIdCompany() != null && company.getIdCompany() != 0) {
                companyString = ", companyId:" + company.getIdCompany() + ", companyName:" + company.getName();
            }
        }
        return "Computer " + idComputer + ", name: " + name + ", introducedDate:" + introducedDate
                + ", discontinuedDate:" + discontinuedDate + companyString;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result + ((discontinuedDate == null) ? 0 : discontinuedDate.hashCode());
        result = prime * result + ((introducedDate == null) ? 0 : introducedDate.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Computer other = (Computer) obj;
        if (company == null) {
            if (other.company != null) {
                return false;
            }
        } else if (!company.equals(other.company)) {
            return false;
        }
        if (discontinuedDate == null) {
            if (other.discontinuedDate != null) {
                return false;
            }
        } else if (!discontinuedDate.equals(other.discontinuedDate)) {
            return false;
        }
        if (introducedDate == null) {
            if (other.introducedDate != null) {
                return false;
            }
        } else if (!introducedDate.equals(other.introducedDate)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public static class Builder {

        private Long idComputer;
        private String name;
        private LocalDate introducedDate;
        private LocalDate discontinuedDate;
        private Company company;

        public Builder setIdComputer(Long idComputer) {
            this.idComputer = idComputer;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setIntroducedDate(LocalDate introducedDate) {
            this.introducedDate = introducedDate;
            return this;
        }

        public Builder setDiscontinuedDate(LocalDate discontinuedDate) {
            this.discontinuedDate = discontinuedDate;
            return this;
        }

        public Builder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public Computer build() {
            Computer computer = new Computer();
            computer.idComputer = this.idComputer;
            computer.name = this.name;
            computer.introducedDate = this.introducedDate;
            computer.discontinuedDate = this.discontinuedDate;
            computer.company = this.company;
            return computer;
        }
    }


}
