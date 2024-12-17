package sap.ass2.usergui;

import java.net.MalformedURLException;
import java.net.URI;
import sap.ass2.usergui.gui.UserGUI;
import sap.ass2.usergui.library.ApplicationAPI;
import sap.ass2.usergui.library.ApplicationImpl;

public class UserGuiLauncher {
    public static void main(String[] args) throws MalformedURLException {
        String apiGatewayUrl = "http://localhost:10000";
        ApplicationAPI application = new ApplicationImpl(URI.create(apiGatewayUrl).toURL());
        UserGUI gui = new UserGUI(application);
        gui.display();
    }
}
