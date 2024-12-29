package org.cswteams.ms3.control.assegnazioneTurni;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class SchedulazioneTest {
    // MethodSource to provide test cases
    static Stream<TestCase> provideTestCases() {
        return Stream.of(
                new TestCase("input1", "expectedOutput1"),
                new TestCase("input2", "expectedOutput2"),
                new TestCase("input3", "expectedOutput3")
        );
    }

    // Helper class for MethodSource test cases
    static class TestCase {
        private final String input;
        private final String expectedOutput;

        public TestCase(String input, String expectedOutput) {
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        public String getInput() {
            return input;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    @DisplayName("Test MyService with MethodSource")
    void testMyServiceWithMethodSource(TestCase testCase) {
        // Act
        String actualOutput = "";

        // Assert
        Assertions.assertEquals(actualOutput, testCase.getExpectedOutput());
    }
}
