# SysML v2 Model Project

This project contains examples and models based on **SysML v2**, integrated with the **Pilot Implementation**. The main goal is system modeling using the latest language specifications within a Java environment.

## 📂 Project Structure

Below is the organization of the main directories:
- `src/main/resources/sysmlmodels/` : Central location for SysML v2 model files.
- `src/main/resources/application.properties` : Execution paths and configuration properties.
- `pom.xml` : Dependency management using Maven.

## ⚙️ Environment Setup

Follow the steps below to properly configure the development environment.
## 1. Prerequisites
- **JDK 23** installed and configured in: `Windows > Preferences > Java > Installed JREs` (in Eclipse).
- **SysML v2 Pilot Implementation** cloned and locally configured.

## 2. Properties Configuration

- Edit the file `src/main/resources/application.properties` with paths specific to your machine:
```properties 
# Absolute path to the project's model folder
app.baseFilePath=C:/Users/.../SysMLExecution/src/main/resources/sysmlmodels

# Absolute path to the Pilot Implementation standard library
app.systemLibPath=C:/Users/.../SySML-v2-Pilot-Implementation/sysml.library
```

## 3. GitHub Packages Authentication (settings.xml)

To download **SysML v2** and **OBP3** dependencies, Maven must be configured to access **GitHub Packages**:
- Generate a **Personal Access Token (classic)** on GitHub with the `read:packages` permission.
- Locate or create the `settings.xml` file at: `C:\Users\YOUR_USER\.m2\`
- Add the server configuration with your credentials:
```XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>github-sysml</id>
            <username>YOUR_GITHUB_USER</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>

        <server>
            <id>github-obp3</id>
            <username>YOUR_GITHUB_USER</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>

</settings>
```

## 4. Project Update
After configuring `settings.xml` and `application.properties`:

- Remove the `sysml-lib3.jar` file from the `/lib` folder (if it exists).
- In Eclipse, right-click the project.
- Select `Maven > Update Project...`.
- Enable **Force Update of Snapshots/Releases**.
- Click **OK**.

If `pom.xml` schema/dependency errors occur, performing a Maven update usually resolves the issue.
