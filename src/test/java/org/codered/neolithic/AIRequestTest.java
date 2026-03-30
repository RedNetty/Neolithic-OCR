package org.codered.neolithic;

import org.codered.neolithic.openai.AIRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AIRequest}.
 */
class AIRequestTest {

    @Test
    void constructor_storesInstructionsAndText() {
        AIRequest request = new AIRequest("Solve this problem", "2 + 2 = ?");
        assertEquals("Solve this problem", request.getInstructions());
        assertEquals("2 + 2 = ?", request.getConvertedText());
    }

    @Test
    void setInstructions_updatesValue() {
        AIRequest request = new AIRequest("Old instructions", "Some text");
        request.setInstructions("New instructions");
        assertEquals("New instructions", request.getInstructions());
    }

    @Test
    void setConvertedText_updatesValue() {
        AIRequest request = new AIRequest("Instructions", "Original text");
        request.setConvertedText("Updated text");
        assertEquals("Updated text", request.getConvertedText());
    }

    @Test
    void constructor_allowsNullValues() {
        // AIRequest must not throw on null parameters (callers may pass null defensively)
        assertDoesNotThrow(() -> {
            AIRequest request = new AIRequest(null, null);
            assertNull(request.getInstructions());
            assertNull(request.getConvertedText());
        });
    }

    @Test
    void constructor_allowsEmptyStrings() {
        AIRequest request = new AIRequest("", "");
        assertEquals("", request.getInstructions());
        assertEquals("", request.getConvertedText());
    }

    @Test
    void setters_allowNullOverride() {
        AIRequest request = new AIRequest("Some instructions", "Some text");
        request.setInstructions(null);
        request.setConvertedText(null);
        assertNull(request.getInstructions());
        assertNull(request.getConvertedText());
    }
}
