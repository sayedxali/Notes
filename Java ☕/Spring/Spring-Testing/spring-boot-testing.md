- Unit tests : [source code1](./Hogwarts-Artifacts-Online/src/test/java/code/with/bingyang/wei/hogwartsartifactsonline)

---
- Integration
  test1 : [source code1](./Hogwarts-Artifacts-Online/src/test/java/code/with/bingyang/wei/hogwartsartifactsonline/artifact/ArtifactControllerIntegrationTest.java)
- Integration
  test2 : [source code1](./Hogwarts-Artifacts-Online/src/test/java/code/with/bingyang/wei/hogwartsartifactsonline/hogwartsuser/UserControllerIntegrationTest.java)
- Integration
  test3 : [source code1](./Hogwarts-Artifacts-Online/src/test/java/code/with/bingyang/wei/hogwartsartifactsonline/wizard/WizardControllerIntegrationTest.java)

> 💡Security testing is in the integration layer.

---
___

- Unit tests2 : [source code2](./DevVault/src/test/java/com/dev/vault/)


Jacoco Code Coverage test maven plugin :
```xml
<!--jacoco code coverage test report plugin-->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>

    <executions>

        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>

        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>


        <!--<editor-fold desc="this is for CI/CD pipeline -> use clean and verify to run">-->
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.9</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
        <!--</editor-fold>-->
    </executions>

</plugin>
```
