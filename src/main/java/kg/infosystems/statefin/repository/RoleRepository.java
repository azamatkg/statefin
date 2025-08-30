package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
    
    Page<Role> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT r FROM Role r JOIN FETCH r.permissions WHERE r.id = :id AND r.active = true")
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);
    
    @Query("SELECT r FROM Role r JOIN FETCH r.permissions WHERE r.name = :name AND r.active = true")
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

}