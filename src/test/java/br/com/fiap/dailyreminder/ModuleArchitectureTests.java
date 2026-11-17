package br.com.fiap.dailyreminder;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
class ModuleArchitectureTests {
    @Test void moduleBoundariesRemainAcyclicAndControllersUseApplicationServices() {
        var classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages("br.com.fiap.dailyreminder");
        slices().matching("br.com.fiap.dailyreminder.modules.(*)..").should().beFreeOfCycles().check(classes);
        noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..application..", "org.springframework.web..").check(classes);
        noClasses().that().resideInAPackage("..controllers..").should().dependOnClassesThat().resideInAPackage("..repositories..").check(classes);
        noClasses().that().resideInAPackage("..modules.activities..").should().dependOnClassesThat().resideInAnyPackage("..modules.users..", "..modules.notes.infrastructure..", "..modules.notes.domain..").check(classes);
        noClasses().that().resideInAPackage("..modules.notes..").should().dependOnClassesThat().resideInAnyPackage("..modules.users..", "..modules.activities..").check(classes);
        noClasses().that().resideInAPackage("..modules.users..").should().dependOnClassesThat().resideInAnyPackage("..modules.notes..", "..modules.activities..").check(classes);
    }
}
