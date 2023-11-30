package code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HogwartsUser implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotEmpty(message = "username is required.")
    private String username;

    @NotEmpty(message = "password is required.")
    private String password;

    private boolean enabled;

    @NotEmpty(message = "roles are required.")
    private String roles;

}
