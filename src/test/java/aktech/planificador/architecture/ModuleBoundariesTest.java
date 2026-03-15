package aktech.planificador.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class ModuleBoundariesTest {

    private static final Path MODULES_ROOT = Paths.get("src/main/java/aktech/planificador/modules");
    private static final Pattern MODULE_IMPORT_PATTERN = Pattern
            .compile("^import\\s+aktech\\.planificador\\.modules\\.([a-zA-Z0-9_]+)\\..*;");

    private static final List<String> FORBIDDEN_LEGACY_IMPORT_PREFIXES = List.of(
            "import aktech.planificador.Controller.",
            "import aktech.planificador.Service.",
            "import aktech.planificador.Repository.",
            "import aktech.planificador.Model.",
            "import aktech.planificador.Dto.",
            "import aktech.planificador.DTO.",
            "import aktech.planificador.security.",
            "import aktech.planificador.Exception.");

    @Test
    void modulesShouldNotImportOtherModulesDirectly() throws IOException {
        List<String> violations = new ArrayList<>();

        forEachModuleJavaFile((file, moduleName, line) -> {
            Matcher matcher = MODULE_IMPORT_PATTERN.matcher(line.trim());
            if (!matcher.matches()) {
                return;
            }

            String importedModuleName = matcher.group(1);
            if (!moduleName.equals(importedModuleName)) {
                violations.add(formatViolation(file,
                        "import directo entre modulos: " + moduleName + " -> " + importedModuleName));
            }
        });

        assertTrue(violations.isEmpty(),
                "Se detectaron imports directos entre modulos. Usar shared/api o shared/event:\n"
                        + String.join("\n", violations));
    }

    @Test
    void modulesShouldNotImportLegacyLayers() throws IOException {
        List<String> violations = new ArrayList<>();

        forEachModuleJavaFile((file, moduleName, line) -> {
            String trimmedLine = line.trim();
            for (String forbiddenPrefix : FORBIDDEN_LEGACY_IMPORT_PREFIXES) {
                if (trimmedLine.startsWith(forbiddenPrefix)) {
                    violations.add(formatViolation(file,
                            "import a capa legacy no permitido: " + trimmedLine));
                    break;
                }
            }
        });

        assertTrue(violations.isEmpty(),
                "Se detectaron imports de modulo hacia capas legacy. Mantener codigo nuevo aislado:\n"
                        + String.join("\n", violations));
    }

    private void forEachModuleJavaFile(LineInspector inspector) throws IOException {
        if (!Files.exists(MODULES_ROOT)) {
            return;
        }

        try (Stream<Path> pathStream = Files.walk(MODULES_ROOT)) {
            pathStream
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
                    .forEach(path -> inspectFile(path, inspector));
        }
    }

    private void inspectFile(Path file, LineInspector inspector) {
        String moduleName = resolveModuleName(file);
        if (moduleName == null) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                inspector.inspect(file, moduleName, line);
            }
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo leer archivo para regla de arquitectura: " + file, ex);
        }
    }

    private String resolveModuleName(Path file) {
        Path relativePath = MODULES_ROOT.relativize(file);
        if (relativePath.getNameCount() < 2) {
            return null;
        }
        return relativePath.getName(0).toString();
    }

    private String formatViolation(Path file, String reason) {
        return file.toString().replace('\\', '/') + " -> " + reason;
    }

    @FunctionalInterface
    private interface LineInspector {
        void inspect(Path file, String moduleName, String line);
    }
}
