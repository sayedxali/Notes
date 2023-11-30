package code.with.bingyang.wei.hogwartsartifactsonline.artifact.converter;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.dto.ArtifactDTO;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter.WizardToWizardDTOConverter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtifactToArtifactDTOConverter implements Converter<Artifact, ArtifactDTO> {

    private final WizardToWizardDTOConverter wizardToWizardDTOConverter;

    @Override
    public ArtifactDTO convert(Artifact source) {
        return new ArtifactDTO(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getImageUrl(),
                source.getOwner() != null
                        ? this.wizardToWizardDTOConverter.convert(source.getOwner())
                        : null
        );
    }

}
