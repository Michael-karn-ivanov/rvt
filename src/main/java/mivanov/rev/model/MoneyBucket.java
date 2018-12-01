package mivanov.rev.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "MoneyBucket")
public class MoneyBucket {
    @DatabaseField( generatedId = true, dataType = DataType.UUID_NATIVE )
    public UUID id;

    @DatabaseField(index = true)
    public String user;

    @DatabaseField
    public Double amount;

    public MoneyBucket() {}

    public MoneyBucket(String user, Double amount) {
        this.user = user;
        this.amount = amount;
    }
}
