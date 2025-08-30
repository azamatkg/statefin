package kg.infosystems.statefin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}