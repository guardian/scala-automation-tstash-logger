# Teststash-logger
- To use this just edit your build.sbt as follows:
```
libraryDependencies ++= Seq(
  "com.gu" %% "teststash-logger" % "1.0"
)
```

# Adding Test-Stash logging to your project
- Define your project name in your project config:
```
"projectName" : "Test Project"
"plugin": {
    "teststash" : { "url" : "54.77.196.196:9000" }
}
```

- In logback.xml add TstashLogger as an appender:
```
...
<appender name="TSTASH" class="com.gu.automation.api.TstashAppender">
</appender>

<root level="info">
    ...
    <appender-ref ref="TSTASH"/>
</root>
```
