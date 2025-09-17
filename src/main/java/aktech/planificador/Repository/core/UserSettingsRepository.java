package aktech.planificador.Repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.UserSettings;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
    UserSettings findByUsuarioId(Integer usuarioId);
}
