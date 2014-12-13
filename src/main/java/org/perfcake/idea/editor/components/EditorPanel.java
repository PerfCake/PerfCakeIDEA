package org.perfcake.idea.editor.components;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by miron on 22.10.2014.
 */
public class EditorPanel extends JPanel {
    JPanel toolbarPanel;
    ScenarioPanel scenarioPanel;

    public EditorPanel(JPanel toolbarPanel, ScenarioPanel scenarioPanel) {
        super(new BorderLayout());

        this.toolbarPanel = toolbarPanel;
        this.scenarioPanel = scenarioPanel;

        //create resizable split pane for inner panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        //splitPane.setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));

        //set divider size
        //splitPane.setDividerSize(10);

        //set divider default location
        splitPane.setDividerLocation(200);

        //add panels to split pane
        splitPane.setLeftComponent(toolbarPanel);
        //put scenario panel in scrollpane to allow scrolling when scenario is big
        JBScrollPane jbScrollPane = new JBScrollPane(scenarioPanel);
        jbScrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jbScrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        splitPane.setRightComponent(jbScrollPane);

        //add split pane to editor
        add(splitPane, BorderLayout.CENTER);
    }

    public JPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public ScenarioPanel getScenarioPanel() {
        return scenarioPanel;
    }

}
