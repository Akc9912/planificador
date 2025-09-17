package aktech.planificador.Repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.NormalUser;

@Repository
public interface NormalUserRepository extends JpaRepository<NormalUser, Integer> {
    // Métodos personalizados si es necesario

}
