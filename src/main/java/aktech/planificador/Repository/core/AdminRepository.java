package aktech.planificador.Repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // Métodos personalizados si es necesario
}
