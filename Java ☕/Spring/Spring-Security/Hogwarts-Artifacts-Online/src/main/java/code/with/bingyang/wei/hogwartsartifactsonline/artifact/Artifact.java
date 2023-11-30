package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.wizard.Wizard;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Artifact implements Serializable {

    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Wizard owner;

}
