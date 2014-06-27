package org.perfcake.idea.model;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.perfcake.model.Property;
import org.perfcake.model.Scenario;

/**
 * Created by miron on 22.4.2014.
 */
public class ReportingModel extends AbstractScenarioModel {
    public static final String REPORTER_PROPERTY = "reporter";
    public static final String PROPERTY_PROPERTY = "property";
    public static final String REPORTING_PROPERTY = "reporting";

    private static final Logger LOG = Logger.getInstance(ReportingModel.class);

    private Scenario.Reporting reporting;

    public ReportingModel(Scenario.Reporting reporting) {
        this.reporting = reporting;
    }

    /**
     *
     * @return PerfCake Reporting model intended for read only.
     */
    public Scenario.Reporting getReporting() {
        return reporting;
    }

    public void setReporting(Scenario.Reporting reporting) {
        Scenario.Reporting old = this.reporting;
        this.reporting = reporting;
        fireChangeEvent(REPORTING_PROPERTY, old, reporting);
    }

    /**
     * Adds new reporter at a specified position given by the index. Existent reporters in this position will be moved after this reporter.
     *
     * @param index    at which should the reporter be placed
     * @param reporter to add
     */
    public void addReporter(int index, @NotNull Scenario.Reporting.Reporter reporter) {
        reporting.getReporter().add(index, reporter);
        fireChangeEvent(REPORTER_PROPERTY, null, reporter);
    }

    /**
     * Adds new reporter to the end.
     *
     * @param reporter to add
     */
    public void addReporter(@NotNull Scenario.Reporting.Reporter reporter) {
        reporting.getReporter().add(reporter);
        fireChangeEvent(REPORTER_PROPERTY, null, reporter);
    }

    /**
     * Deletes reporter object from this model. Reporter object should exist in this model.
     *
     * @param reporter reporter to delete
     */
    public void deleteReporter(@NotNull Scenario.Reporting.Reporter reporter) {
        boolean success = reporting.getReporter().remove(reporter);
        if (!success) {
            LOG.error(this.getClass().getName() + ": Reporter was not found in PerfCake JAXB model");
            return;
        }
        fireChangeEvent(REPORTER_PROPERTY, reporter, null);
    }

    /**
     * Adds new property at a specified position given by the index. Existent properties in this position will be moved after this property.
     *
     * @param index    at which should the property be placed
     * @param property to add
     */
    public void addProperty(int index, @NotNull Property property) {
        reporting.getProperty().add(index, property);
        fireChangeEvent(PROPERTY_PROPERTY, null, property);
    }

    /**
     * Adds new property to the end.
     *
     * @param property to add
     */
    public void addProperty(@NotNull Property property) {
        reporting.getProperty().add(property);
        fireChangeEvent(PROPERTY_PROPERTY, null, property);
    }

    /**
     * Deletes property object from this model.
     *
     * @param property property to delete
     */
    public void deleteProperty(@NotNull Property property) {
        boolean success = reporting.getProperty().remove(property);
        if (!success) {
            LOG.error(this.getClass().getName() + ": Property " + property.getName() + ":" + property.getValue() + " was not found in PerfCake JAXB model");
            return;
        }
        fireChangeEvent(PROPERTY_PROPERTY, property, null);
    }
}