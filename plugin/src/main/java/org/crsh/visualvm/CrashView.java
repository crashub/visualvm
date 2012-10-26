package org.crsh.visualvm;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;

import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashView extends DataSourceView {

  private final CrashSwingController controller;

  public CrashView(Application application) {
    super(
        application,
        "Crash",
        Resources.ICON.asImage(),
        60,
        false);

    //
    this.controller = new CrashSwingController(application);
    this.controller.initUI();

  }

  @Override
  protected DataViewComponent createComponent() {

    //
    DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Crash", null, controller.getPane());
    DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(false);

    //
    DataViewComponent dvc = new DataViewComponent(masterView, masterConfiguration);
    dvc.setBackground(Color.BLACK);

    //
    dvc.setLayout(new BorderLayout());
    dvc.add(controller.getPane(), BorderLayout.CENTER);
    return dvc;

  }

}