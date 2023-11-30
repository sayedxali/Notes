package code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter;

import code.with.bingyang.wei.hogwartsartifactsonline.wizard.Wizard;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.dto.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDTOToWizardConverter implements Converter<WizardDTO, Wizard> {

    @Override
    public Wizard convert(WizardDTO source) {
        return Wizard.builder()
                .id(source.id())
                .name(source.name())
                .build();
    }

}
