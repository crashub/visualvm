package org.crsh.visualvm;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import org.crsh.visualvm.listener.*;

import javax.swing.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashView extends DataSourceView {

  private final CrashSwingController controller;

  public CrashView(Application application) {
    super(
        application,
        "Crash",
        new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("org/crsh/image/icon.png")).getImage(),
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
    dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration("Terminal", true), DataViewComponent.BOTTOM_LEFT);
    dvc.addDetailsView(new DataViewComponent.DetailsView("Terminal", null, 10, controller.getPane(), null), DataViewComponent.BOTTOM_LEFT);
    dvc.addAncestorListener(new InitFocusListener(controller));

    //
    return dvc;

  }

}