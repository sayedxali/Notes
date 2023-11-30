package code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter;

import code.with.bingyang.wei.hogwartsartifactsonline.wizard.Wizard;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.dto.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDTOConverter implements Converter<Wizard, WizardDTO> {

    @Override
    public WizardDTO convert(Wizard source) {
        return new WizardDTO(
                source.getId(),
                source.getName(),
                source.getNumberOfArtifacts()
        );
    }

}
