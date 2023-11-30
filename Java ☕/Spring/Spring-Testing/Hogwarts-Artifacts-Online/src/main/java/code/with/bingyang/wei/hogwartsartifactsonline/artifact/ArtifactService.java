package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.utils.IdWorker;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    private static final String ARTIFACT_NOT_FOUND = "artifact";

    public Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException(ARTIFACT_NOT_FOUND, artifactId));
    }


    public List<Artifact> findAll() {
        return this.artifactRepository.findAll();
    }


    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }


    public Artifact update(String artifactId, Artifact artifact) {
        Artifact foundArtifact = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException(ARTIFACT_NOT_FOUND, artifactId));

        foundArtifact.setName(artifact.getName());
        foundArtifact.setDescription(artifact.getDescription());
        foundArtifact.setImageUrl(artifact.getImageUrl());

        return this.artifactRepository.save(foundArtifact);
    }


    public void delete(String artifactId) {
        Artifact artifact = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException(ARTIFACT_NOT_FOUND, artifactId));
        this.artifactRepository.deleteById(artifact.getId());
    }

}
