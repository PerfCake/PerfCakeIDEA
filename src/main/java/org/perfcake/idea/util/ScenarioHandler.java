package org.perfcake.idea.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.perfcake.PerfCakeConst;
import org.perfcake.PerfCakeException;
import org.perfcake.model.Scenario;
import org.perfcake.util.Utils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

/**
 * Created by miron on 1.3.2014.
 */
public class ScenarioHandler {
    private static final Logger log = Logger.getInstance(ScenarioHandler.class);

    private URL scenarioURL;
    private Scenario scenarioModel;

    /**
     * Creates new ScenarioHandler with scenario on a given scenarioPath and loads the scenario into oldmodel
     *
     * @param scenarioPath absolute path of valid scenario
     * @throws PerfCakeException if scenario XML is not valid or cannot be successfully parsed
     */
    public ScenarioHandler(@NotNull String scenarioPath) throws PerfCakeException {
        //get an URL of a Scenario file
        try {
            scenarioURL = new File(scenarioPath).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new PerfCakeException("Scenario path cannot be resolved: " + scenarioPath, e);
        }
        //load Scenario XML to JAXB class
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(scenarioPath));
            String s = new String(encoded);
            scenarioModel = ScenarioUtil.parse(s);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PerfCakeIDEAException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates template scenario on a given scenarioPath and returns its ScenarioHandler
     *
     * @param scenarioPath new scenario path
     * @param overwrite    overwrites scenario if already exists
     * @return ScenarioHandler with a loaded scenario stored on scenarioPath
     * @throws FileAlreadyExistsException if the target scenarioPath already exists and overwrite is false
     * @throws PerfCakeIDEAException      if an error occures during template creation
     */
    @NotNull
    public static ScenarioHandler createFromTemplate(@NotNull String scenarioPath, boolean overwrite) throws FileAlreadyExistsException, PerfCakeIDEAException {
        Path scenarioTarget = Paths.get(scenarioPath);
        URL templateURL = ScenarioHandler.class.getResource("/ScenarioTemplate.xml");

        try (InputStream in = templateURL.openStream()) {
            if (overwrite) {
                Files.copy(in, scenarioTarget, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(in, scenarioTarget);
            }
            return new ScenarioHandler(scenarioPath);
        } catch (IOException | PerfCakeException e) {
            if (e instanceof FileAlreadyExistsException) {
                throw (FileAlreadyExistsException) e;
            }
            throw new PerfCakeIDEAException("Error while creating scenario from template", e);
        }
    }

    @NotNull
    public Scenario getScenarioModel() {
        return scenarioModel;
    }

    /**
     * Builds scenario for running from scenario oldmodel
     *
     * @return scenario to run
     * @throws PerfCakeIDEAException if scenario cannot be build
     */
    public org.perfcake.scenario.Scenario buildScenario() throws PerfCakeIDEAException {
        try {
            return ScenarioUtil.buildScenario(scenarioModel);
        } catch (Exception e) {
            throw new PerfCakeIDEAException("Cannot build scenario oldmodel", e);
        }
    }

    /**
     * Save current scenario oldmodel
     */
    public void save() {
        try {
            JAXBContext context = JAXBContext.newInstance(Scenario.class);
            final Marshaller marshaller = context.createMarshaller();

            String schemaFileName = "perfcake-scenario-" + PerfCakeConst.XSD_SCHEMA_VERSION + ".xsd";
            URL schemaUrl = getClass().getResource("/schemas/" + schemaFileName);
            if (schemaUrl != null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                try {
                    Schema schema = schemaFactory.newSchema(schemaUrl);
                    marshaller.setSchema(schema);
                } catch (SAXException e) {
                    log.warn("Scenario schema is not valid. Scenario saving continues without validation", e);
                }
            } else {
                log.warn("Could not get scenario schema: " + schemaFileName + ".Scenario reparsing continues without schema validation");
            }

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        marshaller.marshal(scenarioModel, new File(scenarioURL.toURI()));
                    } catch (JAXBException | URISyntaxException e) {
                        log.error("Error saving scenario", e);
                        PerfCakeIdeaUtil.showError(getFocusedProject(), "Error saving scenario", e);
                    }
                }
            });
        } catch (JAXBException e) {
            log.error("Error saving scenario", e);
            PerfCakeIdeaUtil.showError(getFocusedProject(), "Error saving scenario", e);
        }
    }

    /**
     * Reparses current oldmodel from scenarioString. Old oldmodel will be lost
     * @param scenarioString scenario string to reparse from
     * @return reparsed scenario oldmodel
     * @throws PerfCakeException
     */
    public org.perfcake.model.Scenario reparseFrom(String scenarioString) throws PerfCakeException {

        try {
            Source scenarioXML = new StreamSource(new ByteArrayInputStream(scenarioString.getBytes(Utils.getDefaultEncoding())));
            String schemaFileName = "perfcake-scenario-" + PerfCakeConst.XSD_SCHEMA_VERSION + ".xsd";

            JAXBContext context = JAXBContext.newInstance(org.perfcake.model.Scenario.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            URL schemaUrl = getClass().getResource("/schemas/" + schemaFileName);
            if (schemaUrl != null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                try {
                    Schema schema = schemaFactory.newSchema(schemaUrl);
                    unmarshaller.setSchema(schema);
                } catch (SAXException e) {
                    log.warn("Scenario schema is not valid. Scenario reparsing continues without schema validation", e);
                }
            } else {
                log.warn("Could not get scenario schema: " + schemaFileName + ".Scenario reparsing continues without schema validation");
            }
            scenarioModel = (org.perfcake.model.Scenario) unmarshaller.unmarshal(scenarioXML);
            return scenarioModel;
        } catch (JAXBException e) {
            throw new PerfCakeException("Cannot parse scenario from String: ", e);
        } catch (UnsupportedEncodingException e) {
            throw new PerfCakeException("PerfCake set encoding is not supported: ", e);
        }
    }

    @Nullable
    private Project getFocusedProject() {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager != null) {
            DataContext dataContext = dataManager.getDataContextFromFocus().getResult();
            return DataKeys.PROJECT.getData(dataContext);
        }
        return null;
    }
}
