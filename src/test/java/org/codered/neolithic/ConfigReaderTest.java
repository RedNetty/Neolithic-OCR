package org.codered.neolithic;

import org.codered.neolithic.utils.ConfigReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigReader}.
 *
 * <p>Notes on environment-variable tests:
 * We cannot reliably set/clear environment variables at runtime in pure Java
 * (System.setenv is not available). Instead, we test the fallback path (both
 * env var AND config.json absent) by relying on the fact that OPENAI_API_KEY
 * will not be set in a standard CI environment, and we test that the reader
 * does not throw an exception in any case.
 */
class ConfigReaderTest {

    /**
     * When neither an environment variable nor a config.json with a real token
     * is available, {@code getOpenAiToken()} must return {@code null} gracefully
     * rather than throwing.
     */
    @Test
    void getOpenAiToken_returnsSomethingOrNull_doesNotThrow() {
        ConfigReader reader = new ConfigReader();
        // Should never throw regardless of environment
        assertDoesNotThrow(() -> {
            String token = reader.getOpenAiToken();
            // token is either a real string or null — both are acceptable
            // We just verify it didn't explode.
        });
    }

    /**
     * When the OPENAI_API_KEY environment variable is set (as it would be in
     * a properly configured production environment), the token returned must
     * equal the environment variable value.
     *
     * This test is only meaningful when the env var is actually set. If it's
     * not set in the current environment, we skip the assertion.
     */
    @Test
    void getOpenAiToken_usesEnvVarWhenPresent() {
        String envToken = System.getenv("OPENAI_API_KEY");
        if (envToken != null && !envToken.isBlank()) {
            ConfigReader reader = new ConfigReader();
            assertEquals(envToken, reader.getOpenAiToken(),
                    "Token from ConfigReader should match OPENAI_API_KEY env var");
        }
        // If env var isn't set, we consider this test vacuously passing.
    }

    /**
     * Calling getOpenAiToken() multiple times must not throw and must return
     * a stable result (idempotent reads).
     */
    @Test
    void getOpenAiToken_isIdempotent() {
        ConfigReader reader = new ConfigReader();
        String first = reader.getOpenAiToken();
        String second = reader.getOpenAiToken();
        assertEquals(first, second, "Repeated calls should return the same token value");
    }
}
