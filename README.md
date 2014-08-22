hippo-intellij-freemarker
=========================


## INSTALLATION INSTRUCTIONS


go to:

```
Intellij> settings > plugins > Install plugin from disk
```
and choose select folowing file (click to download):
[HippoFreemarkerEditor.zip](https://github.com/machak/hippo-intellij-freemarker/raw/master/HippoFreemarkerEditor.zip)

## SETUP *)

go to:

Intellij> settings > Hippo Freemarker editor
Default values:

```
username: admin
password: admin
freemarker root dir: site/src/main/webapp/WEB-INF/ftl
rmi: rmi://localhost:1099/hipporepository

```

# USAGE

Fetch all templates:
```
File  > Load templates from Hippo repository
```
After editing (or adding a new template):

```
File >  Save to Hippo repository
```





# *) RMI

NOTE: you need to enable RMI port within conf/context.xml by adding following lines:
```
<Parameter name="start-remote-server" value="true" override="false"/>
<Parameter name="repository-address" value="rmi://127.0.0.1:1099/hipporepository" override="false"/>
```
