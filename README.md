# openEHR Generator ![build](https://github.com/subigre/openehr-generator-maven-plugin/workflows/build/badge.svg) 

Maven plugin which can generate Java code from OPT templates
using [EHRbase openEHR SDK](https://github.com/ehrbase/openEHR_SDK).

## Usage

```xml

<plugin>
    <groupId>com.subiger.openehr</groupId>
    <artifactId>openehr-generator-maven-plugin</artifactId>
    <version>${openehr-generator-maven-plugin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>opt2java</goal>
            </goals>
            <configuration>
                <packageName>com.subiger.openehr.model</packageName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

```xml

<dependency>
    <groupId>com.github.ehrbase.openEHR_SDK</groupId>
    <artifactId>client</artifactId>
    <version>${openehr-sdk.version}</version>
</dependency>
```

## Configuration Parameters

| Name | Description |
| --- | --- |
| `generateChoiceForSingleEvent` | Whether or not to generate `Choice` for a single `EVENT`. If `false` only `POINT_EVENT` will be generated.<br /> **Default value:** `false` |
| `generateNullFlavor` | Whether or not to generate null flavor fields.<br /> **Default value:** `false` |
| `optimizerSetting` | Defines if nodes which belong to an archetype and are single valued generate a new class.<br /> **Default value:** `NONE` |
| `optRoot` | The directory where templates are stored.<br /> **Default value:** `${basedir}/src/main/resources/opt` |
| `packageName` | **Required** - The package under which the source files will be generated. |
| `sourceRoot` | The directory where the generated Java source files are created.<br /> **Default value:** ``${project.build.directory}/generated-sources/openehr`` |
