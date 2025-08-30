package kg.infosystems.statefin.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdateRequest {

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Boolean active;

}