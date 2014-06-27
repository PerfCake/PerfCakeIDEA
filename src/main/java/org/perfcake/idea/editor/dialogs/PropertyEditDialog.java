package org.perfcake.idea.editor.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by miron on 21.5.2014.
 */
public class PropertyEditDialog extends DialogWrapper {
    private JTextField nameTextField;
    private JTextField valueTextField;
    private JPanel rootPanel;

    public PropertyEditDialog(@Nullable Project project, String initialName, String initialValue) {
        super(project);
        init();
        setTitle("Edit Property");
        nameTextField.setText(initialName);
        valueTextField.setText(initialValue);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    public String getNameText() {
        return nameTextField.getText();
    }

    public String getValueText() {
        return valueTextField.getText();
    }

    protected ValidationInfo doValidate() {
        /*if (getNameText().trim().isEmpty()) {
            return new ValidationInfo("Please specify property name", nameTextField);
        }
        if (getValueText().trim().isEmpty()) {
            return new ValidationInfo("Please specify property value", valueTextField);
        }*/
        return null;
    }
}