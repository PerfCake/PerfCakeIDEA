package org.perfcake.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.perfcake.idea.util.Constants;

import javax.swing.*;

/**
 * Created by miron on 8.1.2014.
 */
public class PerfCakeModuleType extends ModuleType<PerfCakeModuleBuilder> {
    @NonNls
    private static final String ID = "PERFCAKE_MODULE";

    public PerfCakeModuleType() {
        super(ID);
    }

    public static PerfCakeModuleType getInstance() {
        return (PerfCakeModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public static boolean isOfType(Module module) {
        return get(module) instanceof PerfCakeModuleType;
    }


    @NotNull
    @Override
    public PerfCakeModuleBuilder createModuleBuilder() {
        return new PerfCakeModuleBuilder();
    }


    @NotNull
    @Override
    public String getName() {
        return "PerfCake Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "PerfCake scenario editor";
    }


    @Override
    public Icon getBigIcon() {
        return Constants.ICON_24P;
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return Constants.ICON_16P;
    }
}
