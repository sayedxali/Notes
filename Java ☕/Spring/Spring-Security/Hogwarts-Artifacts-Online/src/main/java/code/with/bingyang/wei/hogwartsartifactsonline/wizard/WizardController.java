package code.with.bingyang.wei.hogwartsartifactsonline.wizard;

import code.with.bingyang.wei.hogwartsartifactsonline.system.Result;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter.WizardDTOToWizardConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter.WizardToWizardDTOConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.dto.WizardDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    private final WizardService wizardService;
    private final WizardToWizardDTOConverter wizardToWizardDTOConverter;
    private final WizardDTOToWizardConverter wizardDTOToWizardConverter;

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable int wizardId) {
        Wizard foundWizard = this.wizardService.findById(wizardId);
        WizardDTO wizardDTO = this.wizardToWizardDTOConverter.convert(foundWizard);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Find One Success")
                .data(wizardDTO)
                .build();
    }


    @GetMapping
    public Result findAllWizards() {
        List<Wizard> foundWizards = this.wizardService.findAll();
        List<WizardDTO> wizardDTOS = foundWizards
                .stream()
                .map(this.wizardToWizardDTOConverter::convert)
                .toList();
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Find All Success")
                .data(wizardDTOS)
                .build();
    }


    @PostMapping
    public Result addWizard(@Valid @RequestBody WizardDTO wizardDTO) {
        Wizard newWizard = this.wizardDTOToWizardConverter.convert(wizardDTO);
        Wizard savedWizard = this.wizardService.save(newWizard);
        WizardDTO savedWizardDTO = this.wizardToWizardDTOConverter.convert(savedWizard);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Add Success")
                .data(savedWizardDTO)
                .build();
    }


    @PutMapping("/{wizardId}")
    public Result updateWizard(
            @PathVariable int wizardId,
            @Valid @RequestBody WizardDTO wizardDTO
    ) {
        Wizard wizard = this.wizardDTOToWizardConverter.convert(wizardDTO);
        Wizard updatedWizard = this.wizardService.update(wizardId, wizard);
        WizardDTO updatedWizardDTO = this.wizardToWizardDTOConverter.convert(updatedWizard);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Update Success")
                .data(updatedWizardDTO)
                .build();
    }


    @DeleteMapping("/{wizardId}")
    public Result deleteWizard(@PathVariable int wizardId) {
        this.wizardService.delete(wizardId);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Delete Success")
                .data(null)
                .build();
    }


    @PutMapping("/{wizardId}/artifacts/{artifactId}")
    public Result assignArtifact(
            @PathVariable String artifactId,
            @PathVariable Integer wizardId
    ) {
        this.wizardService.assignArtifact(wizardId, artifactId);
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("Artifact Assignment Success")
                .data(null)
                .build();
    }

}
