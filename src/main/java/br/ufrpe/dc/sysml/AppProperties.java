package br.ufrpe.dc.sysml;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String systemlibpath;
    private String baseFilePath;

    public String getSystemlibpath() {
        return systemlibpath;
    }

    public void setSystemlibpath(String systemlibpath) {
        this.systemlibpath = systemlibpath;
    }

    public String getBaseFilePath() {
        return baseFilePath;
    }

    public void setBaseFilePath(String baseFilePath) {
        this.baseFilePath = baseFilePath;
    }
}
