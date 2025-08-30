package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    Optional<Permission> findByResourceAndAction(String resource, String action);
    
    boolean existsByName(String name);
    
    boolean existsByResourceAndAction(String resource, String action);
    
    Page<Permission> findByActiveTrue(Pageable pageable);
    
    List<Permission> findByResource(String resource);
    
    @Query("SELECT DISTINCT p.resource FROM Permission p WHERE p.active = true ORDER BY p.resource")
    List<String> findAllResources();
    
    @Query("SELECT DISTINCT p.action FROM Permission p WHERE p.active = true ORDER BY p.action")
    List<String> findAllActions();
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.active = true")
    List<Permission> findByResourceAndActiveTrue(@Param("resource") String resource);

}