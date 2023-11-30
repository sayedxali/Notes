package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.converter.ArtifactDTOToArtifactConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.converter.ArtifactToArtifactDTOConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.dto.ArtifactDTO;
import code.with.bingyang.wei.hogwartsartifactsonline.system.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDTOConverter artifactToArtifactDTOConverter;
    private final ArtifactDTOToArtifactConverter artifactDTOToArtifactConverter;

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId) {
        Artifact foundArtifact = this.artifactService.findById(artifactId);
        ArtifactDTO artifactDTO = this.artifactToArtifactDTOConverter.convert(foundArtifact);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Find One Success")
                .data(artifactDTO)
                .build();
    }


    @GetMapping
    public Result findAllArtifacts() {
        List<Artifact> foundArtifacts = this.artifactService.findAll();
        List<ArtifactDTO> artifactDTOS = foundArtifacts.stream().map(this.artifactToArtifactDTOConverter::convert).toList();
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Find All Success")
                .data(artifactDTOS)
                .build();
    }


    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDTO artifactDTO) {
        Artifact newArtifact = this.artifactDTOToArtifactConverter.convert(artifactDTO);
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        ArtifactDTO savedArtifactDTO = this.artifactToArtifactDTOConverter.convert(savedArtifact);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Add Success")
                .data(savedArtifactDTO)
                .build();
    }


    @PutMapping("/{artifactId}")
    public Result updateArtifact(
            @PathVariable String artifactId,
            @Valid @RequestBody ArtifactDTO artifactDTO
    ) {
        Artifact artifact = this.artifactDTOToArtifactConverter.convert(artifactDTO);
        Artifact updatedArtifact = this.artifactService.update(artifactId, artifact);
        ArtifactDTO updatedArtifactDTO = this.artifactToArtifactDTOConverter.convert(updatedArtifact);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Update Success")
                .data(updatedArtifactDTO)
                .build();
    }


    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId) {
        this.artifactService.delete(artifactId);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Delete Success")
                .build();
    }

}
