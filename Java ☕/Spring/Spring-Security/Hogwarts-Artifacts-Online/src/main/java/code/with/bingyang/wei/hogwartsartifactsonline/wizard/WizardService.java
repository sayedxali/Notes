package code.with.bingyang.wei.hogwartsartifactsonline.wizard;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.ArtifactRepository;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WizardService {

    private static final String ARTIFACT_NOT_FOUND = "artifact";
    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    private static final String WIZARD_NOT_FOUND = "wizard";

    public Wizard findById(int wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException(WIZARD_NOT_FOUND, wizardId));
    }


    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }


    public Wizard save(Wizard newWizard) {
        return this.wizardRepository.save(newWizard);
    }


    public Wizard update(int wizardId, Wizard wizard) {
        return this.wizardRepository.findById(wizardId).map(
                foundWizard -> {
                    foundWizard.setName(wizard.getName());
                    foundWizard.setArtifacts(wizard.getArtifacts());
                    return this.wizardRepository.save(foundWizard);
                }
        ).orElseThrow(() -> new ObjectNotFoundException(WIZARD_NOT_FOUND, wizardId));
    }


    public void delete(int wizardId) {
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException(WIZARD_NOT_FOUND, wizardId));

        // before deleting a wizard, we'll unassign the artifacts of that wizard since we don't want to delete the artifacts even if the wizard is gone
        wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardToBeDeleted.getId());
    }


    public void assignArtifact(Integer wizardId, String artifactId) {
        // Find this artifact by ID from DB.
        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException(ARTIFACT_NOT_FOUND, artifactId));

        // Find this wizard by ID from DB.
        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException(WIZARD_NOT_FOUND, wizardId));

        // Artifact assignment.
        // We need to see if the artifact is already owned by some wizard.
        if (artifactToBeAssigned.getOwner() != null)
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        wizard.addArtifact(artifactToBeAssigned);
    }

}
