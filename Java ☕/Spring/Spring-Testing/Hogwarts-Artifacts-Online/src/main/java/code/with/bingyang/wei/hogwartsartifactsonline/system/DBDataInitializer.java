package code.with.bingyang.wei.hogwartsartifactsonline.system;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.ArtifactRepository;
import code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.UserService;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.Wizard;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.WizardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBDataInitializer {

    private final ArtifactRepository artifactRepository;
    private final WizardRepository wizardRepository;
    private final UserService userService;

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Artifact artifact1 = Artifact.builder().id("12334545").name("The Sword Of Gryffindor").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
            Artifact artifact2 = Artifact.builder().id("45632112").name("Resurrection Stone").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
            Artifact artifact3 = Artifact.builder().id("28493232").name("The Sword Of Gryffindor 2").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
            Artifact artifact4 = Artifact.builder().id("31242342").name("The Sword Of Gryffindor 4").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
            Artifact artifact5 = Artifact.builder().id("98734923").name("The Sword Of Gryffindor 5").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
            Artifact artifact6 = Artifact.builder().id("21356465").name("The Sword Of Gryffindor 5").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();

            Wizard wizard1 = Wizard.builder().id(1).name("Albus Dumbledore").build();
            Wizard wizard2 = Wizard.builder().id(2).name("Harry Potter").build();
            Wizard wizard3 = Wizard.builder().id(3).name("Neville Longbottom").build();

            wizard1.addArtifact(artifact1);
            wizard1.addArtifact(artifact3);

            wizard2.addArtifact(artifact2);
            wizard2.addArtifact(artifact4);

            wizard3.addArtifact(artifact5);

            this.wizardRepository.save(wizard1);
            this.wizardRepository.save(wizard2);
            this.wizardRepository.save(wizard3);

            this.artifactRepository.save(artifact6);

            // Create some users.
            HogwartsUser u1 = new HogwartsUser();
            u1.setId(1);
            u1.setUsername("john");
            u1.setPassword("123456");
            u1.setEnabled(true);
            u1.setRoles("admin user");

            HogwartsUser u2 = new HogwartsUser();
            u2.setId(2);
            u2.setUsername("eric");
            u2.setPassword("654321");
            u2.setEnabled(true);
            u2.setRoles("user");

            HogwartsUser u3 = new HogwartsUser();
            u3.setId(3);
            u3.setUsername("tom");
            u3.setPassword("qwerty");
            u3.setEnabled(false);
            u3.setRoles("user");

            this.userService.save(u1); // because the logic to encode password is int userService.
            this.userService.save(u2); // because the logic to encode password is int userService.
            this.userService.save(u3); // because the logic to encode password is int userService.
        };
    }

}
