package code.with.bingyang.wei.hogwartsartifactsonline.wizard;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wizard implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = {MERGE, PERSIST})
    private List<Artifact> artifacts = new ArrayList<>();

    public void addArtifact(Artifact artifact) {
        if (this.artifacts == null) {
            this.artifacts = new ArrayList<>();
        }
        artifact.setOwner(this);
        this.artifacts.add(artifact);
    }


    public Integer getNumberOfArtifacts() {
        if (this.artifacts == null)
            this.artifacts = new ArrayList<>();
        return this.getArtifacts().size();
    }

    public void removeAllArtifacts() {
        this.artifacts.forEach(artifact -> artifact.setOwner(null));
        this.artifacts = new ArrayList<>();
    }

    public void removeArtifact(Artifact artifactToBeAssigned) {
        // Remove artifact owner.
        artifactToBeAssigned.setOwner(null);
        this.artifacts.remove(artifactToBeAssigned);
    }

}
