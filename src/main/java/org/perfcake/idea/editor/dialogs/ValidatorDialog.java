package org.perfcake.idea.editor.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;
import org.perfcake.idea.editor.dialogs.tables.PropertiesEditor;
import org.perfcake.idea.model.Validator;
import org.perfcake.idea.util.PerfCakeClassProvider;
import org.perfcake.idea.util.PerfCakeClassProviderException;

import javax.swing.*;
import java.awt.*;

/**
 * Created by miron on 16. 11. 2014.
 */
public class ValidatorDialog extends MyDialogWrapper {
    private static final Logger LOG = Logger.getInstance(ValidatorDialog.class);

    private JComboBox validatorComboBox;
    private JTextField idTextField;
    private PropertiesEditor propertiesEditor;
    private JPanel rootPanel;
    private Validator mockCopy;

    public ValidatorDialog(Component parent, Validator mockCopy, Mode mode) {
        super(parent, true);
        this.mockCopy = mockCopy;
        init();
        setTitle(mode == Mode.ADD ? "Add Validator" : "Edit Validator");
        idTextField.setText(mockCopy.getId().getStringValue());
    }

    private void createUIComponents() {
        PerfCakeClassProvider classProvider = new PerfCakeClassProvider();
        DefaultComboBoxModel validators = null;
        try {
            validators = new DefaultComboBoxModel<String>(classProvider.findValidators());
        } catch (PerfCakeClassProviderException e) {
            LOG.error("Error finding validators for ValidatorDialog ComboBox", e);
        }
        validatorComboBox = new ComboBox(validators);
        //set selected sender from model
        String modelValue = mockCopy.getClazz().getStringValue();
        validators.setSelectedItem(modelValue);

        propertiesEditor = new PropertiesEditor(mockCopy);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    public Validator getMockCopy() {
        mockCopy.getClazz().setStringValue((String) validatorComboBox.getSelectedItem());
        mockCopy.getId().setStringValue(idTextField.getText());
        //properties are already set to mockCopy by properties editor
        return mockCopy;
    }

    @Override
    public ValidationInfo doValidate() {
        if (validatorComboBox.getSelectedIndex() == -1) {
            return new ValidationInfo("Please specify validator.", validatorComboBox);
        }
        if (idTextField.getText().trim().isEmpty()) {
            return new ValidationInfo("Id can't be empty.", idTextField);
        }
        return null;
    }
}
