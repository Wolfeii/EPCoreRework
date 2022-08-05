# EPCore - Reworked

## Developer Information

EPCore (Rework) is a core library that helps Developers efficiently and faster write their code for plugins to the prison server EmeraldPrison. This core will be continuously updated in order to keep up with the latest features and demand by players on the server.

## Command System
EPCore gives the Developer the ability to create commands in an easier and more straight forward way than previous systems. Here is an example that shows how to teleport a player:

```java
public class TeleportCommand extends Command {
    
}
```

## Database System
EPCore has an advanced Database system that allows Developers to easily execute SQL statements, without having to edit your code because you missed a comma or something. Here is an example for creating a table with EPCore's SQL system. In order to execute database queries to your plugins own database, you will need to initialize the Database Manager in your plugin, along with an `SQL.yml` file.

#### Setup

Create an `sql.yml` file in your plugins data folder, and then the  Database Manager should automatically be able to read it when initializing it.
```yaml
# Two options here, either MySQL or SQLite (not case-sensitive)
type: "MySQL"

settings:
  sqlite:
    # The file created for the database. Adding the .db in the end is optional and doesn't really matter.
    file-name: "database"
    username: "username"
    password: "password"
  mysql:
    # The database name for this plugin.
    database: "database"
    hostname: "127.0.0.1"
    username: "username"
    password: "password"
    port: 3306
```

After creating the file, you can initialize the Database Manager like this:

```java
public class Main extends JavaPlugin {
    
    private final DatabaseManager databaseManager = new DatabaseManager(this); // this = your plugin main class
    
    @Override
    public void onEnable() {
        if (!databaseManager.setup()) {
            // We couldn't set up the Database Manager for this plugin.
            getLogger().log(LogLevel.FATAL, "Database Manager failed to set up for " + getPlugin().getName() + ", disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Now your Database Manager has been set up, and you can start executing queries using it.
    }
}
```
#
#### Creating Tables
It is really simple creating a table with the new improved TableBuilder. The biggest difference is that we no longer have a unique class for each table. Instead, to create a table, use the TableBuilder like below: 

```java
TableBuilder.newTable("tableName", databaseManager)
        .addColumn("columnName", SQLDataType.YOUR_DATA_TYPE, length, allowNull, SQLDefaultType.DEFAULT_TYPE, primary)
        .build();
```

A working example using this TableBuilder:
```java
TableBuilder.newTable("users", this)
        .addColumn("uuid", SQLDataType.VARCHAR, 32, false, SQLDefaultType.NO_DEFAULT, true)
        .addColumn("name", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
        .build();
```

There are currently four different SQLDefaultTypes, and those are
- `NO_DEFAULT` - no default at all.
- `AUTO_INCREMENT` - will make this column an auto increment column.
- `NULL` - will set the default to null for this column.
- `CUSTOM` - custom default value. You can set the value with SQLDefaultType.CUSTOM.setCustom(value)
#

#### Updating Tables
This code can be used by Developers to update a table:
```java
// Specify the data to update, so each entry's represents the column we want to update, and the value is the 
// new value we want to set it to.
HashMap<String, Object> data = new HashMap<>() {{
    put("key", value);
    put("key", value);
}};

databaseManager.update(
        "tableName",
        data,
        // The WHERE data, so basically for each entry, it translates to WHERE KEY=VALUE AND KEY=VALUE etc... 
        new HashMap<>() {{
            put("key", value);
            put("key", value);
        }}
);
```
#
#### Inserting into Tables
This code can be used by Developers to insert data into a table.
This will return true if it successfully inserted the data, otherwise false. For example, it will return false if there is already an entry with this primary key.

```java
// Specify the data to insert, here we specify all the values for each columns, where each entry contains the key
// and the value for the column.
HashMap<String, Object> data = new HashMap<>() {{
    put("key", value);
    put("key", value);
}};

boolean successfullyInserted = databaseManager.insert("tableName", data);
```

#
#### Removing from Tables
This code can be used by Developers to remove data from a table.
This will return true if it successfully removed the data, otherwise false. For example, it will return false if there is no data with the specified where data.

```java
// The WHERE data, so basically for each entry, it translates to WHERE KEY=VALUE AND KEY=VALUE etc...
HashMap<String, Object> whereData = new HashMap<>() {{
    put("key", value);
    put("key", value);
}};

boolean successfullyInserted = databaseManager.insert("tableName", whereData);
```
