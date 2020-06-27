package io.jenkins.plugins.checks.api;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.checks.api.ChecksAnnotation.ChecksAnnotationBuilder;
import io.jenkins.plugins.checks.api.ChecksAnnotation.ChecksAnnotationLevel;
import io.jenkins.plugins.checks.api.ChecksOutput.ChecksOutputBuilder;

import static io.jenkins.plugins.checks.api.ChecksOutputAssert.*;
import static org.assertj.core.api.Assertions.*;

class ChecksOutputTest {
    private final static String TITLE = "Coverage Report";
    private final static String SUMMARY = "All code have been covered";

    @Test
    void shouldBuildCorrectlyWithOnlyRequiredFields() {
        final ChecksOutput checksOutput = new ChecksOutputBuilder(TITLE, SUMMARY).build();

        assertThat(checksOutput).hasTitle(TITLE)
                .hasSummary(SUMMARY)
                .hasNoChecksAnnotations()
                .hasNoChecksImages();
    }

    @Test
    void shouldBuildCorrectlyWithAllFields() {
        final String text = "#Markdown Supported Text";

        ChecksAnnotationBuilder builder = new ChecksAnnotationBuilder("src/main/java/1.java", 0, 10,
                ChecksAnnotationLevel.WARNING, "first annotation");
        final List<ChecksAnnotation> annotations =
                Arrays.asList(builder.withTitle("first").build(), builder.withTitle("second").build());
        final List<ChecksImage> images =
                Arrays.asList(new ChecksImage("image_1", "https://www.image_1.com"),
                        new ChecksImage("image_2", "https://www.image_2.com"));

        final ChecksOutput checksOutput = new ChecksOutputBuilder(TITLE, SUMMARY)
                .withText(text)
                .withAnnotations(annotations)
                .withImages(images)
                .build();

        assertThat(checksOutput).hasTitle(TITLE)
                .hasSummary(SUMMARY)
                .hasText(text);
        assertThat(checksOutput.getChecksAnnotations())
                .usingFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(annotations);
        assertThat(checksOutput.getChecksImages())
                .usingFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(images);

        // test copy constructor
        final ChecksOutput copied = new ChecksOutput(checksOutput);
        assertThat(copied).hasTitle(TITLE)
                .hasSummary(SUMMARY)
                .hasText(text);
        assertThat(copied.getChecksAnnotations())
                .usingFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(annotations);
        assertThat(copied.getChecksImages())
                .usingFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(images);
    }
}