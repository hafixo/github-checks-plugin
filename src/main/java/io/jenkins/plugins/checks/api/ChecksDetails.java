package io.jenkins.plugins.checks.api;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class ChecksDetails {
    private final String name;
    private final ChecksStatus status;
    private final String detailsURL;
    private final LocalDateTime startedAt;
    private final ChecksConclusion conclusion;
    private final LocalDateTime completedAt;
    private final ChecksOutput output;
    private final List<ChecksAction> actions;

    private ChecksDetails(final String name, final ChecksStatus status, final String detailsURL,
            final LocalDateTime startedAt, final ChecksConclusion conclusion, final LocalDateTime completedAt,
            final ChecksOutput output, final List<ChecksAction> actions) {
        this.name = name;
        this.status = status;
        this.detailsURL = detailsURL;
        this.startedAt = startedAt;
        this.conclusion = conclusion;
        this.completedAt = completedAt;
        this.output = output;
        this.actions = actions;
    }

    /**
     * Returns the name of a check.
     *
     * @return the unique name of a check
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the status of a check.
     *
     * @return {@link ChecksStatus}, one of {@code QUEUED}, {@code IN_PROGRESS}, {@code COMPLETED}
     */
    public ChecksStatus getStatus() {
        return status;
    }

    /**
     * Returns the url of a site with full details of a check.
     *
     * @return the url of a site or null
     */
    @Nullable
    public String getDetailsURL() {
        return detailsURL;
    }

    /**
     * Returns the time that the check started.
     *
     * @return the start time of a check
     */
    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    /**
     * Returns the conclusion of a check.
     *
     * @return the conclusion of a check
     */
    public ChecksConclusion getConclusion() {
        return conclusion;
    }

    /**
     * Returns the time that the check completed.
     *
     * @return the complete time of a check
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns the {@link ChecksOutput} of a check
     *
     * @return An {@link ChecksOutput} of a check or null
     */
    @Nullable
    public ChecksOutput getOutput() {
        return output;
    }

    /**
     * Returns the {@link ChecksAction}s of a check
     *
     * @return An immutable list of {@link ChecksAction}s of a check
     */
    public List<ChecksAction> getActions() {
        return actions;
    }

    /**
     * Builder for {@link ChecksDetails}.
     */
    public static class ChecksDetailsBuilder {
        private final String name;
        private final ChecksStatus status;
        private String detailsURL;
        private LocalDateTime startedAt;
        private ChecksConclusion conclusion;
        private LocalDateTime completedAt;
        private ChecksOutput output;
        private List<ChecksAction> actions;

        /**
         * Construct a builder with the given name and status.
         *
         * <p>
         *     The name will be the same as the check run's name shown on GitHub UI and GitHub uses this name to
         *     identify a check run, so make sure this name is unique, e.g. "Coverage".
         * <p>
         *
         * @param name
         *         the name of the check run
         * @param status
         *         the status which the check run will be set
         *
         * @throws IllegalArgumentException if the name is blank or the status is null
         */
        public ChecksDetailsBuilder(final String name, final ChecksStatus status) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("check name should not be blank");
            }

            this.name = name;
            this.status = requireNonNull(status);

            conclusion = ChecksConclusion.NONE;
            actions = Collections.emptyList();
        }

        /**
         * Set the url of a site with full details of a check. Note that the url must use http or https scheme.
         *
         * <p>
         *     If the details url is not set, the Jenkins build url will be used,
         *     e.g. https://ci.jenkins.io/job/Core/job/jenkins/job/master/2000/.
         * </p>
         *
         * @param detailsURL
         *         the url using http or https scheme
         * @return this builder
         * @throws NullPointerException if the {@code detailsURL} is null
         * @throws IllegalArgumentException if the {@code detailsURL} doesn't use http or https scheme
         */
        public ChecksDetailsBuilder withDetailsURL(final String detailsURL) {
            if (!StringUtils.equalsAny(URI.create(detailsURL).getScheme(), "http", "https")) {
                throw new IllegalArgumentException("details URL must use http or https scheme: " + detailsURL);
            }
            this.detailsURL = detailsURL;
            return this;
        }

        /**
         * Set the time when a check starts.
         *
         * <p>
         *     If this attribute is not set and {@code conclusion} is not set as well, the time when
         *     {@link ChecksDetailsBuilder#build()} is called will be used.
         * </p>
         *
         * @param startedAt
         *         the time when a check starts
         * @return this builder
         * @throws NullPointerException if the {@code startAt} is null
         */
        public ChecksDetailsBuilder withStartedAt(final LocalDateTime startedAt) {
            this.startedAt = requireNonNull(startedAt);
            return this;
        }

        /**
         * Set the conclusion of a check.
         *
         * <p>
         *     The conclusion should only be set when the {@code status} was set {@link ChecksStatus#COMPLETED}
         *     when constructing this builder.
         * </p>
         *
         * @param conclusion
         *         the conclusion
         * @return this builder
         * @throws NullPointerException if the {@code conclusion} is null
         * @throws IllegalArgumentException if the {@code status} is not {@link ChecksStatus#COMPLETED}
         */
        public ChecksDetailsBuilder withConclusion(final ChecksConclusion conclusion) {
            if (status != ChecksStatus.COMPLETED) {
                throw new IllegalArgumentException("status must be completed when setting conclusion");
            }
            this.conclusion = requireNonNull(conclusion);
            return this;
        }

        /**
         * Set the time when a check completes.
         *
         * <p>
         *     If this attribute is not set while {@code conclusion} is set, the time when
         *     {@link ChecksDetailsBuilder#build()} is called will be used.
         * </p>
         *
         * @param completedAt
         *         the time when a check completes
         * @return this builder
         * @throws NullPointerException if the {@code completedAt} is null
         */
        public ChecksDetailsBuilder withCompletedAt(final LocalDateTime completedAt) {
            this.completedAt = requireNonNull(completedAt);
            return this;
        }

        /**
         * Set the output of a check.
         *
         * @param output
         *         an output of a check
         * @return this builder
         * @throws NullPointerException if the {@code outputs} is null
         */
        public ChecksDetailsBuilder withOutput(final ChecksOutput output) {
            this.output = new ChecksOutput(requireNonNull(output));
            return this;
        }

        /**
         * Set the actions of a check.
         *
         * @param actions
         *         a list of actions
         * @return this builder
         * @throws NullPointerException if the {@code actions} is null
         */
        public ChecksDetailsBuilder withActions(final List<ChecksAction> actions) {
            requireNonNull(actions);
            this.actions = Collections.unmodifiableList(
                    actions.stream()
                            .map(ChecksAction::new)
                            .collect(Collectors.toList())
            );
            return this;
        }

        /**
         * Actually build the {@code ChecksDetail}.
         *
         * @return the built {@code ChecksDetail}
         * @throws IllegalArgumentException if {@code conclusion} is null when {@code status} is {@code completed}
         */
        public ChecksDetails build() {
            if (conclusion == ChecksConclusion.NONE) {
                if (startedAt == null) {
                    startedAt = LocalDateTime.now();
                }

                if (status == ChecksStatus.COMPLETED) {
                    throw new IllegalArgumentException("conclusion must be set when status is completed");
                }

                if (completedAt != null) {
                    throw new IllegalArgumentException("conclusion must be set when completed at is provided");
                }
            } else {
                if (completedAt == null) {
                    completedAt = LocalDateTime.now();
                }
            }

            return new ChecksDetails(name, status, detailsURL, startedAt, conclusion, completedAt, output, actions);
        }
    }
}