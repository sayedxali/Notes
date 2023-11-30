package code.with.bingyang.wei.hogwartsartifactsonline.artifact.converter;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.dto.ArtifactDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDTOToArtifactConverter implements Converter<ArtifactDTO, Artifact> {

    @Override
    public Artifact convert(ArtifactDTO source) {
        return Artifact.builder()
                .id(source.id())
                .name(source.name())
                .description(source.description())
                .imageUrl(source.imageUrl())
                .build();
    }

}
