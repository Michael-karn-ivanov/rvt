package mivanov.rev.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
public class User {
    @DatabaseField(id = true)
    public String Name;

    public Double Balance;

    public User() {}

    public User(String name) { this.Name = name; }
}
